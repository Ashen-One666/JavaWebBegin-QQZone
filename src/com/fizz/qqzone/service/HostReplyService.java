package com.fizz.qqzone.service;

import com.fizz.qqzone.pojo.HostReply;

public interface HostReplyService {
    HostReply getHostReplyByReplyId(Integer replyId);

    void addHostReply(HostReply hostReply);
}
