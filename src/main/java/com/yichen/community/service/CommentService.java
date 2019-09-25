package com.yichen.community.service;

import com.yichen.community.dto.CommentDTO;
import com.yichen.community.enums.CommentTypeEnum;
import com.yichen.community.enums.NotificationStatusEnum;
import com.yichen.community.enums.NotificationTypeEnum;
import com.yichen.community.exception.CustomizeErrorCode;
import com.yichen.community.exception.CustomizeException;
import com.yichen.community.mapper.*;
import com.yichen.community.model.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    CommentMapper commentMapper;

    @Autowired
    CommentExtMapper commentExtMapper;

    @Autowired
    QuestionMapper questionMapper;

    @Autowired
    QuestionExtMapper questionExtMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    NotificationMapper notificationMapper;

    @Transactional
    public void insert(Comment comment, User commentator) {
        if (comment.getParentId() == null || comment.getParentId() == 0) {
            throw new CustomizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUND);
        }

        if (comment.getType() == null || !CommentTypeEnum.isExist(comment.getType())) {
            throw new CustomizeException(CustomizeErrorCode.TYPE_PARAM_WRONG);
        }

        if (comment.getType() == CommentTypeEnum.COMMENT.getType()) {
            Comment dbComment = commentMapper.selectByPrimaryKey(comment.getParentId());
            if (dbComment == null) {
                throw new CustomizeException(CustomizeErrorCode.COMMAND_NOT_FOUND);
            }
            //Reply comment
            commentMapper.insert(comment);

            //Add comment view time
            Comment parentComment = new Comment();
            parentComment.setId(comment.getParentId());
            parentComment.setCommentCount(1);
            commentExtMapper.increCommentCount(parentComment);

            //Notification
            Question dbQuestion = questionMapper.selectByPrimaryKey(dbComment.getParentId());
            if (dbQuestion == null) {
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }

            dbQuestion.setHotIn7d(1);
            dbQuestion.setHotIn15d(1);
            dbQuestion.setHotIn30d(1);
            questionExtMapper.updateHotTopic(dbQuestion);

            createNotify(comment, dbComment.getCommentator(), commentator.getName(), dbQuestion.getTitle(), NotificationTypeEnum.REPLY_COMMENT.getType(), dbQuestion.getId());
        } else {
            Question dbQuestion = questionMapper.selectByPrimaryKey(comment.getParentId());
            if (dbQuestion == null) {
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            //Reply question
            commentMapper.insert(comment);

            //Add question view time
            dbQuestion.setHotIn7d(1);
            dbQuestion.setHotIn15d(1);
            dbQuestion.setHotIn30d(1);
            dbQuestion.setCommentCount(1);
            questionExtMapper.increCommentCount(dbQuestion);

            //Notification
            createNotify(comment, dbQuestion.getCreator(), commentator.getName(), dbQuestion.getTitle(), NotificationTypeEnum.REPLY_QUESTION.getType(), dbQuestion.getId());
        }
    }

    private void createNotify(Comment comment, Long receiver, String notifierName, String outerTitle, Integer type, Long outerId) {
        if(receiver == comment.getCommentator()) {
            return;
        }

        Notification notification = new Notification();
        notification.setGmtCreate(System.currentTimeMillis());
        notification.setType(type);
        notification.setOuterid(outerId);
        notification.setNotifier(comment.getCommentator());
        notification.setReceiver(receiver);
        notification.setState(NotificationStatusEnum.UNREAD.getStatus());
        notification.setOuterTitle(outerTitle);
        notification.setNotifierName(notifierName);
        notificationMapper.insert(notification);
    }

    public List<CommentDTO> listByTargetId(Long id, CommentTypeEnum type) {

        CommentExample commentExample = new CommentExample();
        commentExample.createCriteria().andParentIdEqualTo(id).andTypeEqualTo(type.getType());
        commentExample.setOrderByClause("gmt_create desc");
        List<Comment> comments = commentMapper.selectByExample(commentExample);

        if (comments.size() == 0) {
            return new ArrayList<>();
        }

        Set<Long> commentators = comments.stream().map(comment -> comment.getCommentator()).collect(Collectors.toSet());
        List<Long> userIds = new ArrayList<>();
        userIds.addAll(commentators);
        UserExample userExample = new UserExample();
        userExample.createCriteria().andIdIn(userIds);
        List<User> users = userMapper.selectByExample(userExample);
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(user -> user.getId(), user -> user));

        List<CommentDTO> commentDTOS = comments.stream().map(comment -> {
            CommentDTO commentDTO = new CommentDTO();
            BeanUtils.copyProperties(comment, commentDTO);
            commentDTO.setUser(userMap.get(comment.getCommentator()));
            return commentDTO;
        }).collect(Collectors.toList());

        return commentDTOS;
    }
}
