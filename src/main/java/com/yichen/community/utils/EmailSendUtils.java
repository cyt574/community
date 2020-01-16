package com.yichen.community.utils;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.springframework.beans.factory.annotation.Autowired;

public class EmailSendUtils {
    @Autowired
    Email email;

    public void send(String subject, String msg, String... arg) {
        try {
            email.setSubject(subject);
            email.setMsg(msg);
            email.addTo(arg);
            email.send();

        } catch (EmailException e) {
            e.printStackTrace();
        }
    }
}
