package com.yichen.community.mapper;

import com.yichen.community.model.Question;

public interface QuestionExtMapper {
    int increView(Question question);

    int increCommentCount(Question dbQuestion);
}
