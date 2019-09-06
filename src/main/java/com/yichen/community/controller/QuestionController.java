package com.yichen.community.controller;

import com.yichen.community.dto.CommentDTO;
import com.yichen.community.dto.QuestionDTO;
import com.yichen.community.enums.CommentTypeEnum;
import com.yichen.community.service.CommentService;
import com.yichen.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class QuestionController {

    @Autowired
    QuestionService questionService;

    @Autowired
    CommentService commentService;

    @GetMapping("/question/{id}")
    public String question(@PathVariable(name = "id") Long id,
                           Model model) {
        QuestionDTO questionDTO = questionService.getById(id);

        List<CommentDTO> comments = commentService.listByTargetId(id, CommentTypeEnum.QUESTION);

        questionService.increView(id);
        model.addAttribute("question", questionDTO);
        model.addAttribute("comments", comments);
        return "question";
    }
}
