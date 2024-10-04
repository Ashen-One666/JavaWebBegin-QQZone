package com.fizz.qqzone.service.impl;

import com.fizz.qqzone.dao.UserBasicDAO;
import com.fizz.qqzone.pojo.UserBasic;
import com.fizz.qqzone.service.UserBasicService;

import java.util.Collections;
import java.util.List;

public class UserBasicServiceImpl implements UserBasicService {

    private UserBasicDAO userBasicDAO = null;

    @Override
    public UserBasic login(String loginId, String pwd) {
        return userBasicDAO.getUserBasic(loginId, pwd);
    }

    @Override
    public List<UserBasic> getFriendList(UserBasic userBasic) {
        return userBasicDAO.getFriendList(userBasic);
    }

    @Override
    public UserBasic getUserBasicById(Integer id) {
        return userBasicDAO.getUserBasicById(id);
    }
}
