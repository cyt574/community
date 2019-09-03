package com.yichen.community.controller;

import com.yichen.community.dto.PaginationDTO;
import com.yichen.community.dto.QuestionDTO;
import com.yichen.community.mapper.QuestionMapper;
import com.yichen.community.mapper.UserMapper;
import com.yichen.community.model.Question;
import com.yichen.community.model.User;
import com.yichen.community.service.QuestionService;
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
public class IndexController {

    @Autowired
    UserMapper userMapper;

    @Autowired
    QuestionService questionService;

    @GetMapping("/")
    public String index(HttpServletRequest httpServletRequest,
                        Model model,
                        @RequestParam(value = "page", defaultValue = "1")Integer page,
                        @RequestParam(value = "size", defaultValue = "2")Integer size){
        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies != null && cookies.length != 0) {
            for (Cookie cookie : cookies) {
                if(cookie.getName().equals("token")) {
                    String token = cookie.getValue();
                    User user = userMapper.findByToken(token);
                    if(user != null) {
                        httpServletRequest.getSession().setAttribute("user", user);
                    }
                    break;
                }
            }
        }

        PaginationDTO paginationDTO = questionService.list(page, size);

        model.addAttribute("pagination", paginationDTO);

        return "index";
    }
}
