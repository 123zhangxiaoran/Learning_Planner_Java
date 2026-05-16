package com.ai.dto;

import lombok.Data;

import java.util.List;

@Data
public class LearningPathDTO {
    private String skill_name;
    private String job_name;
    private List<String> dimensions;
    private Long user_id;
    private String userinput;
}
