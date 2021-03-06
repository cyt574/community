package com.yichen.community.dto;

import com.yichen.community.model.User;
import lombok.Data;

@Data
public class QuestionDTO {
    private Long id;
    private String title;
    private String description;
    private String tag;
    private Long gmtCreate;
    private Long gmtModified;
    private Long creator;
    private Integer commentCount;
    private Integer viewCount;
    private Integer likeCount;
    private User user;
    private Integer hotIn30d;
    private Integer hotIn15d;
    private Integer hotIn7d;
    private String timeCreate;
}
