package com.fizz.qqzone.controller;

import com.fizz.qqzone.pojo.Topic;
import com.fizz.qqzone.pojo.UserBasic;
import com.fizz.qqzone.service.TopicService;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

public class TopicController {

    private TopicService topicService;

    public String topicDetail(Integer id, HttpSession session) {
        // 1. 已知topic的id，需要根据topic的id获取特定topic
        // 2. 获取这个topic关联的所有回复(已封装到业务方法topicService中)
        // 3. 如果某个回复有主人回复，需要查询出来(已封装到业务方法replyService中,然后topicService调用该方法并将主人回复赋给topic)
        Topic topic = topicService.getTopicById(id);

        session.setAttribute("topic", topic);
        return "frames/detail";
    }

    public String delTopic(Integer topicId) {
        topicService.delTopic(topicId);
        return "redirect:/topic.do?operate=getTopicList";
    }

    public String getTopicList(HttpSession session) {
        // 从session中获取当前用户信息
        UserBasic userBasic = (UserBasic) session.getAttribute("userBasic");
        // 再次查询当前用户关联的所有日志
        List<Topic> topicList = topicService.getTopicList(userBasic);
        // 更新关联的日志列表(之前session中关联的friend的topicList与当前数据库中的不一致)
        userBasic.setTopicList(topicList);
        // main页面是通过遍历friend的topicList来展示日志列表的，因此此处只需要覆盖friend的topicList就行了
        session.setAttribute("friend", userBasic);
        return "frames/main";
    }

    public String showNewTopicPage() {
        return "frames/newTopic";
    }

    public String addTopic(String title, String content, HttpSession session) {
        UserBasic userBasic = (UserBasic) session.getAttribute("userBasic");
        Topic topic = new Topic(title, content, LocalDateTime.now(), userBasic);
        Integer topicId = topicService.addTopic(topic).getId();
        System.out.println(topic.getId());
        return "redirect:topic.do?operate=topicDetail&id="+topicId;
    }
}
