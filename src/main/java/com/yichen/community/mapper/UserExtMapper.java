package com.yichen.community.mapper;
import com.yichen.community.model.User;

import java.util.List;

public interface UserExtMapper {
    void register(User user);

    String getEncodePasswordByUsername(String userName);

    Integer isExist(String username, String email, String phone);
}
