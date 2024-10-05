package com.fizz.qqzone.service.impl;

import com.fizz.qqzone.dao.HostReplyDAO;
import com.fizz.qqzone.pojo.HostReply;
import com.fizz.qqzone.pojo.UserBasic;
import com.fizz.qqzone.service.HostReplyService;
import com.fizz.qqzone.service.UserBasicService;

public class HostReplyServiceImpl implements HostReplyService {

    private HostReplyDAO hostReplyDAO;
    private UserBasicService userBasicService;

    @Override
    public HostReply getHostReplyByReplyId(Integer replyId) {
        HostReply hostReply = hostReplyDAO.getHostReplyByReplyId(replyId);

        // hostReply的reply属性只有id值

        // 在Service层将hostReply的author信息补全
        if (hostReply == null) {return hostReply;}
        UserBasic author = hostReply.getAuthor();
        author = userBasicService.getUserBasicById(author.getId());
        hostReply.setAuthor(author);

        return hostReply;
    }

    @Override
    public void addHostReply(HostReply hostReply) {
        hostReplyDAO.addHostReply(hostReply);
    }

    @Override
    public void delHostReply(Integer id) {
        hostReplyDAO.delHostReply(id);
    }

    @Override
    public void delHostReplyByReplyId(Integer replyId) {
        hostReplyDAO.delHostReplyByReplyId(replyId);
    }
}
