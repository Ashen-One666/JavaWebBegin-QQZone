package com.fizz.qqzone.controller;

import com.fizz.qqzone.pojo.Topic;
import com.fizz.qqzone.service.TopicService;

import javax.servlet.http.HttpSession;

public class TopicController {

    private TopicService topicService;

    public String topicDetail(Integer id, HttpSession session) {
        // 1. 已知topic的id，需要根据topic的id获取特定topic
        // 2. 获取这个topic关联的所有回复(已封装到业务方法topicService中)
        // 3. 如果某个回复有主人回复，需要查询出来(已封装到业务方法replyService中,然后topicService调用该方法并将主人回复赋给topic)
        Topic topic = topicService.getTopicById(id);

        session.setAttribute("topic", topic);
        return "frames/detail";
    }
}
