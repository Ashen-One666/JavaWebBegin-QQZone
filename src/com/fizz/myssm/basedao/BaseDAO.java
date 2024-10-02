package com.fizz.myssm.basedao;

import java.lang.reflect.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseDAO<T> {
/*
    public final String DRIVER = "com.mysql.cj.jdbc.Driver" ;
    public final String URL = "jdbc:mysql://localhost:3306/qqzonedb2?serverTimezone=GMT&useUnicode=true&characterEncoding=utf-8&useSSL=false";
    public final String USER = "root";
    public final String PWD = "401128" ;
*/

    protected Connection conn ;
    protected PreparedStatement psmt ;
    protected ResultSet rs ;

    //T的Class对象
    private Class entityClass ;

    public BaseDAO(){
        //getClass() 获取Class对象，当前我们执行的是new FruitDAOImpl() , 创建的是FruitDAOImpl的实例
        //那么子类构造方法内部首先会调用父类（BaseDAO）的无参构造方法
        //因此此处的getClass()会被执行，但是getClass获取的是FruitDAOImpl的Class
        //所以getGenericSuperclass()获取到的是BaseDAO的Class
        Type genericType = getClass().getGenericSuperclass();
        //ParameterizedType 参数化类型
        Type[] actualTypeArguments = ((ParameterizedType) genericType).getActualTypeArguments();
        //获取到的<T>中的T的真实的类型
        Type actualType = actualTypeArguments[0];
        try {
            entityClass = Class.forName(actualType.getTypeName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            // 将DAO的异常封装并向上抛出，让Servlet捕获处理
            // (如果DAO只是自己try-catch却不向上抛异常，那么Servlet无法知道DAO出现异常，导致事务回滚时仅错误的那个事务回滚)
            // 这样事务回滚时同一个connection中的事务会全部回滚而非仅错误的那个事务回滚
            throw new DAOException("BaseDAO init error");
        }
    }

    protected Connection getConn(){
       /*
        try {
            //1.加载驱动
            Class.forName(DRIVER);
            //2.通过驱动管理器获取连接对象
            return DriverManager.getConnection(URL, USER, PWD);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return null ;
        */
        return ConnUtil.getConn() ;
    }

    protected void close(ResultSet rs , PreparedStatement psmt , Connection conn){
        // 所有DAO都使用同一个Connection，因此close现在不在DAO中执行，而是在事务处理过滤器中执行
        /*
        try {
            if (rs != null) {
                rs.close();
            }
            if(psmt!=null){
                psmt.close();
            }
            if(conn!=null && !conn.isClosed()){
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        */
    }

    //给预处理命令对象设置参数
    private void setParams(PreparedStatement psmt , Object... params) throws SQLException {
        if(params!=null && params.length>0){
            for (int i = 0; i < params.length; i++) {
                psmt.setObject(i+1,params[i]);
            }
        }
    }

    //执行更新，返回影响行数
    protected int executeUpdate(String sql , Object... params){
        boolean insertFlag = false ;
        insertFlag = sql.trim().toUpperCase().startsWith("INSERT");
        try {
            conn = getConn();
            if(insertFlag){
                psmt = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            }else {
                // 原来这里没有加 Statement.RETURN_GENERATED_KEYS
                psmt = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            }
            setParams(psmt,params);
            int count = psmt.executeUpdate() ;

            if(insertFlag){
                rs = psmt.getGeneratedKeys();
                if(rs.next()){
                    return ((Long)rs.getLong(1)).intValue();
                }
            }

            return count ;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException("BaseDAO executeUpdate error");
        }finally {
            close(rs,psmt,conn);
        }
        //return 0;
    }

    /*
    //通过反射技术给obj对象的property属性赋propertyValue值
    private void setValue(Object obj ,  String property , Object propertyValue){
        Class clazz = obj.getClass();
        try {
            //获取property这个字符串对应的属性名 ， 比如 "fid"  去找 obj对象中的 fid 属性
            Field field = clazz.getDeclaredField(property);
            if(field!=null){
                field.setAccessible(true);
                field.set(obj,propertyValue);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            throw new DAOException("BaseDAO setValue error");
        }
    }
    */

    // 重新写了一个 setValue
    // 优先反射调用参数类型完全匹配的 set 方法，如果找不到这样的 set 方法则直接反射赋值
    private void setValue(Object obj, String property, Object propertyValue) {
        Class<?> clazz = obj.getClass();
        try {
            // 先获取属性字段
            Field field = clazz.getDeclaredField(property);
            if (field != null) {
                Method setter = null;

                // 遍历所有的公共方法，查找与属性名称相关的setter方法
                for (Method method : clazz.getMethods()) {
                    // 匹配以set开头且参数个数为1的方法
                    if (method.getName().equalsIgnoreCase("set" + property) && method.getParameterCount() == 1) {
                        Class<?> paramType = method.getParameterTypes()[0];

                        // 检查参数类型是否与传入的propertyValue类型完全匹配
                        if (propertyValue != null && paramType.equals(propertyValue.getClass())) {
                            setter = method;
                            break;
                        }
                    }
                }

                // 如果找到了参数类型完全匹配的setter方法，则调用它
                if (setter != null) {
                    setter.setAccessible(true);
                    setter.invoke(obj, propertyValue);
                } else {
                    // 如果没有找到匹配的setter方法，直接通过反射给属性赋值
                    field.setAccessible(true);
                    field.set(obj, propertyValue);
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            throw new DAOException("Error setting value for property: " + property);
        }
    }

    //执行复杂查询，返回例如统计结果
    protected Object[] executeComplexQuery(String sql , Object... params){
        try {
            conn = getConn() ;
            psmt = conn.prepareStatement(sql);
            setParams(psmt,params);
            rs = psmt.executeQuery();

            //通过rs可以获取结果集的元数据
            //元数据：描述结果集数据的数据 , 简单讲，就是这个结果集有哪些列，什么类型等等

            ResultSetMetaData rsmd = rs.getMetaData();
            //获取结果集的列数
            int columnCount = rsmd.getColumnCount();
            Object[] columnValueArr = new Object[columnCount];
            //6.解析rs
            if(rs.next()){
                for(int i = 0 ; i<columnCount;i++){
                    Object columnValue = rs.getObject(i+1);     //33    苹果      5
                    columnValueArr[i]=columnValue;
                }
                return columnValueArr ;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException("BaseDAO executeComplexQuery error");
        } finally {
            close(rs,psmt,conn);
        }
        return null ;
    }

    //执行查询，返回单个实体对象
    protected T load(String sql , Object... params){
        try {
            conn = getConn() ;
            psmt = conn.prepareStatement(sql);
            setParams(psmt,params);
            rs = psmt.executeQuery();

            //通过rs可以获取结果集的元数据
            //元数据：描述结果集数据的数据 , 简单讲，就是这个结果集有哪些列，什么类型等等

            ResultSetMetaData rsmd = rs.getMetaData();
            //获取结果集的列数
            int columnCount = rsmd.getColumnCount();
            //6.解析rs
            if(rs.next()){
                T entity = (T)entityClass.newInstance();

                for(int i = 0 ; i<columnCount;i++){
                    String columnName = rsmd.getColumnName(i+1);            //fid   fname   price
                    Object columnValue = rs.getObject(i+1);     //33    苹果      5
                    setValue(entity,columnName,columnValue);
                }
                return entity ;
            }
        } catch (SQLException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
            throw new DAOException("BaseDAO load error");
        } finally {
            close(rs,psmt,conn);
        }
        return null ;
    }


    //执行查询，返回List
    protected List<T> executeQuery(String sql , Object... params){
        List<T> list = new ArrayList<>();
        try {
            conn = getConn() ;
            psmt = conn.prepareStatement(sql);
            setParams(psmt,params);
            rs = psmt.executeQuery();

            //通过rs可以获取结果集的元数据
            //元数据：描述结果集数据的数据 , 简单讲，就是这个结果集有哪些列，什么类型等等

            ResultSetMetaData rsmd = rs.getMetaData();
            //获取结果集的列数
            int columnCount = rsmd.getColumnCount();
            //6.解析rs
            while(rs.next()){
                T entity = (T)entityClass.newInstance();

                for(int i = 0 ; i < columnCount; i++){
                    // getColumnName获取的是列的原名
                    // getColumnLabel获取的是列的别名
                    // 如 "select fid as id from user" 前者获取到fid，后者获取到id
                    // 如果在调用该函数时使用类似上面的sql语句并希望获取别名，则需要将getColumnName改为getColumnLabel
                    String columnName = rsmd.getColumnName(i+1);            //fid   fname   price
                    Object columnValue = rs.getObject(i+1);     //33    苹果      5
                    //System.out.println();
                    setValue(entity, columnName, columnValue);
                }
                list.add(entity);
            }
        } catch (SQLException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
            throw new DAOException("BaseDAO executeQuery error");
        } finally {
            close(rs,psmt,conn);
        }
        return list ;
    }
}