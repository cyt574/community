package com.yichen.community.enums;


public enum QuestionTypeEnum {

    ZERO(6),
    HOT_IN_30D(2),
    HOT_IN_15D(3),
    HOT_IN_7D(4),
    HOT_IN_ALL(5),
    RECOMMEND(1);

    private Integer type;

    public static boolean isExist(Integer type) {
        for (QuestionTypeEnum questionTypeEnum : QuestionTypeEnum.values()) {
            if(questionTypeEnum.getType() == type) return true;
        }
        return false;
    }

    public Integer getType() {
        return type;
    }

    QuestionTypeEnum(Integer type) {
        this.type = type;
    }
}
