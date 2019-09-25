package com.yichen.community.controller;

import com.github.pagehelper.PageInfo;
import com.yichen.community.dto.*;
import com.yichen.community.mapper.UserMapper;
import com.yichen.community.service.NotificationService;
import com.yichen.community.service.QuestionService;
import com.yichen.community.utils.StaticScheduleTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@Slf4j
public class IndexController {

    @Autowired
    UserMapper userMapper;

    @Autowired
    QuestionService questionService;

    @Autowired
    NotificationService notificationService;

    @GetMapping(value = {"", "/", "/index"})
    public String index(@RequestParam(value = "tag", required = false) String tag,
                        @RequestParam(value = "search", required = false) String search,
                        @RequestParam(value = "category", defaultValue = "0") String category,
                        Map<String, Object> map) {
        map.put("tag", tag);
        map.put("search", search);
        map.put("category", category);
        return "index";
    }


    @GetMapping("/getQuestionList")
    @ResponseBody
    public ResultDTO getQuestionList(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                                            @RequestParam(value = "size", defaultValue = "15") Integer size,
                                                            @RequestParam(value = "search", required = false) String search,
                                                            @RequestParam(value = "order", required = false) String order,
                                                            @RequestParam(value = "tag", required = false) String tag,
                                                            @RequestParam(value = "category", required = false) String category) {

        PageInfo<QuestionDTO>  questionDTOPageHelper = questionService.listBySort(search, page, size, order, tag);
        return ResultDTO.okOf(questionDTOPageHelper);
    }

    @GetMapping("/getTagList")
    @ResponseBody
    public ResultDTO getTagList() {
        return ResultDTO.okOf(StaticScheduleTask.getTagList());
    }
}
