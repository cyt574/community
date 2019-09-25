var App = function () {

    var setTagParam = function (e) {
        $("#tagParam").attr("value", e.getAttribute("data-tag"));
        $("#main-title").html("当前话题：" + $("#tagParam").val());
    };

    var build_tags_list = function(resp){
        $("#tag-list").empty();
        var tags = resp.data;
        $.each(tags, function (index, item) {
            var tag = '    <span class="tag-item-box-index">' +
                '            <a data-tag="'+ item + '"onclick="App.initTag(this)">' + item + '</a>' +
                '            </span>';
            var tagList = $("#tag-list");
            tagList.append(tag);
        })
    };

    var loadTagsList = function() {
        var url = "/getTagList";
        $.ajax({
            type: "GET",
            url: url,
            success: function (resp) {
                if(resp.code = 200) {
                    build_tags_list(resp);
                }
            }
        })
    };


    var load_question_list = function(resp) {
        $("#question_wrapper").empty();
        var questionList = resp.data.list;
        $.each(questionList, function (index, item) {
            var question = $('<div class="media" style="margin-left: 20px">' +
                '                <div class="media-left">' +
                '                    <a href="#">' +
                '                        <img class="media-object img-rounded" src="'+ item.user.avatarUrl + '" alt="....">' +
                '                    </a>' +
                '                </div>' +
                '                <div class="media-body">' +
                '                    <h4 class="media-heading" style="color: #2860AC">' +
                '                        <a href="/question/' + item.id + '">'+ item.title +'</a>' +
                '                    </h4>' +
                '                    <span class="text-desc">' +
                '                        <span >' + item.commentCount +
                '                        </span> 个评论 •' +
                '                        <span>' + item.viewCount +
                '                        </span> 次浏览 • ' +
                '                        <span>发布于' + item.timeCreate +'</span>' +
                '                    </span>' +
                '                </div>' +
                '            </div>');

            question.appendTo("#question_wrapper");
        })
    }

    var setPage = function(page) {
        // var args = $("#main_form").serialize();
        var req = {
            "page": page,
            "size": 15,
            "tag": $("#tagParam").val(),
            "search": $("#searchParam").val(),
            "order": $("#orderParam").val(),
            "category": $("#categoryParam").val()
        };
        $.ajax({
            type: "GET",
            url: "/getQuestionList",
            data: req,
            beforeSend: function () {
                loadingIndex = layer.msg('加载列表中', {icon: 16});
            },
            success: function (resp) {
                layer.close(loadingIndex);
                if (resp.code == 200) {
                    load_question_list(resp);
                    build_page_nav(resp);
                    $("html,body").animate({scrollTop: 0}, 1);//回到顶端
                } else {
                    layer.msg(data.extend.msg, {time: 2000, icon: 5, shift: 6}, function () {
                    });
                }
            }
        })
    }

    //构建分页导航
    var build_page_nav = function(resp) {
        var page = resp.data;
        //设置当前页
        currentpage = page.pageNum;
        //设置末页
        totalpageo = page.pageSize;
        $('.page_info-area').empty();
        $(".pagination").empty();
        $('.page_info-area').append("当前第" + page.pageNum + "页,共" + page.pages + "页,共" + page.total + "条记录")
        //分页导航
        var nav = $(".pagination");
        var firstLi = $("<li></li>").append($("<a>首页</a>").attr("href", "#"));
        var prli = $("<li></li>").append($("<a  aria-label='Previous'><span aria-hidden='true'>&laquo;</span></a>").attr("href", "#"));
        //首页
        firstLi.click(function () {
            setPage(1);
        });
        //上一页
        prli.click(function () {
            var target = page.pageNum - 1;
            target = target == 0 ? 1 : target;
            setPage(target);
        })
        var lastLi = $("<li></li>").append($("<a>末页</a>").attr("href", "#"));
        var nextli = $("<li></li>").append($("<a  aria-label='Next'><span aria-hidden='true'>&raquo;</span></a>").attr("href", "#"));
        //末页
        lastLi.click(function () {
            //alert("转到:"+page.pages)
            setPage(page.pageSize);
        })
        //下一页
        nextli.click(function () {
            var target = page.pageNum + 1;
            target = target < page.pageSize ? target : page.pageSize;
            setPage(target);
        })
        nav.append(prli);
        $.each(page.navigatepageNums, function (index, item) {
            var li = $("<li></li>").append($("<a>" + item + "</a>").attr("href", "#"));
            if (page.pageNum == item) {
                li.addClass("active");
            }
            //点击翻页
            li.click(function () {
                $(".pagination>li").removeClass("active");
                $(this).addClass("active");
                setPage(item);
                return false;
            })
            nav.append(li);
        })
        nav.append(nextli);
    }

    var initParams =function () {
        $("#primary-tab li a").click(function () {
            $("#orderParam").val(this.id);
            $("#primary-tab li a").removeClass("nav-tabs-active");
            $(this).toggleClass("nav-tabs-active");
            setPage(1);
        });

        $("#btnRegister").bind("click",function () {
            $("#login_page").hide();
            $("#register_page").show();
        });

        function login_popup() {
            $("#loginModal").modal("show")
            $("#register_page").hide();
            $("#login_page").show();
        }
        $(".globalLoginBtn").on("click", login_popup),function() {
            var e = [];
            $(".modal").on("show.bs.modal",
                function() {
                    for (var s = 0; e.length > s; s++) e[s] && (e[s].modal("hide"), e[s] = null);
                    e.push($(this));
                    var o = $(this),
                        a = o.find(".modal-dialog"),
                        t = $('<div style="display:table; width:100%; height:100%;"></div>');
                    t.html('<div style="vertical-align:middle; display:table-cell;"></div>'),
                        t.children("div").html(a),
                        o.html(t)
                })
        } ();
    }

    var register = function () {
        let data = {
            "username": $("#id_register_username").val(),
            "password": $("#id_register_password").val(),
            "email": $("#id_account_email").val(),
            "phone": $("#id_account_phone").val()
        }
        $.ajax({
            url: "register",
            method: "POST",
            data: JSON.stringify(data),
            contentType: "application/json",
            success: function (resp) {
                if ( resp.code == 200 ) {
                    $("#register-form-tips").val(resp.message + ', 正在跳转,请重新登录');
                    $("#register-form-tips").show();
                    setTimeout(function () {
                        window.location.href = "/index";
                    },3000);
                    // $("#loginModal").modal("hide")
                    // $(".globalLoginBtn").trigger("click");
                    // $("#id_register_username").val(resp.username);
                } else {
                    $("#register-form-tips").html(resp.message);
                    $("#register-form-tips").show();
                }
            }
        });
    }



    var login = function() {
        var data = {
            "username": $("#id_account_username").val(),
            "password": $("#id_account_password").val()
        }
        $.ajax({
            url: "/login",
            method: "POST",
            type: "application/json",
            data: data,
            success: function (resq) {
                $("#login-form-tips").html(resq.message);
                $("#login-form-tips").show();
                setTimeout(function () {
                    if( resq.code == 200) {
                        window.location.href = "/index";
                    } else {
                        $("#login-form-tips").html("");
                    }
                },1000);
            }
        });
    }

    return {
        initTag: function (e) {
            setTagParam(e);
            setPage(1);
        },
        init: function (page) {
            setPage(page);
            loadTagsList();
            initParams();
        },
        initLoginPage: function () {
            initParams();
        },
        register: function () {
            register();
        },
        login: function () {
            login();
        }
    }
}()

$(document).ready(function () {
});
