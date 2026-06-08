package com.ai.config.async;

import com.ai.entity.Questions;
import com.ai.entity.UserAnswers;
import com.ai.mapper.QuestionsMapper;
import com.ai.mapper.UserAnswersMapper;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionAsyncService {

    @Resource
    private QuestionsMapper questionMapper;
    @Resource
    private UserAnswersMapper userAnswersMapper;

    @Async("questionExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void saveQuestionsBatch(List<Questions> questions, Long userId) {
        if (questions == null || questions.isEmpty()) {
            return;
        }
        // 1. 批量插入题目
        questionMapper.batchInsert(questions);

        // 2. 如果 userId 不为 null，则插入用户答题记录（未答题状态）
        if (userId != null) {
            List<UserAnswers> userAnswers = new ArrayList<>();
            for (Questions q : questions) {
                UserAnswers ua = new UserAnswers();
                ua.setUserId(userId);
                ua.setQuestionId(q.getId());
                ua.setIsCorrect(null);
                userAnswers.add(ua);
            }
            userAnswersMapper.batchInsert(userAnswers);
        }
    }
}