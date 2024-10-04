package com.fizz.qqzone.service;

import com.fizz.qqzone.pojo.Reply;

import java.util.List;

public interface ReplyService {
    // 根据topic的id获取关联的所有回复
    List<Reply> getReplyListByTopicId(Integer topicId);

    // 根据reply的id获取特定的reply信息
    Reply getReplyById(Integer id);

    // 添加回复
    void addReply(Reply reply);
}
