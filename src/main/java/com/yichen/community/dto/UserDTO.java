package com.yichen.community.dto;

import com.yichen.community.utils.RegexpUtils;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Pattern;

@Data
public class UserDTO {
    @Length(min = 6, max = 20, message = "姓名长度必须介于6 - 20位之间")
    private String username;
    @Length(min = 6, max = 20, message = "密码长度必须介于6 - 20位之间")
    private String password;

    @Pattern(regexp = RegexpUtils.PHONE, message = "手机号格式不正确")
    private String phone;
    @Pattern(regexp = RegexpUtils.EMAIL, message = "邮箱格式不正确")
    private String email;
}
