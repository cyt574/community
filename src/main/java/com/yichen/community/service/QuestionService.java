package com.yichen.community.service;

import com.yichen.community.cache.TagCache;
import com.yichen.community.dto.PaginationDTO;
import com.yichen.community.dto.QuestionDTO;
import com.yichen.community.dto.QuestionQueryDTO;
import com.yichen.community.dto.TagDTO;
import com.yichen.community.enums.CommentTypeEnum;
import com.yichen.community.enums.QuestionTypeEnum;
import com.yichen.community.exception.CustomizeErrorCode;
import com.yichen.community.exception.CustomizeException;
import com.yichen.community.mapper.CommentMapper;
import com.yichen.community.mapper.QuestionExtMapper;
import com.yichen.community.mapper.QuestionMapper;
import com.yichen.community.mapper.UserMapper;
import com.yichen.community.model.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    @Autowired
    QuestionMapper questionMapper;

    @Autowired
    QuestionExtMapper questionExtMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    CommentMapper commentMapper;

    public PaginationDTO list(String search, Integer page, Integer size) {
        String tagReg = null;
        if(StringUtils.isNotBlank(search)) {
            String tags[] = StringUtils.split(search,' ');
            tagReg = Arrays.stream(tags).collect(Collectors.joining("|"));
        }

        PaginationDTO<QuestionDTO> paginationDTO = new PaginationDTO();
        QuestionQueryDTO questionQueryDTO = new QuestionQueryDTO();
        //set Search Content
        questionQueryDTO.setSearch(tagReg);
        //set Page and Size

        Integer totalCount = questionExtMapper.countBySearch(questionQueryDTO);
        Integer totalPage;
        if (totalCount % size == 0) {
            totalPage = totalCount / size;
        } else {
            totalPage = totalCount / size + 1;
        }
        if(page < 1) {
            page = 1;
        }
        if(page > totalPage) {
            page = totalPage;
        }
        paginationDTO.setPagination(totalPage, page);

        Integer offset = page < 1 ? 0 : size * (page - 1);
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        questionQueryDTO.setOffset(offset);
        questionQueryDTO.setSize(size);
        List<Question> questionList = questionExtMapper.selectBySearch(questionQueryDTO);


        for (Question question : questionList) {
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setData(questionDTOList);
        return paginationDTO;
    }

    public PaginationDTO list(Long userId, Integer page, Integer size) {

        PaginationDTO<QuestionDTO> paginationDTO = new PaginationDTO();
        QuestionExample questionExample = new QuestionExample();
        questionExample.createCriteria().andCreatorEqualTo(userId);
        Integer totalCount = (int) questionMapper.countByExample(questionExample);

        Integer totalPage;
        if (totalCount % size == 0) {
            totalPage = totalCount / size;
        } else {
            totalPage = totalCount / size + 1;
        }

        if(page < 1) {
            page = 1;
        }

        if(page > totalPage) {
            page = totalPage;
        }

        paginationDTO.setPagination(totalPage, page);

        Integer offset = size * (page - 1);
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        QuestionExample example = new QuestionExample();
        example.createCriteria().andCreatorEqualTo(userId);
        List<Question> questionList = questionMapper.selectByExampleWithRowbounds(example, new RowBounds(offset, size));


        for (Question question : questionList) {
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setData(questionDTOList);
        return paginationDTO;
    }

    public QuestionDTO getById(Long id) {
        Question question = questionMapper.selectByPrimaryKey(id);
        if(question == null) {
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question, questionDTO);
        User user = userMapper.selectByPrimaryKey(question.getCreator());
        questionDTO.setUser(user);
        return questionDTO;
    }

    public void createOrUpdate(Question question) {
        if(question.getId() == null) {
            question.setViewCount(0);
            question.setCommentCount(0);
            question.setLikeCount(0);
            question.setHotIn7d(0);
            question.setHotIn15d(0);
            question.setHotIn30d(0);
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            questionMapper.insert(question);
        } else {
            QuestionExample example = new QuestionExample();
            example.createCriteria().andIdEqualTo(question.getId());

            Question updateQuestion = new Question();
            updateQuestion.setGmtModified(System.currentTimeMillis());
            updateQuestion.setTag(question.getTag());
            updateQuestion.setDescription(question.getDescription());
            updateQuestion.setTitle(question.getTitle());

            int updated = questionMapper.updateByExampleSelective(updateQuestion, example);
            if(updated != 1) {
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
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
        if(StringUtils.isBlank(questionDTO.getTag())) {
            return new ArrayList<>();
        }
        String tags[] = StringUtils.split(questionDTO.getTag(),',');
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

    public PaginationDTO list(String search, Integer page, Integer size, Integer type, String tag) {
        String tagReg = null;
        if(StringUtils.isNotBlank(search)) {
            String tags[] = StringUtils.split(search,' ');
            tagReg = Arrays.stream(tags).collect(Collectors.joining("|"));
        }

        PaginationDTO<QuestionDTO> paginationDTO = new PaginationDTO();
        QuestionQueryDTO questionQueryDTO = new QuestionQueryDTO();
        //set Search Content
        questionQueryDTO.setSearch(tagReg);

        if(StringUtils.isNotBlank(tag)) {
            String finalTag = StringUtils.join("^"+tag+"$|"+tag+",|,"+tag);
            questionQueryDTO.setTag(finalTag);
        }


        if(type != null) {
            if(type.equals(QuestionTypeEnum.ZERO.getType())) {
                questionQueryDTO.setCommentCount(0);
            } else if (type.equals(QuestionTypeEnum.HOT_IN_30D.getType())){
                questionQueryDTO.setOrderRule("hot_in_30d desc");
            } else if (type.equals(QuestionTypeEnum.HOT_IN_15D.getType())){
                questionQueryDTO.setOrderRule("hot_in_15d desc");
            } else if (type.equals(QuestionTypeEnum.HOT_IN_7D.getType())){
                questionQueryDTO.setOrderRule("hot_in_7d desc");
            } else if (type.equals(QuestionTypeEnum.HOT_IN_ALL.getType())){
                questionQueryDTO.setOrderRule("view_count desc");
            } else if (type.equals(QuestionTypeEnum.RECOMMEND.getType())) {
                questionQueryDTO.setOrderRule("like_count desc");
            }
        }
        //set Page and Size
        Integer totalCount = questionExtMapper.countBySearch(questionQueryDTO);
        Integer totalPage;
        if (totalCount % size == 0) {
            totalPage = totalCount / size;
        } else {
            totalPage = totalCount / size + 1;
        }
        if(page < 1) {
            page = 1;
        }
        if(page > totalPage) {
            page = totalPage;
        }
        paginationDTO.setPagination(totalPage, page);

        Integer offset = page < 1 ? 0 : size * (page - 1);


        List<QuestionDTO> questionDTOList = new ArrayList<>();



        questionQueryDTO.setOffset(offset);
        questionQueryDTO.setSize(size);

        List<Question> questionList = questionExtMapper.selectBySearch(questionQueryDTO);


        for (Question question : questionList) {
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setData(questionDTOList);
        return paginationDTO;
    }

    public boolean updateQuestionHotTopic() {
        long nowTime = System.currentTimeMillis();
        long sevenDayEarly = nowTime - 60 * 60 * 24 * 7 * 1000L;
        long fifteenDayEarly = nowTime - 60 * 60 * 24 * 15 * 1000L;
        long thirtyDayEarly = nowTime - 60 * 60 * 24 * 30 * 1000L;

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
        Map<String,Integer> tagsMap= new HashMap<>();
        for (Question question : questions) {
            String[] tags = StringUtils.split(question.getTag(), ",");
            for (String tag : tags) {
                if(tagsMap.containsKey(tag)) {
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
}
