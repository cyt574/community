package com.yichen.community.controller;

import com.yichen.community.dto.NotificationDTO;
import com.yichen.community.enums.NotificationTypeEnum;
import com.yichen.community.model.User;
import com.yichen.community.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


import javax.servlet.http.HttpServletRequest;

@Controller
public class NotificationController {

    @Autowired
    NotificationService notificationService;

    @GetMapping("/notification/{id}")
    public String profile(@PathVariable(name = "id") Long id, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if(user == null) {
            return "redirect:/";
        }

        NotificationDTO notificationDTO = notificationService.read(id, user);

        if(NotificationTypeEnum.REPLY_QUESTION.getName() == notificationDTO.getTypeName()
                || NotificationTypeEnum.REPLY_COMMENT.getName() == notificationDTO.getTypeName()) {
            return "redirect:/question/" + notificationDTO.getOuterid();
        } else {
            return "redirect:/";
        }
    }
}
