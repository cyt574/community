package com.yichen.community.controller;

import com.yichen.community.dto.CommentCreateDTO;
import com.yichen.community.dto.ResultDTO;
import com.yichen.community.exception.CustomizeErrorCode;
import com.yichen.community.model.Comment;
import com.yichen.community.model.User;
import com.yichen.community.service.CommentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class CommentController {

    @Autowired
    CommentService commentService;

    @ResponseBody
    @RequestMapping(value = "/comment", method = RequestMethod.POST)
    public Object post(@RequestBody CommentCreateDTO commentCreateDTO,
                       HttpServletRequest httpServletRequest) {
        User user = (User) httpServletRequest.getSession().getAttribute("user");
        if( user == null) {
            return ResultDTO.errorOf(CustomizeErrorCode.NOT_LOGIN);
        }

        if ( commentCreateDTO == null || StringUtils.isBlank(commentCreateDTO.getContent())) {
            return ResultDTO.errorOf(CustomizeErrorCode.CONTENT_IS_EMPTY);
        }

        Comment comment = new Comment();
        comment.setParentId(commentCreateDTO.getParentId());
        comment.setContent(commentCreateDTO.getContent());
        comment.setType(commentCreateDTO.getType());
        comment.setGmtCreate(System.currentTimeMillis());
        comment.setGmtModified(comment.getGmtCreate());
        comment.setCommentator(1L);
        comment.setLikeCount(0L);
        commentService.insert(comment);
        return ResultDTO.okOf();
    }
}