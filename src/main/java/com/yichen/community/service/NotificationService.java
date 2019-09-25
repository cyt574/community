package com.yichen.community.service;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yichen.community.dto.NotificationDTO;
import com.yichen.community.dto.PaginationDTO;
import com.yichen.community.enums.NotificationStatusEnum;
import com.yichen.community.enums.NotificationTypeEnum;
import com.yichen.community.exception.CustomizeErrorCode;
import com.yichen.community.exception.CustomizeException;
import com.yichen.community.mapper.NotificationMapper;
import com.yichen.community.mapper.UserMapper;
import com.yichen.community.model.Notification;
import com.yichen.community.model.NotificationExample;
import com.yichen.community.model.User;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class NotificationService {

    @Autowired
    NotificationMapper notificationMapper;

    @Autowired
    UserMapper userMapper;

    public PageInfo<Notification> list(Long userid, Integer page, Integer size) {
        PageHelper.startPage(page, size);
        NotificationExample example = new NotificationExample();
        example.createCriteria().andReceiverEqualTo(userid);
        example.setOrderByClause("gmt_create desc");
        List<Notification> notificationList = notificationMapper.selectByExample(example);
        PageInfo<Notification> notificationPageInfo = new PageInfo<>(notificationList);
        return notificationPageInfo;

    }

    public Long unreadCount(Long id) {
        NotificationExample example = new NotificationExample();
        example.createCriteria().andReceiverEqualTo(id).andStateEqualTo(NotificationStatusEnum.UNREAD.getStatus());
        return notificationMapper.countByExample(example);
    }

    public NotificationDTO read(Long id, User user) {
        Notification notification = notificationMapper.selectByPrimaryKey(id);
        if(notification == null) {
            throw new CustomizeException(CustomizeErrorCode.NOTIFICATION_NOT_FOUND);
        }
        if(!Objects.equals(notification.getReceiver() , user.getId())) {
            throw new CustomizeException(CustomizeErrorCode.READ_NOTIFICATION_FAIL);
        }

        notification.setState(NotificationStatusEnum.READ.getStatus());
        notificationMapper.updateByPrimaryKey(notification);

        NotificationDTO notificationDTO = new NotificationDTO();
        BeanUtils.copyProperties(notification, notificationDTO);
        notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notification.getType()));
        return  notificationDTO;
    }
}
