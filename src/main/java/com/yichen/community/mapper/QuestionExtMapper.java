package com.yichen.community.mapper;

import com.yichen.community.dto.QuestionQueryDTO;
import com.yichen.community.model.Question;

import java.util.List;

public interface QuestionExtMapper {
    int increView(Question question);

    int increCommentCount(Question dbQuestion);

    List<Question> selectRelatedTags(Question question);

    Integer countBySearch(QuestionQueryDTO questionQueryDTO);

    List<Question> selectBySearch(QuestionQueryDTO questionQueryDTO);

    int updateHotById(Question question);

    int updateHotTopic(Question question);
}
