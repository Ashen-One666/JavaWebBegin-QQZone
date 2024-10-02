package com.fizz.qqzone.dao.impl;

import com.fizz.myssm.basedao.BaseDAO;
import com.fizz.qqzone.dao.UserBasicDAO;
import com.fizz.qqzone.pojo.UserBasic;

import java.util.List;

public class UserBasicDAOImpl extends BaseDAO<UserBasic> implements UserBasicDAO {
    @Override
    public UserBasic getUserBasic(String loginId, String pwd) {
        return super.load("select * from t_user_basic where loginId=? and pwd=?", loginId, pwd);
    }

    @Override
    public List<UserBasic> getFriendList(UserBasic userBasic) {
        // 连接查询 (注意这里用户不能查询到好友的密码)
        String sql = "select t1.id, t1.loginId, t1.nickname, t1.headImg\n" +
                        "from t_user_basic t1\n" +
                        "join t_friend t2 ON t1.id = t2.fid\n" +
                        "where t2.uid = ?";
        return super.executeQuery(sql, userBasic.getId());
    }

    @Override
    public UserBasic getUserBasicById(Integer id) {
        return super.load("select * from t_user_basic where id=?", id);
    }
}
