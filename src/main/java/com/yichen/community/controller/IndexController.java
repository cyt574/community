package com.yichen.community.controller;

import com.yichen.community.dto.PaginationDTO;
import com.yichen.community.dto.QuestionDTO;
import com.yichen.community.enums.QuestionTypeEnum;
import com.yichen.community.mapper.QuestionMapper;
import com.yichen.community.mapper.UserMapper;
import com.yichen.community.model.Question;
import com.yichen.community.model.User;
import com.yichen.community.service.NotificationService;
import com.yichen.community.service.QuestionService;
import com.yichen.community.utils.StaticScheduleTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
@Slf4j
public class IndexController {

    @Autowired
    UserMapper userMapper;

    @Autowired
    QuestionService questionService;

    @Autowired
    NotificationService notificationService;

    @GetMapping("/")
    public String index(Model model,
                        @RequestParam(value = "page", defaultValue = "1")Integer page,
                        @RequestParam(value = "size", defaultValue = "10")Integer size,
                        @RequestParam(value = "search", required = false)String search,
                        @RequestParam(value = "type", required = false)Integer type){
        PaginationDTO paginationDTO;
        if(type != null) {
            paginationDTO = questionService.list(search, page, size, type);
        } else {
            paginationDTO = questionService.list(search, page, size);
        }

        model.addAttribute("pagination", paginationDTO);
        model.addAttribute("search", search);
        model.addAttribute("type", type);
        model.addAttribute("tags", StaticScheduleTask.getTagList());
        return "index";
    }

}
