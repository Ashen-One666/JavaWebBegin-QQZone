package com.fizz.myssm.myspringmvc;

import com.fizz.myssm.ioc.BeanFactory;
import com.fizz.myssm.util.StringUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * DispatcherServlet是中央控制器，是真正的servlet，处理所有.do请求
 * 假设request的URL是： http://localhost:8080/fruit.do?operate=edit&fid=2
 * 1. 定位Controller：
 *      会从request中servletPath里面得到fruit.do，通过在xml配置文件里bean标签描述将fruit和fruitController对应关系
 *      再通过Document解析xml配置文件，将配置文件中每一对bean封装成beanMap中一个一个的对象
 *      这样当收到fruit请求后根据对应关系就能找到fruitController，此时就会去调用fruitController中的方法
 * 2. 调用Controller中的方法：
 *      调用fruitController的方法的过程是通过反射(method.invoke)实现的
 *      再将request中的参数operate=edit传入到该方法中，这样就能正确使用Controller里 edit方法了
 */

// 此处不需要加/，表示拦截所有以.do结尾的请求
@WebServlet("*.do")
public class DispatcherServlet extends ViewBaseServlet{

    // 框架就是： 注解 + 反射
    // 使用中央控制器DispatcherServlet的好处是不需要在每个controller中都写反射的代码，而是把这部分代码统一抽取到这里

    private BeanFactory beanFactory;

    // 在Servlet初始化函数中，解析xml配置文件
    public DispatcherServlet() {
    }

    public void init() throws ServletException {
        super.init();
        // 初始化的时候创建bean工厂，而不是实例化的时候再创建
        /*
           性能优化改进： bean工厂不在中央控制器初始化中创建，而是在ServletContext(Application)初始化时创建，
                       中央控制器在Application作用域中获取，这样初始化时间略长，但后续的响应时间缩短
           实现方法： 在监听器ContextLoaderListener中创建，在中央控制器获取
         */
        // beanFactory = new ClassPathXmlApplicationContext();
        ServletContext application = getServletContext();
        Object beanFactoryObj = application.getAttribute("beanFactory");
        if(beanFactoryObj != null) {
            beanFactory = (BeanFactory) beanFactoryObj;
        }
        else{
            throw new RuntimeException("IOC容器获取失败");
        }

        System.out.println("中央控制器初始化...");
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 在编码过滤器中设置编码

        /*
        * 假设URL： http://localhost:8080/fruit.do
        * 那么servletPath是： /fruit.do
        * 思路是：
        *   第一步： /fruit.do -> fruit (发送的/fruit.do转换成fruit)
        *   第二步： fruit -> fruitController (将fruit和fruitController对应上，如何对应 见上面的实例化方法)
        * */
        String servletPath = request.getServletPath();
        servletPath = servletPath.substring(1); // 去掉/
        int lastDotIndex = servletPath.lastIndexOf(".do");
        servletPath = servletPath.substring(0, lastDotIndex); // 去掉.do

        // 中央控制器初始化方法init()中已经通过DOM技术将对应关系存储在beanMap容器中了，这里直接根据请求fruit获取到FruitController
        Object controllerBeanObj = beanFactory.getBean(servletPath);

        String operate = request.getParameter("operate");
        // 初始化默认是index
        if(StringUtil.isEmpty(operate)){
            operate = "index";
        }

        // 通过反射技术优化代码，这样就不需要写多个switch分支对operate进行判断
        // 使用 getDeclaredMethod 获取指定operate的方法
        try {
            // 获取到/*.do中*对应的控制器，并根据operate调用该控制器中的方法(反射实现)
            // 如请求是/fruit.do，则bean标签的id=fruit，中央控制器就会去调用该id对应的class(FruitController)中的方法
            Method[] methods = controllerBeanObj.getClass().getDeclaredMethods();
            for (Method method : methods) {
                if(operate.equals(method.getName())){
                    // 1. 统一获取请求参数
                    // 获取当前方法的参数，返回参数数组
                    // 注：java8的新特性，在编译class文件时可以带上函数中的参数名称，设置后就可以method.getParameters()直接获取到参数名了
                    // 设置方法：settings -> java compiler -> additional command line parameters -> 写上 -parameters
                    Parameter[] parameters = method.getParameters();
                    Object[] parameterValues = new Object[parameters.length]; // 存放参数值
                    for (int i = 0; i < parameters.length; i++) {
                        Parameter parameter = parameters[i];
                        String parameterName = parameter.getName();
                        // 如果参数名是request, response, session，那么就不是通过请求中获取参数的方式了
                        if("request".equals(parameterName)){
                            parameterValues[i] = request;
                        }
                        else if("response".equals(parameterName)){
                            parameterValues[i] = response;
                        }
                        else if("session".equals(parameterName)){
                            parameterValues[i] = request.getSession();
                        }
                        else{
                            // 注：接收复选框(parameter同名但有多个值)的参数使用函数request.getParameterValue，会得到一个数组，本代码中不考虑这个
                            // 从请求中获取参数值
                            String parameterValue = request.getParameter(parameterName);
                            // 常见错误： IllegalArgumentException: argument type mismatch
                            // 即 如果直接赋值parameterValues[i] = parameterValue，那么当pageNo=2时，传进来的是 "2" 而不是 2
                            // 直接从request中拿到的参数类型都是String，因此需要根据controller中方法里参数类型进行转换
                            Object parameterObj = parameterValue;
                            String typeName = parameter.getType().getName();
                            if(parameterObj != null){
                                // 本项目除了String外中只用到了Integer类型的参数，如果有其他类型参数，需要把判断补全
                                if("java.lang.Integer".equals(typeName)){
                                    parameterObj = Integer.parseInt(parameterValue);
                                }
                            }
                            parameterValues[i] = parameterObj;

                        }
                    }

                    // 2. Controller组件中的方法调用
                    // controller中每个operate的方法都是private的，需要暴力反射
                    method.setAccessible(true);
                    // 调用Controller中的方法(根据operate获取的method),并获取返回值用来做视图处理(请求转发重定向等)
                    Object methodReturnObj = method.invoke(controllerBeanObj, parameterValues);

                    // 3. 视图处理
                    String methodReturnStr = methodReturnObj.toString();
                    // 如果Controller跳转是进行重定向
                    if(methodReturnStr.startsWith("redirect:")){ // 比如: redirect:fruit.do
                        String redirectStr = methodReturnStr.substring("redirect:".length());
                        response.sendRedirect(redirectStr);
                    }
                    // 如果Controller跳转是进行thymeleaf渲染
                    else{ // 比如: "edit"
                        super.processTemplate(methodReturnStr, request, response);
                    }
                }
            }

            /*
            else{
                throw new RuntimeException("operate值非法！");
            }*/
        } /*catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }*/ catch (Exception e) {
            e.printStackTrace();
            throw new DispatcherServletException("DispatcherServlet error");
            //throw new RuntimeException(e);
        }

    }

}

// 常见错误： IllegalArgumentException: argument type mismatch
//