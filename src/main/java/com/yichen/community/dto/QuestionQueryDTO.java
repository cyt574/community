package com.yichen.community.dto;

import lombok.Data;

@Data
public class QuestionQueryDTO {
    private String search;
    private Integer viewCount;
    private Integer commentCount;
    private Integer likeCount;
    private String tag;
    private String category;
    private Long beginTime;
    private Long endTime;
    private String orderRule;
}
