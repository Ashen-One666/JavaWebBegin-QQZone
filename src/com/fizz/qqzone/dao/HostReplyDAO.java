package com.fizz.qqzone.dao;

import com.fizz.qqzone.pojo.HostReply;

public interface HostReplyDAO {
    // 根据reply的id查询关联的hostReply实体
    HostReply getHostReplyByReplyId(Integer replyId);
    // 根据hostReply的id查询
    HostReply getHostReply(Integer id);
    // 添加主人回复
    void addHostReply(HostReply hostReply);
    // 删除主人回复
    void delHostReply(Integer id);
    // 根据reply的id删除特定的主人回复
    void delHostReplyByReplyId(Integer replyId);
}
