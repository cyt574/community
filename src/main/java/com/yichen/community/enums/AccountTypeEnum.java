package com.yichen.community.enums;

public enum AccountTypeEnum {

    NORMAL(1),GITHUB(2),WECHAT(3),QQ(4),WEIBO(5);


    private Integer type;

    AccountTypeEnum(int i) {
        this.type = i;
    }

    public Integer getType() {
        return type;
    }
}
