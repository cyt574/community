package com.yichen.community.controller;

import com.github.pagehelper.PageInfo;
import com.yichen.community.dto.QuestionDTO;
import com.yichen.community.dto.ResultDTO;
import com.yichen.community.dto.UserProfileDTO;
import com.yichen.community.exception.CustomizeErrorCode;
import com.yichen.community.mapper.UserMapper;
import com.yichen.community.model.Notification;
import com.yichen.community.model.User;
import com.yichen.community.model.UserDetail;
import com.yichen.community.service.NotificationService;
import com.yichen.community.service.QuestionService;
import com.yichen.community.service.UserDetailService;
import com.yichen.community.service.UserService;
import com.yichen.community.utils.EmailSendUtils;
import com.yichen.community.validator.BeanValidator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.xml.validation.Validator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Controller
public class ProfileController {

    @Autowired
    UserMapper userMapper;

    @Autowired
    UserService userService;

    @Autowired
    QuestionService questionService;

    @Autowired
    NotificationService notificationService;

    @Autowired
    UserDetailService userDetailService;

    @Autowired
    EmailSendUtils emailSendUtils;

    @GetMapping("/profiles")
    public String profile(@RequestParam(value = "param", required = false) String param, @RequestParam(value = "id", required = false) Long id, Model model, HttpServletRequest request) {
        if (StringUtils.isBlank(param)) {
            param = "question";
        }
        if (param.equals("reply")) {
            model.addAttribute("sectionParam", param);
        } else if (param.equals("question")) {
            model.addAttribute("sectionParam", param);
        } else if (param.equals("follower")) {
            model.addAttribute("sectionParam", param);
        } else if (param.equals("following")) {
            model.addAttribute("sectionParam", param);
        }

        // 用户未登录退出用户中心，返回首页
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return "index";
        }

        UserProfileDTO userProfileDTO = null;
        if (id == null) {
            userProfileDTO = userService.getUserProfileById(user.getId());
        } else {
            userProfileDTO = userService.getUserProfileById(id);
        }

        // 无信息，返回首页
        if (userProfileDTO == null) {
            return "index";
        }

        String followStatus = userService.getFollowStatus(id, user.getId()) == 1 ? "已关注" : "关注他";
        model.addAttribute("followStatus", followStatus);
        model.addAttribute("userProfile", userProfileDTO);
        return "profile";
    }


    @GetMapping("/profile/{action}/{id}")
    @ResponseBody
    public ResultDTO profile(@PathVariable(name = "action") String action,
                             @PathVariable(name = "id") Long id,
                             HttpServletRequest httpServletRequest,
                             @RequestParam(value = "page", defaultValue = "1") Integer page,
                             @RequestParam(value = "size", defaultValue = "10") Integer size) {
        Map<String, Object> map = new HashMap<>();

        if ("questions".equals(action)) {
            PageInfo<QuestionDTO> questionDTOPageInfo = questionService.list(id, page, size);
            map.put("itemList", questionDTOPageInfo);
            map.put("sectionName", "我的问题");
        } else if ("replies".equals(action)) {
            PageInfo<Notification> notificationDTOPageInfo = notificationService.list(id, page, size);
            map.put("sectionName", "最新回复");
            map.put("itemList", notificationDTOPageInfo);
        } else if ("following".equals(action)) {
            map.put("sectionName", "我的关注");
            PageInfo<UserProfileDTO> userDTOPageInfo = userService.listByFollowing(id, page, size);
            map.put("itemList", userDTOPageInfo);
        } else if ("follower".equals(action)) {
            map.put("sectionName", "我的粉丝");
            PageInfo<UserProfileDTO> userDTOPageInfo = userService.listByFollower(id, page, size);
            map.put("itemList", userDTOPageInfo);
        }

        User user = (User) httpServletRequest.getSession().getAttribute("user");
        if (user == null) {
            return ResultDTO.errorOf(CustomizeErrorCode.NOT_LOGIN);
        }
//        Long unreadCount = notificationService.unreadCount(user.getId());
//        map.put("unreadCount", unreadCount);

        return ResultDTO.okOf(map);
    }

    @GetMapping("/profile/follow")
    @ResponseBody
    public ResultDTO follow(Long id, Long fid) {
        int result = userService.follow(id, fid);
        if (result == 1) {
            return ResultDTO.okOf("已关注");
        }
        return ResultDTO.errorOf(2100, " 关注失败");
    }

    @GetMapping("/profile/unfollow")
    @ResponseBody
    public ResultDTO unfollow(Long id, Long fid) {
        int result = userService.unFollow(id, fid);
        if (result == 1) {
            return ResultDTO.okOf("关注他");
        }
        return ResultDTO.errorOf(2100, " 取关失败");
    }


    @GetMapping("/profile/detail")
    public String profileDetail(@SessionAttribute("user") User sessionUser, Model model) {
        UserDetail userDetail = userDetailService.getUserDetail(sessionUser.getId());
        model.addAttribute("profileDetail", userDetail);
        return "profile_detail";
    }

    @ResponseBody
    @PostMapping("/profile/update")
    public ResultDTO profileDetail(@SessionAttribute("user") User sessionUser, @RequestBody UserProfileDTO userProfileDTO, Model model) {
        String validator = BeanValidator.validator(userProfileDTO);
        if(StringUtils.isNotBlank(validator)) {
            return ResultDTO.errorOf(2003, "数据校验不通过");
        }

        UserDetail userDetail = new UserDetail();
        long id = sessionUser.getId();
        BeanUtils.copyProperties(userProfileDTO, userDetail);
        BeanUtils.copyProperties(userProfileDTO, sessionUser);
        userDetail.setId(id);
        sessionUser.setId(id);

        int result = userDetailService.updateUserDetail(userDetail);
        result &= userService.update(sessionUser);
        if (result == 1) {
            return ResultDTO.okOf();
        } else {
            return ResultDTO.errorOf(2003, "更新失败");
        }
    }

    @GetMapping("/profile/safety")
    public String profileSafety() {
        return "safety";
    }


    @ResponseBody
    @GetMapping("/profile/sendSecurityCodeByEmail")
    public ResultDTO profileSendSecurityCodeByEmail(@SessionAttribute("user") User user) {
        String securityCode = generateRandomCode(4, user);
        emailSendUtils.send("验证码确认 | 以辰社区",
                String.format("用户%s 在%s时间 申请了修改密码操作，如果不是您本人的操作，请忽略本条邮件或者进行安全检查，验证码为：%s",
                        user.getName(), new Date(), securityCode, user.getEmail()));
        return ResultDTO.okOf();
    }




    private String generateRandomCode(int length, User user) {
        char code;
        int rand;

        Random random = new Random();
        String randomCode = "";
        for (int i = 0; i < length; i++) {
            rand = random.nextInt(10);
            code = (char) ('0' + (char) (rand));
            randomCode += code;
        }
        return randomCode;
    }


    // TODO: 2019/10/13
    @GetMapping("/profile/sendSecurityCodeByPhone")
    public String profileSendSecurityCodeByPhone() {
        return "/";
    }
}
