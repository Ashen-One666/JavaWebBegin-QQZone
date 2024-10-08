package com.fizz.qqzone.pojo;

import com.fizz.qqzone.dao.UserBasicDAO;
import com.fizz.qqzone.dao.impl.UserBasicDAOImpl;

import java.time.LocalDateTime;
import java.util.Date;

public class HostReply {
    private Integer id;
    private String content;
    private LocalDateTime hostReplyDate;
    private UserBasic author;   // M : 1
    private Reply reply;        // 1 : 1

    public HostReply() {}

    public HostReply(Integer id) {
        this.id = id;
    }

    public HostReply(String content, LocalDateTime hostReplyDate, UserBasic author, Reply reply) {
        this.content = content;
        this.hostReplyDate = hostReplyDate;
        this.author = author;
        this.reply = reply;
    }

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

    public LocalDateTime getHostReplyDate() {
        return hostReplyDate;
    }

    public void setHostReplyDate(LocalDateTime hostReplyDate) {
        this.hostReplyDate = hostReplyDate;
    }

    public UserBasic getAuthor() {
        return author;
    }

    public void setAuthor(UserBasic author) {
        this.author = author;
    }

    public void setAuthor(Integer author) {
        this.author = new UserBasic(author);
    }

    public Reply getReply() {
        return reply;
    }

    public void setReply(Reply reply) {
        this.reply = reply;
    }

    public void setReply(Integer reply) {
        this.reply = new Reply(reply);
    }
}
