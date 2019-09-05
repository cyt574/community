function post() {
    var questionId = $("#question_id").val();
    var commentContent = $("#comment_content").val();

    if(!commentContent) {
        alert('不能回复空内容！！！');
        return;
    }

    $.ajax({
        type: "POST",
        url: "/comment",
        data: JSON.stringify({
            "parentId": questionId,
            "content": commentContent,
            "type": 1
        }),
        success: function (resp) {
            if (resp.code == 200) {
                location.reload();
            } else if (resp.code == 2003) {
                var isAccepted = confirm(resp.message);
                if (isAccepted) {
                    window.open("https://github.com/login/oauth/authorize?client_id=1352e765cd2b5821e6a7&redirect_uri=http://localhost:8087/callback&scope=user&state=1");
                    window.localStorage.setItem("closable", "true");
                }
            } else {
                alert(resp.message);
            }
            console.log(resp)
        },
        dataType: "json",
        contentType: "application/json"
    })
}