package com.yichen.community.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yichen.community.dto.UserDTO;
import com.yichen.community.dto.UserProfileDTO;
import com.yichen.community.enums.AccountTypeEnum;
import com.yichen.community.mapper.UserExtMapper;
import com.yichen.community.mapper.UserMapper;
import com.yichen.community.model.User;
import com.yichen.community.model.UserExample;
import com.yichen.community.utils.TimeUtils;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    UserExtMapper userExtMapper;

    public void createOrUpdate(User user) {
        UserExample userExample = new UserExample();
        userExample.createCriteria().andAccountIdEqualTo(user.getAccountId());
        List<User> users = userMapper.selectByExample(userExample);
        if(users.size() == 0) {
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            userMapper.insert(user);
        } else {
            User dbUser = users.get(0);
            User updateUser = new User();
            updateUser.setGmtCreate(System.currentTimeMillis());
            updateUser.setAvatarUrl(user.getAvatarUrl());
            updateUser.setName(user.getName());
            updateUser.setToken(user.getToken());
            UserExample example = new UserExample();
            example.createCriteria().andIdEqualTo(dbUser.getId());
            userMapper.updateByExampleSelective(updateUser, userExample);
        }
    }

    public void register(UserDTO userDTO) {
        User user = new User();
        String token = UUID.randomUUID().toString();
        BeanUtils.copyProperties(userDTO, user);
        user.setToken(token);
        user.setName(userDTO.getUsername());
        user.setGmtCreate(System.currentTimeMillis());
        user.setAccountType(AccountTypeEnum.NORMAL.getType());
        user.setAvatarUrl("/images/default-avatar.png");


        String salt = new SecureRandomNumberGenerator().nextBytes().toString();
        int times = 2;
        String algorithmName = "md5";

        String encodedPassword = new SimpleHash(algorithmName, userDTO.getPassword(), salt, times).toString();

        user.setSalt(salt);
        user.setPassword(encodedPassword);
        userExtMapper.register(user);
    }

    public void login(UserDTO userDTO) {
    }



    public String getPassword(String name) {
        User user = getByName(name);
        if(null==user)
            return null;
        return user.getPassword();
    }

    public User getByName(String name) {
        UserExample example = new UserExample();
        example.createCriteria().andNameEqualTo(name);
        List<User> users = userMapper.selectByExample(example);
        if(users.isEmpty())
            return null;
        return users.get(0);
    }


    public void add(User u) {
        userMapper.insert(u);
    }

    public void delete(Long id) {
        userMapper.deleteByPrimaryKey(id);
    }

    public int update(User u) {
        return userMapper.updateByPrimaryKeySelective(u);
    }

    public User get(Long id) {
        return userMapper.selectByPrimaryKey(id);
    }

    public List<User> list(){
        UserExample example =new UserExample();
        example.setOrderByClause("id desc");
        return userMapper.selectByExample(example);

    }

    public boolean isExist(String username, String email, String phone) {
        int count = userExtMapper.isExist(username, email, phone);
        if(count > 0)
            return true;
        return false;
    }

    public UserProfileDTO getUserProfileById(Long id) {
        UserProfileDTO userProfileDTO = userExtMapper.getUserProfileById(id);
        int followerCount = userExtMapper.getUserFollowerCountById(id);
        int followingCount = userExtMapper.getUserFollowingCountById(id);
        int question = userExtMapper.getUserQuestionCountById(id);
        userProfileDTO.setFollower(followerCount);
        userProfileDTO.setFollowing(followingCount);
        userProfileDTO.setQuestion(question);
        userProfileDTO.setId(id);
        return userProfileDTO;
    }

    public int follow(Long id, Long fid) {
        return userExtMapper.follow(id, fid);
    }

    public PageInfo<UserProfileDTO> listByFollowing(Long id, Integer page, Integer size) {
        PageHelper.startPage(page, size);

        List<UserProfileDTO> userDTOList = userExtMapper.selectByFollowing(id);
//        for (UserDTO qusetion : userDTOList) {
//            qusetion.setTimeCreate(TimeUtils.getFormatedDiffTime(qusetion.getGmtCreate(), System.currentTimeMillis()));
//        }
        PageInfo<UserProfileDTO> userDTOPageInfo = new PageInfo<>(userDTOList);
        userDTOPageInfo.setNavigatePages(5);
        return userDTOPageInfo;
    }

    public PageInfo<UserProfileDTO> listByFollower(Long id, Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<UserProfileDTO> userDTOList = userExtMapper.selectByFollower(id);
        PageInfo<UserProfileDTO> userDTOPageInfo = new PageInfo<>(userDTOList);
        userDTOPageInfo.setNavigatePages(5);
        return userDTOPageInfo;
    }

    public int getFollowStatus(Long uid, Long fid) {
        int result = userExtMapper.getFollowStatus(uid, fid);
        return result;
    }

    public int unFollow(Long id, Long fid) {
        return userExtMapper.unFollow(id, fid);
    }
}
