package com.yichen.community.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yichen.community.dto.QuestionDTO;
import com.yichen.community.dto.QuestionQueryDTO;
import com.yichen.community.enums.CommentTypeEnum;
import com.yichen.community.exception.CustomizeErrorCode;
import com.yichen.community.exception.CustomizeException;
import com.yichen.community.mapper.CommentMapper;
import com.yichen.community.mapper.QuestionExtMapper;
import com.yichen.community.mapper.QuestionMapper;
import com.yichen.community.mapper.UserMapper;
import com.yichen.community.model.*;
import com.yichen.community.utils.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class QuestionService {

    @Autowired
    QuestionMapper questionMapper;

    @Autowired
    QuestionExtMapper questionExtMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    CommentMapper commentMapper;

    public QuestionDTO getById(Long id) {
        Question question = questionMapper.selectByPrimaryKey(id);
        if (question == null) {
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question, questionDTO);
        User user = userMapper.selectByPrimaryKey(question.getCreator());
        questionDTO.setUser(user);
        return questionDTO;
    }

    public void createOrUpdate(Question question, Long userId) {

        if (question.getId() == null) {
            question.setViewCount(0);
            question.setCommentCount(0);
            question.setLikeCount(0);
            question.setHotIn7d(0);
            question.setHotIn15d(0);
            question.setHotIn30d(0);
            question.setCreator(userId);
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            questionMapper.insert(question);
        } else {
            Question dbQuestion = questionMapper.selectByPrimaryKey(question.getId());
            if (dbQuestion == null) {
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            } else if (userId != dbQuestion.getCreator()) {
                throw new CustomizeException(CustomizeErrorCode.PERMISSION_DENIED);
            }
            QuestionExample example = new QuestionExample();
            example.createCriteria().andIdEqualTo(question.getId());

            Question updateQuestion = new Question();
            updateQuestion.setGmtModified(System.currentTimeMillis());
            updateQuestion.setTag(question.getTag());
            updateQuestion.setDescription(question.getDescription());
            updateQuestion.setTitle(question.getTitle());

            int updated = questionMapper.updateByExampleSelective(updateQuestion, example);
            if (updated != 1) {
                throw new CustomizeException(CustomizeErrorCode.QUESTION_PUBLISH_FAIL);
            }
        }
    }

    public void increView(Long id) {
        Question question = new Question();
        question.setId(id);
        question.setViewCount(1);
        questionExtMapper.increView(question);
    }

    public List<QuestionDTO> selectRelatedTags(QuestionDTO questionDTO) {
        if (StringUtils.isBlank(questionDTO.getTag())) {
            return new ArrayList<>();
        }
        String tags[] = StringUtils.split(questionDTO.getTag(), ',');
        String tagReg = Arrays.stream(tags).collect(Collectors.joining("|"));

        Question question = new Question();
        question.setTag(tagReg);
        question.setId(questionDTO.getId());

        List<Question> questions = questionExtMapper.selectRelatedTags(question);
        List<QuestionDTO> questionDTOS = questions.stream().map(q -> {
            QuestionDTO quest = new QuestionDTO();
            BeanUtils.copyProperties(q, quest);
            return quest;
        }).collect(Collectors.toList());
        return questionDTOS;
    }

    public boolean updateQuestionHotTopic() {
        long nowTime = System.currentTimeMillis();
        long sevenDayEarly = nowTime - TimeUtils.ONE_WEEK_TIME;
        long fifteenDayEarly = nowTime - TimeUtils.FIFTEEN_DAY_TIME;
        long thirtyDayEarly = nowTime - TimeUtils.ONE_MONTH_TIME;

        QuestionExample example = new QuestionExample();
        example.createCriteria().andGmtCreateGreaterThan(thirtyDayEarly);
        List<Question> questions = questionMapper.selectByExample(example);

        List<Comment> sevenDaycommentList = null;
        List<Comment> fifteenDaycommentList = null;
        List<Comment> thirtyDaycommentList = null;

        for (Question question : questions) {
            CommentExample commentExample1 = new CommentExample();
            commentExample1.createCriteria().andGmtCreateGreaterThan(sevenDayEarly)
                    .andParentIdEqualTo(question.getId())
                    .andTypeEqualTo(CommentTypeEnum.QUESTION.getType());
            sevenDaycommentList = commentMapper.selectByExample(commentExample1);


            CommentExample commentExample2 = new CommentExample();
            commentExample2.createCriteria().andGmtCreateGreaterThan(fifteenDayEarly)
                    .andParentIdEqualTo(question.getId())
                    .andTypeEqualTo(CommentTypeEnum.QUESTION.getType());
            fifteenDaycommentList = commentMapper.selectByExample(commentExample2);

            CommentExample commentExample3 = new CommentExample();
            commentExample3.createCriteria().andGmtCreateGreaterThan(thirtyDayEarly)
                    .andParentIdEqualTo(question.getId())
                    .andTypeEqualTo(CommentTypeEnum.QUESTION.getType());
            thirtyDaycommentList = commentMapper.selectByExample(commentExample3);

            question.setHotIn7d(sevenDaycommentList.size());
            question.setHotIn15d(fifteenDaycommentList.size());
            question.setHotIn30d(thirtyDaycommentList.size());
            questionExtMapper.updateHotTopic(question);
            System.out.println(question);
        }


        return true;
    }

    public List<String> updateTagHotTopic() {
        QuestionExample example = new QuestionExample();
        example.createCriteria();
        List<Question> questions = questionMapper.selectByExample(example);
        Map<String, Integer> tagsMap = new HashMap<>();
        for (Question question : questions) {
            String[] tags = StringUtils.split(question.getTag(), ",");
            for (String tag : tags) {
                if (tagsMap.containsKey(tag)) {
                    tagsMap.put(tag, tagsMap.get(tag) + 1);
                } else {
                    tagsMap.put(tag, 1);
                }
            }
        }
        List<Map.Entry<String, Integer>> list = new ArrayList<>(tagsMap.entrySet());
        Collections.sort(list, Comparator.comparing(Map.Entry::getValue));

        List<String> tags = new ArrayList<>();
        for (Map.Entry<String, Integer> mapping : list) {
            tags.add(mapping.getKey());
        }
        Collections.reverse(tags);
        return tags;
    }

    public PageInfo<QuestionDTO> listBySort(String search, Integer page, Integer size, String orderType, String tag) {
        QuestionQueryDTO questionQueryDTO = new QuestionQueryDTO();
        log.info("into function listBySort");
        if (StringUtils.isNotBlank(search)) {
            String tags[] = StringUtils.split(search, ' ');
            String tagReg = Arrays.stream(tags).collect(Collectors.joining("|"));
            questionQueryDTO.setSearch(tagReg);
        }

        if (StringUtils.isNotBlank(tag)) {
            String finalTag = StringUtils.join("^" + tag + "$|" + tag + ",|," + tag);
            questionQueryDTO.setTag(finalTag);
        }

        if (orderType != null) {
            if (orderType.equals("zero")) {
                questionQueryDTO.setCommentCount(0);
            } else if (orderType.equals("recommend")) {
                questionQueryDTO.setOrderRule("like_count desc, view_count desc");
            } else if (orderType.equals("hotAll")) {
                questionQueryDTO.setOrderRule("comment_count desc, view_count desc");
            } else if (orderType.equals("hot1Month")) {
                questionQueryDTO.setOrderRule("hot_in_30d desc, view_count desc");
            } else if (orderType.equals("hot1Week")) {
                questionQueryDTO.setOrderRule("hot_in_7d desc, view_count desc");
            }
        }

        PageHelper.startPage(page, size);
        List<QuestionDTO> questionList = questionExtMapper.selectBySort(questionQueryDTO);
        for (QuestionDTO qusetion : questionList) {
            qusetion.setTimeCreate(TimeUtils.getFormatedDiffTime(qusetion.getGmtCreate(), System.currentTimeMillis()));
        }
        PageInfo<QuestionDTO> questionDTOPageHelper = new PageInfo<>(questionList);
        questionDTOPageHelper.setNavigatePages(5);
        return questionDTOPageHelper;
    }

    public PageInfo<QuestionDTO> list(Long userId, Integer page, Integer size) {

        PageHelper.startPage(page, size);
        List<QuestionDTO> questionList = questionExtMapper.selectByCreatorId(userId);
        for (QuestionDTO questionDTO : questionList) {
            questionDTO.setTimeCreate(TimeUtils.getFormatedDiffTime(questionDTO.getGmtCreate(), System.currentTimeMillis()));
        }

        PageInfo<QuestionDTO> questionDTOPageHelper = new PageInfo<>(questionList);
        questionDTOPageHelper.setNavigatePages(5);
        return questionDTOPageHelper;
    }

}


