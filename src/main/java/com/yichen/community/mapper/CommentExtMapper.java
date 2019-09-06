package com.yichen.community.mapper;

import com.yichen.community.model.Comment;
import com.yichen.community.model.CommentExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

@Mapper
public interface CommentExtMapper {
    int increCommentCount(Comment comment);
}