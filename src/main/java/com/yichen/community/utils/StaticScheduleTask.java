package com.yichen.community.utils;

import com.yichen.community.model.Question;
import com.yichen.community.service.QuestionService;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Configuration
@EnableScheduling
@Log
public class StaticScheduleTask {

    private static List<String> tagList;

    @Autowired
    QuestionService questionService;

    public static List<String> getTagList() {
        return tagList;
    }

    public static void setTagList(List<String> tagList) {
        StaticScheduleTask.tagList = tagList;
    }

    @Scheduled(cron = "0 0 1 * * ?")
    private void updateQuestionHotTasks() {
        if (!questionService.updateQuestionHotTopic()) {
            log.info("update question hot topic failed!!!");
        }
    }
//    @Scheduled(cron = "0 0 0 * * ?")
    @Scheduled(fixedRate=36000000)
    private void updateTagsHotTasks() {
        tagList = questionService.updateTagHotTopic();
        log.info("update question hot topic failed!!!");
    }
}