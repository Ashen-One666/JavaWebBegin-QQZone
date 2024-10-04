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
    public void addTopic(Topic topic) {

    }

    @Override
    public void delTopic(Topic topic) {

    }

    @Override
    public Topic getTopic(Integer id) {
        return super.load("select * from t_topic where id = ?", id);
    }
}
