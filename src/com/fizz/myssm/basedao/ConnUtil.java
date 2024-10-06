package com.fizz.myssm.basedao;

import com.alibaba.druid.pool.DruidDataSourceFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.sql.DataSource;
import java.util.Properties;


public class ConnUtil {

    private static ThreadLocal<Connection> threadLocal = new ThreadLocal<>();

    private static DataSource dataSource;

    // 使用德鲁伊数据库连接池
    static {
        // 注意：使用druid连接池时，jdbc.properties里的key名称必须要符合规范
        InputStream is = ConnUtil.class.getClassLoader().getResourceAsStream("jdbc.properties");
        Properties properties = new Properties();
        try {
            properties.load(is);
            dataSource = DruidDataSourceFactory.createDataSource(properties);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private static Connection createConn(){
        try {
            //1.加载驱动
            //Class.forName(DRIVER);
            //2.通过驱动管理器获取连接对象
            //return DriverManager.getConnection(URL, USER, PWD);

            return dataSource.getConnection();

        } catch (/*ClassNotFoundException | */SQLException e) {
            e.printStackTrace();
        }
        return null ;
    }


    public static Connection getConn() {
        Connection conn = threadLocal.get();
        if (conn == null) {
            conn = createConn();
            threadLocal.set(conn);
        }
        return threadLocal.get();
    }

    public static void closeConn() throws SQLException {
        Connection conn = threadLocal.get();
        if (conn == null) {
            return;
        }
        if (!conn.isClosed()) {
            conn.close();
            threadLocal.remove();
        }
    }
}
