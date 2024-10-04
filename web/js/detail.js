function showDelImg(imgId){
    var obj = document.getElementById(imgId) ;
    if(obj){
        obj.style.display='inline';
    }

}
function hiddenDelImg(imgId){
    var obj = document.getElementById(imgId) ;
    if(obj){
        obj.style.display='none';
    }
}
function delTopic(topicId){
    if(window.confirm("是否确认删除该日志？")){
        window.location.href='topic.do?operate=delTopic&topicId='+topicId;
    }
}
function delReply(replyId , topicId){
    if(window.confirm("是否确认删除该回复？")){
        window.location.href='reply.do?operate=delReply&replyId='+replyId+'&topicId='+topicId;
    }
}
function delHostReply(replyId , topicId){
    if(window.confirm("是否确认删除该主人回复？")){
        window.location.href='hostReply.do?operate=delHostReply&replyId='+replyId+'&topicId='+topicId;
    }
}

// 显示/隐藏输入框
function showReplyBox(replyId) {
    var replyBox = document.getElementById("div_add_host_reply_" + replyId);
    replyBox.style.display = (replyBox.style.display === "none") ? "block" : "none";

    // 点击空白处隐藏输入框
    document.addEventListener("click", function(event) {
        var isClickInside = replyBox.contains(event.target) || event.target.id === "a" + replyId;
        if (!isClickInside) {
            replyBox.style.display = "none";
        }
    });
}