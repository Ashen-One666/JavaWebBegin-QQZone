package com.fizz.qqzone.controller;

import com.fizz.qqzone.pojo.Topic;
import com.fizz.qqzone.pojo.UserBasic;
import com.fizz.qqzone.service.TopicService;
import com.fizz.qqzone.service.UserBasicService;

import javax.servlet.http.HttpSession;
import java.util.List;

public class UserController {

    private UserBasicService userBasicService;
    private TopicService topicService;

    // 1. 登录验证：需要获取用户信息、用户好友列表、用户日志列表
    public String login(String loginId, String pwd, HttpSession session) {
        UserBasic userBasic = userBasicService.login(loginId, pwd);
        if (userBasic != null) {
            // 1-1 获取相关好友信息
            List<UserBasic> friendList = userBasicService.getFriendList(userBasic);
            // 1-2 获取相关日志信息
            List<Topic> topicList = topicService.getTopicList(userBasic);

            userBasic.setFriendList(friendList);
            userBasic.setTopicList(topicList);

            // userBasic这个key保存的是登录者的信息
            // friend这个key保存的是当前进入的是谁的空间
            session.setAttribute("userBasic", userBasic);
            session.setAttribute("friend", userBasic);

            return "index";
        }
        else {
            // 登录失败回到登录页面
            return "login";
        }
    }

    //
    public String friend(Integer id, HttpSession session) {
        // 根据id获取指定的用户信息
        UserBasic currentFriend = userBasicService.getUserBasicById(id);

        List<Topic> topicList = topicService.getTopicList(currentFriend);
        currentFriend.setTopicList(topicList);

        session.setAttribute("friend", currentFriend);

        return "index";
    }
}
