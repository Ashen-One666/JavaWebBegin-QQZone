package com.fizz.qqzone.pojo;

// 日志里面topicDate的Date需要年月日时分秒
import com.fizz.qqzone.dao.UserBasicDAO;
import com.fizz.qqzone.dao.impl.UserBasicDAOImpl;

import java.time.LocalDateTime;
import java.util.List;

public class Topic {
    private Integer id;
    private String title;
    private String content;
    private LocalDateTime topicDate;

    private UserBasic author;       // M : 1

    private List<Reply> replyList;  // 1 : N

    public Topic(){}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTopicDate() {
        return topicDate;
    }

    public void setTopicDate(LocalDateTime topicDate) {
        this.topicDate = topicDate;
    }

    public UserBasic getAuthor() {
        return author;
    }

    public void setAuthor(UserBasic author) {
        //System.out.println(author);
        this.author = author;
    }
    public void setAuthor(Integer author) {
        UserBasicDAO userBasicDAO = new UserBasicDAOImpl();
        this.author = userBasicDAO.getUserBasicById(author);
    }
    public List<Reply> getReplyList() {
        return replyList;
    }

    public void setReplyList(List<Reply> replyList) {
        this.replyList = replyList;
    }
}
