package com.fizz.qqzone.dao;

import com.fizz.qqzone.pojo.Reply;
import com.fizz.qqzone.pojo.Topic;

import java.util.List;

public interface ReplyDAO {
    // 获取指定日志的回复列表
    public List<Reply> getReplyList(Topic topic);

    // 添加回复
    public void addReply(Reply reply);

    // 删除回复
    public void delReply(Integer id);

    // 获取指定回复
    public Reply getReplyById(Integer id);
}
