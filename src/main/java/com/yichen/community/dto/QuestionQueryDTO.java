package com.yichen.community.dto;

import lombok.Data;

@Data
public class QuestionQueryDTO {
    private String search;
    private Integer offset;
    private Integer size;
    private Integer viewCount;
    private Integer commentCount;
    private Integer likeCount;
    private String orderRule;
    private String tag;
}
