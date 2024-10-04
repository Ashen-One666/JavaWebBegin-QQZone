package com.fizz.qqzone.controller;

import com.fizz.qqzone.pojo.Reply;
import com.fizz.qqzone.pojo.Topic;
import com.fizz.qqzone.pojo.UserBasic;
import com.fizz.qqzone.service.ReplyService;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

public class ReplyController {

    private ReplyService replyService;

    public String addReply(String content, Integer topicId, HttpSession session) {
        UserBasic author = (UserBasic) session.getAttribute("userBasic");
        Reply reply = new Reply(content, LocalDateTime.now(), author, new Topic(topicId));
        replyService.addReply(reply);

        // 更新之后的跳转需要重定向才能将更新后的信息显示在页面上
        return "redirect:topic.do?operate=topicDetail&id="+topicId;
    }
}
