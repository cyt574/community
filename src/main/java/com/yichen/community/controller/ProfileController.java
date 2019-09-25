package com.yichen.community.controller;

import com.github.pagehelper.PageInfo;
import com.yichen.community.dto.QuestionDTO;
import com.yichen.community.dto.ResultDTO;
import com.yichen.community.exception.CustomizeErrorCode;
import com.yichen.community.mapper.UserMapper;
import com.yichen.community.model.Notification;
import com.yichen.community.model.User;
import com.yichen.community.service.NotificationService;
import com.yichen.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ProfileController {

    @Autowired
    UserMapper userMapper;

    @Autowired
    QuestionService questionService;

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/profiles")
    public String profile(@RequestParam(value = "param") String param, Model model) {
        if(param.equals("reply")) {
            model.addAttribute("sectionParam", param);
        } else if (param.equals("question")) {
            model.addAttribute("sectionParam", param);
        }
        return "profile";
    }

    @GetMapping("/profile/{action}")
    @ResponseBody
    public ResultDTO profile(@PathVariable(name = "action") String action,
                             HttpServletRequest httpServletRequest,
                             @RequestParam(value = "page", defaultValue = "1")Integer page,
                             @RequestParam(value = "size", defaultValue = "10")Integer size) {
        Map<String, Object> map = new HashMap<>();
        User user = (User) httpServletRequest.getSession().getAttribute("user");
        if(user == null) {
            return ResultDTO.errorOf(CustomizeErrorCode.NOT_LOGIN);
        }
        if("questions".equals(action)) {
            PageInfo<QuestionDTO> questionDTOPageInfo = questionService.list(user.getId(), page, size) ;
            map.put("itemList", questionDTOPageInfo);
            map.put("sectionName", "我的问题");
        } else if ("replies".equals(action)) {
            PageInfo<Notification> notificationDTOPageInfo = notificationService.list(user.getId(), page ,size);
            map.put("sectionName", "最新回复");
            map.put("itemList", notificationDTOPageInfo);
        }

        Long unreadCount = notificationService.unreadCount(user.getId());
        map.put("unreadCount", unreadCount);

        return ResultDTO.okOf(map);
    }
}
