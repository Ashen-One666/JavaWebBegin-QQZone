package com.fizz.qqzone.dao;

import com.fizz.qqzone.pojo.HostReply;

public interface HostReplyDAO {
    // 根据reply的id查询关联的hostReply实体
    HostReply getHostReplyByReplyId(Integer replyId);
    // 添加主人回复
    void addHostReply(HostReply hostReply);
}
