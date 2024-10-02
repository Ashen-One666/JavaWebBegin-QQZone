package com.fizz.qqzone.dao;

import com.fizz.qqzone.pojo.UserBasic;

import java.util.List;

public interface UserBasicDAO {
    // 根据账号和密码获取特定用户信息
    public UserBasic getUserBasic(String loginId, String pwd);

    // 获取指定用户的所有好友列表
    public List<UserBasic> getFriendList(UserBasic userBasic);

    // 根据id获取特定用户信息
    public UserBasic getUserBasicById(Integer id);
}
