package com.yichen.community.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource("classpath:spring-kaptcha.xml")
public class KaptChaConfig {
}
