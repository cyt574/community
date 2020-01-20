const App = function () {

    const setTagParam = (element) => {
        $("#tagParam").attr("value", element.getAttribute("data-tag"));
        $("#main-title").html($("#tagParam").val());
    };

    const buildTagsList = (resp) => {
        $("#tag-list").empty();
        const tags = resp.data;
        $.each(tags, (index, item) => {
            let tag = '    <span class="tag-item-box-index">' +
                '            <a data-tag="' + item + '"onclick="App.initTag(this)">' + item + '</a>' +
                '            </span>';
            let tagList = $("#tag-list");
            tagList.append(tag);
        })
    };

    const loadTagsList = () => {
        var url = "/getTagList";
        $.ajax({
            type: "GET",
            url: url,
            success: function (resp) {
                if (resp.code = 200) {
                    buildTagsList(resp);
                }
            }
        })
    };

    const loadQuestionList = (resp) => {
        $("#question-wrapper").empty();
        var questionList = resp.data.list;
        $.each(questionList, (index, item) => {
            var question = $('<div class="media" style="margin-left: 20px">' +
                '                <div class="media-left">' +
                '                    <a href="#">' +
                '                        <img class="media-object img-rounded" src="' + item.user.avatarUrl + '" alt="....">' +
                '                    </a>' +
                '                </div>' +
                '                <div class="media-body">' +
                '                    <h4 class="media-heading" style="color: #2860AC">' +
                '                        <a href="/question/' + item.id + '">' + item.title + '</a>' +
                '                    </h4>' +
                '                    <span class="text-desc">' +
                '                        <span >' + item.commentCount +
                '                        </span> 个评论 •' +
                '                        <span>' + item.viewCount +
                '                        </span> 次浏览 • ' +
                '                        <span>发布于' + item.timeCreate + '</span>' +
                '                    </span>' +
                '                </div>' +
                '            </div>');

            question.appendTo("#question-wrapper");
        })
    }

    const setPage = (page) => {
        const req = {
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
            beforeSend: () => {
                loadingIndex = layer.msg('加载列表中', {icon: 16});
            },
            success: (resp) => {
                layer.close(loadingIndex);
                if (resp.code == 200) {
                    loadQuestionList(resp);
                    buildPageNav(resp);
                    $("html,body").animate({scrollTop: 0}, 1);//回到顶端
                } else {
                    layer.msg(data.extend.msg, {time: 2000, icon: 5, shift: 6}, function () {
                    });
                }
            }
        })
    }

    //构建分页导航
    const buildPageNav = (resp) => {
        const page = resp.data;
        //设置当前页
        currentpage = page.pageNum;
        //设置末页
        totalpageo = page.pageSize;
        $('.page-info-area').empty();
        $(".pagination").empty();
        $('.page-info-area').append("当前第" + page.pageNum + "页,共" + page.pages + "页,共" + page.total + "条记录")
        //分页导航
        const nav = $(".pagination");
        const firstLi = $("<li></li>").append($("<a>首页</a>").attr("href", "#"));
        const prli = $("<li></li>").append($("<a  aria-label='Previous'><span aria-hidden='true'>&laquo;</span></a>").attr("href", "#"));
        //首页
        firstLi.click(() => {
            setPage(1);
        });
        //上一页
        prli.click(() => {
            let target = page.pageNum - 1;
            target = target == 0 ? 1 : target;
            setPage(target);
        })
        const lastLi = $("<li></li>").append($("<a>末页</a>").attr("href", "#"));
        const nextli = $("<li></li>").append($("<a  aria-label='Next'><span aria-hidden='true'>&raquo;</span></a>").attr("href", "#"));
        //末页
        lastLi.click(() => {
            //alert("转到:"+page.pages)
            setPage(page.pageSize);
        })
        //下一页
        nextli.click(() => {
            let target = page.pageNum + 1;
            target = target < page.pageSize ? target : page.pageSize;
            setPage(target);
        })
        nav.append(prli);
        $.each(page.navigatepageNums, (index, item) => {
            var li = $("<li></li>").append($("<a>" + item + "</a>").attr("href", "#"));
            if (page.pageNum == item) {
                li.addClass("active");
            }
            //点击翻页
            li.click(() => {
                $(".pagination>li").removeClass("active");
                $(this).addClass("active");
                setPage(item);
                return false;
            })
            nav.append(li);
        })
        nav.append(nextli);
    }

    const initParams = () => {
        $("#primary-tab li a").click(function () {
            $("#orderParam").val(this.id);
            $("#primary-tab li a").removeClass("nav-tabs-item-active");
            $(this).addClass("nav-tabs-item-active");
            setPage(1);
        });

        $("#btn-register").bind("click", () => {
            $("#login-page").hide();
            $("#register-page").show();
        });

        function loginPopup() {
            $("#login-modal").modal("show")
            $("#register-page").hide();
            $("#login-page").show();
        }
        $(".globalLoginBtn").on("click", loginPopup),function() {
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

    const register = () => {
        let data = {
            "username": $("#id-register-username").val(),
            "password": $("#id-register-password").val(),
            "email": $("#id-account-email").val(),
            "phone": $("#id-account-phone").val()
        }
        $.ajax({
            url: "register",
            method: "POST",
            data: JSON.stringify(data),
            contentType: "application/json",
            success: (resp) => {
                if (resp.code == 200) {
                    $("#register-form-content").val(resp.message + ', 正在跳转,请重新登录');
                    $("#register-form-tips").show(1000);
                    setTimeout(() => {
                        window.location.href = "/index";
                    }, 3000);
                } else {
                    $("#register-form-content").html(resp.message);
                    $("#register-form-tips").show(1000);
                    setTimeout(() => {
                        $("#register-form-tips").hide(1000);
                    }, 3000)
                }
            }
        });
    }

    const login = () => {
        var data = {
            "username": $("#id-account-username").val(),
            "password": $("#id-account-password").val()
        }
        $.ajax({
            url: "/login",
            method: "POST",
            type: "application/json",
            data: data,
            success: (resp) => {
                $("#login-form-content").html(resp.message);
                $("#login-form-tips").show(1000);
                setTimeout(() => {
                    if (resp.code == 200) {
                        window.location.href = "/index";
                    } else {
                        $("#login-form-tips").hide(1000);
                    }
                }, 2000);
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
