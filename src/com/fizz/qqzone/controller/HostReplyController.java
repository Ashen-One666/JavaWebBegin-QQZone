package com.fizz.qqzone.controller;

import com.fizz.qqzone.pojo.HostReply;
import com.fizz.qqzone.pojo.Reply;
import com.fizz.qqzone.pojo.Topic;
import com.fizz.qqzone.pojo.UserBasic;
import com.fizz.qqzone.service.HostReplyService;
import com.fizz.qqzone.service.ReplyService;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

public class HostReplyController {

    private HostReplyService hostReplyService;

    public String addHostReply(String content, Integer topicId, Integer replyId, HttpSession session){
        UserBasic author = (UserBasic) session.getAttribute("userBasic");
        HostReply hostReply = new HostReply(content, LocalDateTime.now(), author, new Reply(replyId));
        hostReplyService.addHostReply(hostReply);

        return "redirect:topic.do?operate=topicDetail&id="+topicId;
    }
}
