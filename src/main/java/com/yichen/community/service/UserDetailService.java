package com.yichen.community.service;

import com.yichen.community.mapper.UserDetailMapper;
import com.yichen.community.model.UserDetail;
import com.yichen.community.model.UserDetailExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserDetailService {
    @Autowired
    UserDetailMapper userDetailMapper;


    public UserDetail getUserDetail(Long id) {
        UserDetail userDetail = userDetailMapper.selectByPrimaryKey(id);
        if( userDetail == null) {
            userDetail = new UserDetail();
            userDetail.setId(id);
            userDetailMapper.insert(userDetail);
        }
        return userDetail;
    }

    public int updateUserDetail(UserDetail userDetail) {
        int result = userDetailMapper.updateByPrimaryKey(userDetail);
        return result;
    }
}
