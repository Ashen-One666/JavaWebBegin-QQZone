package com.fizz.qqzone.pojo;

import com.fizz.qqzone.dao.UserBasicDAO;
import com.fizz.qqzone.dao.impl.UserBasicDAOImpl;

import java.util.Date;

public class HostReply {
    private Integer id;
    private String content;
    private Date hostReplyTime;
    private UserBasic author;   // M : 1
    private Reply reply;        // 1 : 1

    public HostReply() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getHostReplyTime() {
        return hostReplyTime;
    }

    public void setHostReplyTime(Date hostReplyTime) {
        this.hostReplyTime = hostReplyTime;
    }

    public UserBasic getAuthor() {
        return author;
    }

    public void setAuthor(UserBasic author) {
        this.author = author;
    }

    public void setAuthor(Integer author) {
        UserBasicDAO userBasicDAO = new UserBasicDAOImpl();
        this.author = userBasicDAO.getUserBasicById(author);
    }

    public Reply getReply() {
        return reply;
    }

    public void setReply(Reply reply) {
        this.reply = reply;
    }
}
