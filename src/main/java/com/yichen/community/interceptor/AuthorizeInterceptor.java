package com.yichen.community.interceptor;

import com.yichen.community.exception.CustomizeErrorCode;
import com.yichen.community.exception.CustomizeException;
import com.yichen.community.mapper.UserMapper;
import com.yichen.community.model.User;
import com.yichen.community.model.UserExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


@Service
public class AuthorizeInterceptor implements HandlerInterceptor {

    @Autowired
    UserMapper userMapper;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            throw new CustomizeException(CustomizeErrorCode.NOT_LOGIN);
        }
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length != 0) {
            for (Cookie cookie : cookies) {
                if(cookie.getName().equals("token")) {
                    String token = cookie.getValue();
                    UserExample userExample = new UserExample();
                    userExample.createCriteria().andTokenEqualTo(token);
                    List<User> users =  userMapper.selectByExample(userExample);
                    if(users.size() != 0) {
                        if(!user.getId().equals(users.get(0).getId())) {
                            throw new CustomizeException(CustomizeErrorCode.PERMISSION_DENIED);
                        }
                        break;
                    }
                }
            }
        }
        return  true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
