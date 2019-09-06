package com.yichen.community.mapper;

import com.yichen.community.model.Question;

import java.util.List;

public interface QuestionExtMapper {
    int increView(Question question);

    int increCommentCount(Question dbQuestion);

    List<Question> selectRelatedTags(Question question);
}
