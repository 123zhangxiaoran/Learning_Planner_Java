package com.ai.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.util.List;

@Data
public class QuestionWithAnswerStatusDTO {
    private String Id;
    private Integer isCorrect;
    private String questionId;
    private String questionText;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> options;
    private String correctAnswer;
    private String explanation;
    private String questionType;
    private String skillName;
    private String knowledgeName;
    private String jobName;
}
