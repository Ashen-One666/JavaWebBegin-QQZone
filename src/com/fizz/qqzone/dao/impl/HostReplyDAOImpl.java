package com.fizz.qqzone.dao.impl;

import com.fizz.myssm.basedao.BaseDAO;
import com.fizz.qqzone.dao.HostReplyDAO;
import com.fizz.qqzone.pojo.HostReply;

public class HostReplyDAOImpl extends BaseDAO<HostReply> implements HostReplyDAO {
    @Override
    public HostReply getHostReplyByReplyId(Integer replyId) {
        return super.load("select * from t_host_reply where reply=?", replyId);
    }

    @Override
    public HostReply getHostReply(Integer id) {
        return super.load("select * from t_host_reply where id=?", id);
    }

    @Override
    public void addHostReply(HostReply hostReply) {
        super.executeUpdate("insert into t_host_reply values(0,?,?,?,?)", hostReply.getContent(), hostReply.getHostReplyDate(), hostReply.getAuthor().getId(), hostReply.getReply().getId());
    }

    @Override
    public void delHostReply(Integer id) {
        super.executeUpdate("delete from t_host_reply where id=?", id);
    }

    @Override
    public void delHostReplyByReplyId(Integer replyId) {
        super.executeUpdate("delete from t_host_reply where reply=?", replyId);
    }
}
