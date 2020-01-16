package com.yichen.community.advice;

import com.alibaba.fastjson.JSON;
import com.yichen.community.dto.ResultDTO;
import com.yichen.community.exception.CustomizeErrorCode;
import com.yichen.community.exception.CustomizeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

@ControllerAdvice
@Slf4j
public class CustomizeExceptionHandler {

    private final static Integer VALIDATION_ERROR = 999;

    @ExceptionHandler(ConstraintViolationException.class)
    public ResultDTO handleConstraintViolation(HttpServletRequest request, ConstraintViolationException ex) {
        if(isAjax(request)) {
            StringBuffer stringBuffer = new StringBuffer();
            for(ConstraintViolation violation : ex.getConstraintViolations()) {
                stringBuffer.append(violation.getMessage());
            }
            return ResultDTO.errorOf(VALIDATION_ERROR, stringBuffer.toString());
        }
        return null;
    }

    @ExceptionHandler(Exception.class)
    ModelAndView handle(HttpServletRequest request, HttpServletResponse httpServletResponse, Throwable ex, Model model) {
        String contentType = request.getContentType();
        if ("application/json".equals(contentType)) {
            ResultDTO resultDTO = null;
            if (ex instanceof CustomizeException) {
                resultDTO = ResultDTO.errorOf((CustomizeException) ex);
            } else {
                resultDTO = ResultDTO.errorOf(CustomizeErrorCode.SYS_ERROR);
            }

            try {
                httpServletResponse.setStatus(200);
                httpServletResponse.setContentType("application/json");
                httpServletResponse.setCharacterEncoding("UTF-8");
                PrintWriter writer = httpServletResponse.getWriter();
                writer.write(JSON.toJSONString(resultDTO));
                writer.close();
            } catch (IOException e) {

            }
            return null;
        } else {
            if (ex instanceof CustomizeException) {
                model.addAttribute("message", ex.getMessage());
            } else {
                model.addAttribute("message", CustomizeErrorCode.SYS_ERROR.getMessage());
            }
            return new ModelAndView("error");
        }
    }

    public static boolean isAjax(HttpServletRequest httpRequest) {
        return (httpRequest.getHeader("X-Requested-With") != null
                && "XMLHttpRequest"
                .equals(httpRequest.getHeader("X-Requested-With").toString()));
    }
}
