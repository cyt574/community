package com.yichen.community.dto;

import com.yichen.community.model.User;
import lombok.Data;

@Data
public class NotificationDTO {
    private Long id;
    private Long gmtCreate;
    private Integer state;
    private String notifierName;
    private Long notifier;
    private String outerTitle;
    private Long outerid;
    private String typeName;
}
