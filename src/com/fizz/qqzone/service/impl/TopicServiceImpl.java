package com.fizz.qqzone.service.impl;

import com.fizz.qqzone.dao.TopicDAO;
import com.fizz.qqzone.pojo.Reply;
import com.fizz.qqzone.pojo.Topic;
import com.fizz.qqzone.pojo.UserBasic;
import com.fizz.qqzone.service.ReplyService;
import com.fizz.qqzone.service.TopicService;
import com.fizz.qqzone.service.UserBasicService;

import java.util.Collections;
import java.util.List;

public class TopicServiceImpl implements TopicService {

    private TopicDAO topicDAO = null;
    // 此处引用replyService而不是replyDAO
    private ReplyService replyService = null;
    private UserBasicService userBasicService = null;

    @Override
    public List<Topic> getTopicList(UserBasic userBasic) {
        return topicDAO.getTopicList(userBasic);
    }

    @Override
    public Topic getTopicById(Integer id) {
        Topic topic = topicDAO.getTopic(id);

        // 在Service层将topic的author信息补全
        UserBasic author = topic.getAuthor();
        author = userBasicService.getUserBasicById(author.getId());
        topic.setAuthor(author);

        System.out.println("my topic is : " + topic.getId());

        List<Reply> replyList = replyService.getReplyListByTopicId(topic.getId());
        topic.setReplyList(replyList);
        return topic;
    }
}
