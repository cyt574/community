package com.yichen.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.yichen.community.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RedisController {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @RequestMapping("/user/save")
    public void saveUser() {
        User user = new User();
        user.setName("张三");
        user.setAccountId("3838438");
        user.setEmail("418894140@qq.com");
        redisTemplate.opsForValue().set("userZhang", JSONObject.toJSONString(user));
    }

    @RequestMapping("/user/get")
    public String getUser() {
        return (String)redisTemplate.opsForValue().get("userZhang");
    }
}