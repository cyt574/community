package com.yichen.community.mapper;
import com.yichen.community.dto.UserDTO;
import com.yichen.community.dto.UserProfileDTO;
import com.yichen.community.model.User;

import java.util.List;

public interface UserExtMapper {
    void register(User user);

    String getEncodePasswordByUsername(String userName);

    Integer isExist(String username, String email, String phone);

    Integer getUserFollowerCountById(Long id);

    Integer getUserFollowingCountById(Long id);

    Integer getUserQuestionCountById(Long id);

    Integer follow(Long id, Long fid);

    Integer unFollow(Long id, Long fid);

    List<UserProfileDTO> selectByFollowing(Long id);

    List<UserProfileDTO> selectByFollower(Long id);

    UserProfileDTO getUserProfileById(Long id);

    Integer getFollowStatus(Long uid, Long fid);

}
