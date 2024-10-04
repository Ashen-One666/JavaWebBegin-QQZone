package com.fizz.qqzone.service;

import com.fizz.qqzone.pojo.UserBasic;

import java.util.List;

public interface UserBasicService {

    // 根据账号和密码返回用户信息
    public UserBasic login(String loginId, String pwd);

    // 根据用户信息返回该用户的好友列表
    public List<UserBasic> getFriendList(UserBasic userBasic);

    // 根据用户id获取制定用户信息
    public UserBasic getUserBasicById(Integer id);
}
