package com.fizz.myssm.myspringmvc;

// PageController是通用代码，因此放在myspringmvc下
// 在Controller中，只要返回值不是"redirect:"，中央控制器都会用thymeleaf跳转
// PageController相当于是利用thymeleaf进行页面跳转(用thymeleaf进行页面渲染)
// page.do?operate=page&page=login 代表用thymeleaf渲染login.html页面
// (page.do -> PageController, operate=page -> page(), page=login -> return login)
// page.do?operate=page&page=frames/left 代表用thymeleaf渲染left.html页面
public class PageController {
    // 对应的index页面中operate = page
    public String page(String page){
        // 返回给中央控制器的service方法，里面调用processTemplate，后thymeleaf渲染frames/left.html
        return page; // frames/left
    }
}
