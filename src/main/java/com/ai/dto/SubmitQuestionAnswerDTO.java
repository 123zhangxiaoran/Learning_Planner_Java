package com.ai.dto;

import lombok.Data;

@Data
public class SubmitQuestionAnswerDTO {
    private Long user_id;
    private String question_id;
    private int is_correct;
    private String question_type;
    private String job_name;
    private String skill_name;
    private String knowledge_name;
}
