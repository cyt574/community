package com.yichen.community.controller;

import com.yichen.community.cache.TagCache;
import com.yichen.community.dto.QuestionDTO;
import com.yichen.community.dto.ResultDTO;
import com.yichen.community.exception.CustomizeErrorCode;
import com.yichen.community.exception.CustomizeException;
import com.yichen.community.mapper.QuestionMapper;
import com.yichen.community.mapper.UserMapper;
import com.yichen.community.model.Question;
import com.yichen.community.model.User;
import com.yichen.community.service.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
public class PublishController {

    @Autowired
    UserMapper userMapper;

    @Autowired
    QuestionMapper questionMapper;

    @Autowired
    QuestionService questionService;

    @GetMapping("/publish")
    public String publish(Model model) {
        model.addAttribute("tags", TagCache.get());
        return "publish";
    }

    @ResponseBody
    @PostMapping("/publish")
    public ResultDTO doPublish(
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "tag", required = false) String tag,
            @RequestParam(value = "id", required = false) Long id,
            HttpServletRequest httpServletRequest,
            Model model) {

        model.addAttribute("title", title);
        model.addAttribute("description", description);
        model.addAttribute("tag", tag);
        model.addAttribute("tags", TagCache.get());


        User user = (User) httpServletRequest.getSession().getAttribute("user");

        if(user == null) {
            return new ResultDTO().errorOf(CustomizeErrorCode.NOT_LOGIN);
        }


        if(title == null || title.equals("")) {
            return new ResultDTO().errorOf(CustomizeErrorCode.TITLE_IS_EMPTY);
        }

        if(description == null || description == "") {
            return new ResultDTO().errorOf(CustomizeErrorCode.CONTENT_IS_EMPTY);
        }

        if(tag == null || tag == "") {
            return new ResultDTO().errorOf(CustomizeErrorCode.TAG_IS_EMPTY);
        }

        String invalid = TagCache.filter(tag);
        if(StringUtils.isNotBlank(invalid)) {
            return new ResultDTO().errorOf(CustomizeErrorCode.TAG_IS_INVALID);
        }


        Question question = new Question();

        question.setTitle(title);
        question.setDescription(description);
        question.setTag(tag);
        question.setId(id);

        questionService.createOrUpdate(question, user.getId());

        ResultDTO<Integer> resultDTO = new ResultDTO().errorOf(999,"问题发表成功，♂That's Good♂");
        resultDTO.setData(1);
        return  resultDTO;

    }

    @GetMapping("/publish/{id}")
    public String rePublish(@PathVariable(value = "id") Long id,
                            Model model) {
        QuestionDTO question = questionService.getById(id);
        model.addAttribute("title", question.getTitle());
        model.addAttribute("description", question.getDescription());
        model.addAttribute("tag", question.getTag());
        model.addAttribute("id", question.getId());
        model.addAttribute("tags", TagCache.get());
        return "publish";
    }
}
