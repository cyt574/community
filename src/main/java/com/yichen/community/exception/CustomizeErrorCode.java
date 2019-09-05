package com.yichen.community.exception;

public enum  CustomizeErrorCode implements ICustomizeErrorCode {

    QUESTION_NOT_FOUND(2001, "你所找的问题不见了，要不换个试试？"),
    TARGET_PARAM_NOT_FOUND(2002, "未选中任何问题或评论进行回复"),
    NOT_LOGIN(2003, "未登录不能进行评论，请先登录后重试"),
    SYS_ERROR(2004, "服务器抽风了，稍后重试下吧！！！"),
    TYPE_PARAM_WRONG(2005, "评论类型错误或不存在"),
    COMMAND_NOT_FOUND(2006, "回复的评论不存在了，稍后重试下吧！！！"),
    CONTENT_IS_EMPTY(2007, "输入内容不能为空，老哥！");

    private String message;
    private Integer code;

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    CustomizeErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
