package com.fizz.myssm.filters;

import com.fizz.myssm.trans.TransactionManager;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;
import java.sql.SQLException;

@WebFilter("*.do")
public class OpenSessionInViewFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try{
            TransactionManager.beginTrans();
            // System.out.println("开启事务...");
            filterChain.doFilter(servletRequest, servletResponse); // 如果Servlet中Service的某个DAO有问题就catch回滚
            TransactionManager.commit();
            // System.out.println("提交事务...");
        }catch (Exception e){
            e.printStackTrace();
            try {
                TransactionManager.rollback();
                // System.out.println("回滚事务...");
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }

    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
