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

//    @Scheduled(cron = "0 0 1 * * ?")
    @Scheduled(fixedRate=10800000)
    private void updateQuestionHotTasks() {
        if (!questionService.updateQuestionHotTopic()) {
            log.info("update question hot topic failed!!!");
        } else  {
            log.info("update question hot topic succeed!!!");
        }
    }
 //   @Scheduled(cron = "0 0 0 * * ?")
    @Scheduled(fixedRate=10800000)
    private void updateTagsHotTasks() {
        tagList = questionService.updateTagHotTopic();
        if(tagList != null || tagList.size() > 0){
            log.info("update tag hot topic succeed!!!");
        } else {
            log.info("update tag hot topic failed!!!");

        }
    }
}