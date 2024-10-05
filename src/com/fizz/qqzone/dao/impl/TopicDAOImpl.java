package com.fizz.qqzone.dao.impl;

import com.fizz.myssm.basedao.BaseDAO;
import com.fizz.qqzone.dao.TopicDAO;
import com.fizz.qqzone.pojo.Topic;
import com.fizz.qqzone.pojo.UserBasic;

import java.util.List;

public class TopicDAOImpl extends BaseDAO<Topic> implements TopicDAO {
    @Override
    public List<Topic> getTopicList(UserBasic userBasic) {
        List<Topic> topicList = super.executeQuery("select * from t_topic where author = ?", userBasic.getId());
        return topicList;
    }

    @Override
    public Topic addTopic(Topic topic) {
        super.executeUpdate("insert into t_topic values(0,?,?,?,?)", topic.getTitle(), topic.getContent(), topic.getTopicDate(), topic.getAuthor().getId());
        return super.load("select * from t_topic where id = last_insert_id()");
    }

    @Override
    public void delTopic(Topic topic) {
        super.executeUpdate("delete from t_topic where id = ?", topic.getId());
    }

    @Override
    public Topic getTopic(Integer id) {
        return super.load("select * from t_topic where id = ?", id);
    }
}
