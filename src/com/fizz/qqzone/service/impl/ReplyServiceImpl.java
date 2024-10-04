package com.fizz.qqzone.service.impl;

import com.fizz.qqzone.dao.ReplyDAO;
import com.fizz.qqzone.pojo.HostReply;
import com.fizz.qqzone.pojo.Reply;
import com.fizz.qqzone.pojo.Topic;
import com.fizz.qqzone.pojo.UserBasic;
import com.fizz.qqzone.service.HostReplyService;
import com.fizz.qqzone.service.ReplyService;
import com.fizz.qqzone.service.TopicService;
import com.fizz.qqzone.service.UserBasicService;

import java.util.Collections;
import java.util.List;

public class ReplyServiceImpl implements ReplyService {

    private ReplyDAO replyDAO;
    // 此处引入的是其他POJO的Service接口，而不是DAO接口
    // 因为其他POJO对应的业务逻辑是封装在service层的，我需要调用别人的业务逻辑方法，而不需要深入考虑别人的内部细节
    private HostReplyService hostReplyService;
    private UserBasicService userBasicService;

    @Override
    public List<Reply> getReplyListByTopicId(Integer topicId) {
        List<Reply> replyList = replyDAO.getReplyList(new Topic(topicId));
        for (Reply reply : replyList) {
            // reply的topic属性只有id值

            // 在Service层将reply的author信息补全
            UserBasic author = reply.getAuthor();
            author = userBasicService.getUserBasicById(author.getId());
            reply.setAuthor(author);
            // 在Service层将reply的hostReply信息补全
            HostReply hostReply = hostReplyService.getHostReplyByReplyId(reply.getId());
            reply.setHostReply(hostReply);
        }
        return replyList;
    }

    @Override
    public Reply getReplyById(Integer id) {
        return replyDAO.getReplyById(id);
    }

    @Override
    public void addReply(Reply reply) {
        replyDAO.addReply(reply);
    }
}
