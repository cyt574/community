package com.yichen.community.controller;

import com.yichen.community.validator.BeanValidator;
import com.yichen.community.dto.AccessTokenDTO;
import com.yichen.community.dto.GithubUser;
import com.yichen.community.dto.ResultDTO;
import com.yichen.community.dto.UserDTO;
import com.yichen.community.model.User;
import com.yichen.community.provider.GithubProvider;
import com.yichen.community.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Validator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.yichen.community.validator.BeanValidator.validator;

@Controller
public class AuthorizeController {

    @Autowired
    private GithubProvider githubProvider;

    @Value("${github.client.id}")
    private String clientId;

    @Value("${github.client.secret}")
    private String clientSecret;

    @Value("${github.redirect.uri}")
    private String redirectUri;

    @Autowired
    private UserService userService;

    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code") String code, @RequestParam(name = "state") String state, HttpServletResponse httpServletResponse, HttpServletRequest request) {
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setCode(code);
        accessTokenDTO.setClient_id(clientId);
        accessTokenDTO.setClient_secret(clientSecret);
        accessTokenDTO.setState(state);
        accessTokenDTO.setRedirect_uri(redirectUri);
        String accessToken = githubProvider.getAccessToken(accessTokenDTO);
        GithubUser githubUser = githubProvider.getUser(accessToken);
        if(githubUser !=null && githubUser.getId() != null) {
            User user = new User();
            String token = UUID.randomUUID().toString();
            user.setToken(token);
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setAvatarUrl(githubUser.getAvatarUrl());
            user.setName(githubUser.getName());
            userService.createOrUpdate(user);
            //登录成功，写cookie和session
            httpServletResponse.addCookie(new Cookie("token", token));
            request.getSession().setAttribute("user", user);
            return "redirect:/";
        }else {
            return "redirect:/";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        request.getSession().removeAttribute("user");
        Cookie cookie = new Cookie("token", null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/";
    }


    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @ResponseBody
    @PostMapping("/register")
    public ResultDTO register(@RequestBody UserDTO userDTO) {
        String validator = BeanValidator.validator(userDTO);
        if(validator != null) {
            return ResultDTO.errorOf(2100, validator);
        }
        boolean exist = userService.isExist(userDTO.getUsername(), null, null);
        if(exist) {
            return ResultDTO.errorOf(2100, "用户名已存在");
        }
        exist = userService.isExist(null, userDTO.getEmail(), null);
        if(exist) {
            return ResultDTO.errorOf(2100, "邮箱已被注册");
        }
        exist = userService.isExist(null, null, userDTO.getPhone());
        if(exist) {
            return ResultDTO.errorOf(2100, "手机号已被注册");
        }
        userService.register(userDTO);

        Map<String, Object> map = new HashMap<>();
        map.put("username", userDTO.getUsername());
        return ResultDTO.okOf(map);
    }

    @PostMapping("/login")
    @ResponseBody
    public ResultDTO login(@RequestParam(value = "username") String username, @RequestParam(value = "password") String password, HttpServletRequest request, HttpServletResponse response) {
        if( StringUtils.isBlank(username)) {
            return  ResultDTO.errorOf(2100, "用户名不能为空");
        }
        if( StringUtils.isBlank(password)) {
            return ResultDTO.errorOf(2100, "密码不能为空");
        }
        // 从SecurityUtils里边创建一个 subject
        Subject subject = SecurityUtils.getSubject();
        // 在认证提交前准备 token（令牌）
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        // 执行认证登陆
        try {
            subject.login(token);
        } catch (UnknownAccountException uae) {
            return ResultDTO.errorOf(2100, "未知账户");
        } catch (IncorrectCredentialsException ice) {
            return ResultDTO.errorOf(2100, "密码不正确");
        } catch (LockedAccountException lae) {
            return ResultDTO.errorOf(2100, "账户已锁定");
        } catch (ExcessiveAttemptsException eae) {
            return ResultDTO.errorOf(2100, "用户名或密码错误次数过多");
        } catch (AuthenticationException ae) {
            return ResultDTO.errorOf(2100, "用户名或密码错误");
        }
        if (subject.isAuthenticated()) {
            User user = userService.getByName(username);
            //登录成功，写cookie和session
            response.addCookie(new Cookie("token", user.getToken()));
            request.getSession().setAttribute("user", user);
            return ResultDTO.okOf();
        } else {
            token.clear();
            return ResultDTO.errorOf(2100, "登录失败！");
        }
    }


    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
}
