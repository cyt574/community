package com.yichen.community.config;

import com.yichen.community.utils.EmailSendUtils;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
public class EmailConfig {

    @Value("${email.host.name}")
    String hostName;

    @Value("${email.smtp.port}")
    int stmpPort;

    @Value("${email.from}")
    String emailFrom;

    @Value("${email.password}")
    String emailPassword;

    @Value("${email.from}")
    String from;

    @Value("${email.SSLOnConnect}")
    boolean SSLOnConnect;

    @Bean
    public DefaultAuthenticator defaultAuthenticator() {
        return new DefaultAuthenticator(emailFrom, emailPassword);
    }


    @Bean
    public SimpleEmail simpleEmail() throws EmailException {
        SimpleEmail simpleEmail = new SimpleEmail();
        simpleEmail.setHostName(hostName);
        simpleEmail.setSmtpPort(stmpPort);
        simpleEmail.setAuthenticator(defaultAuthenticator());
        simpleEmail.setSSLOnConnect(SSLOnConnect);
        simpleEmail.setFrom(from);
        return simpleEmail;
    }

    @Bean
    public EmailSendUtils emailSendUtils() {
        return new EmailSendUtils();
    }



//    <bean id="email" class="org.apache.commons.mail.SimpleEmail">
//        <property name="hostName" value="${email.host.name}"/>
//        <property name="smtpPort" value="${email.smtp.port}"/>
//        <property name="authenticator">
//            <bean class="org.apache.commons.mail.DefaultAuthenticator">
//                <constructor-arg index="0" value="${email.from}"/>
//                <constructor-arg index="1" value="${email.password}"/>
//            </bean>
//        </property>
//        <property name="SSLOnConnect" value="true"/>
//        <property name="from" value="${email.from}"/>
//    </bean>
//
//    <bean id="emailSendUtils" class="com.yichen.my.shop.commons.utils.EmailSendUtils">
//
//    </bean>
}
