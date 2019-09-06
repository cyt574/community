/*
* Post Reply
* */
function post() {
    var questionId = $("#question_id").val();
    var commentContent = $("#comment_content").val();

    comment2target(questionId, 1, commentContent);
}

function comment2target(targetId, type, commentContent) {
    if (!commentContent) {
        alert('不能回复空内容！！！');
        return;
    }

    $.ajax({
        type: "POST",
        url: "/comment",
        data: JSON.stringify({
            "parentId": targetId,
            "content": commentContent,
            "type": type
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

function comment(e) {
    var commentId = e.getAttribute("data-id")
    var commentContent = $('#input-' + commentId).val();
    comment2target(commentId, 2, commentContent);
}

/*
*Expand Secondary Reply
* */

function collapseComments(e) {
    var id = e.getAttribute("data-id");
    var collapse = e.getAttribute("data-collapse");
    var comments = $("#comment-" + id);
    if (collapse) {
        //Collapse
        comments.removeClass('in');
        e.removeAttribute("data-collapse");
        e.classList.remove('active');
    } else {
        var subCommentContainer = $("#comment-item-" + id);
        if (subCommentContainer.children().length == 0) {
            $.getJSON("/comment/" + id, function (data) {
                console.log(data);

                $.each(data.data, function (index, comment) {
                    var mediaAvatarLinkElement = $("<a/>", {
                        "href": "#"
                    });
                    var mediaLeftElement = $("<div/>", {
                        "class": "media-left"
                    });
                    mediaAvatarLinkElement.append($("<img/>", {
                        "class": "media-object img-rounded media-object-avatar",
                        "src": comment.user.avatarUrl
                    }));
                    mediaLeftElement.append(mediaAvatarLinkElement);

                    var mediaContentElement = $("<div/>", {
                        "html": comment.content
                    });
                    var menuTimeElement = $("<div/>", {
                        "class": "menu"
                    }).append($("<span/>", {
                        "html": moment(comment.gmtCreate).format('YYYY-MM-DD HH:mm:ss'),
                        "class": "pull-right"
                    }));
                    var mediaHeadingElement = $("<h5/>", {
                        "class": "media-heading"
                    });

                    mediaHeadingElement.append($("<a/>", {
                        "html": comment.user.name
                    }));

                    var mediaBodyElement = $("<div/>", {
                        "class": "media-body"
                    });
                    mediaBodyElement.append(mediaHeadingElement, mediaContentElement, menuTimeElement);


                    var mediaElement = $("<div/>", {
                        "class": "media"
                    });
                    mediaElement.append(mediaLeftElement, mediaBodyElement);


                    var commentElement = $("<div/>", {
                        "class": "col-lg-12 col-md-12 col-sm-12 col-xs-12 secondary-comment-item"
                    });
                    commentElement.append(mediaElement)

                    subCommentContainer.append(commentElement);
                });

            });
        }

        //Expand
        comments.addClass('in');
        e.setAttribute("data-collapse", "in");
        e.classList.add('active');

    }
}

function selectTag(e) {
    var value = e.getAttribute("data-tag");
    var previous = $("#tag").val();
    if(previous.indexOf(value) == -1) {
        if(previous) {
            $("#tag").val(previous + ',' + value);
        } else {
            $("#tag").val(value);
        }
    }
}

function showSelectTab() {
    var tagsNavigation = $("#tags-navigation");
    tagsNavigation.show();
}