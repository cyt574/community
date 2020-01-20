package com.yichen.community.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yichen.community.model.User;
import com.yichen.community.utils.RegexpUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class UserProfileDTO extends User {

    @Size(min = 6, max = 22)
    private String accountId;
    @Size(min = 3, max = 22)
    private String username;

    @JsonIgnore
    private String password;
    private Integer follower;
    private Integer following;
    private Integer question;

    @Pattern(regexp = RegexpUtils.PHONE, message = "手机号格式不正确")
    private String phone;
    @Pattern(regexp = RegexpUtils.EMAIL, message = "邮箱格式不正确")
    private String email;


    private String gender;
    private String avatarUrl;
    private String industry;
    private String profession;
    private String signature;
}
