function delTopic(topicId){
    if(window.confirm("是否确认删除日志？")){
        window.location.href="topic.do?operate=delTopic&topicId="+topicId;
    }
}