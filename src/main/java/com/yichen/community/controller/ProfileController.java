package com.yichen.community.controller;

import com.yichen.community.dto.NotificationDTO;
import com.yichen.community.dto.PaginationDTO;
import com.yichen.community.dto.QuestionDTO;
import com.yichen.community.mapper.UserMapper;
import com.yichen.community.model.Question;
import com.yichen.community.model.User;
import com.yichen.community.service.NotificationService;
import com.yichen.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Controller
public class ProfileController {

    @Autowired
    UserMapper userMapper;

    @Autowired
    QuestionService questionService;

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/profile/{action}")
    public String profile(@PathVariable(name = "action") String action,
                          Model model,
                          HttpServletRequest httpServletRequest,
                          @RequestParam(value = "page", defaultValue = "1")Integer page,
                          @RequestParam(value = "size", defaultValue = "5")Integer size) {
        User user = (User) httpServletRequest.getSession().getAttribute("user");
        if(user == null) {
            return "redirect:/";
        }
        if("questions".equals(action)) {
            model.addAttribute("section", "questions");
            model.addAttribute("sectionName","我的问题");
            PaginationDTO<QuestionDTO> paginationDTO = questionService.list(user.getId(), page, size) ;
            model.addAttribute("pagination", paginationDTO);
        } else if ("replies".equals(action)) {
            model.addAttribute("section", "replies");
            model.addAttribute("sectionName", "最新回复");
            PaginationDTO<NotificationDTO> paginationDTO = notificationService.list(user.getId(), page ,size);
            model.addAttribute("pagination", paginationDTO);
        }
        Long unreadCount = notificationService.unreadCount(user.getId());
        model.addAttribute("unreadCount", unreadCount);
        return "profile";
    }
}
