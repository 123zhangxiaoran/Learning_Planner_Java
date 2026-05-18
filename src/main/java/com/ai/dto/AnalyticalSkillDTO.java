package com.ai.dto;

import lombok.Data;

import java.util.List;

@Data
public class AnalyticalSkillDTO {
    private String skill_name;
    private String job_name;
    private String userinput;
    private Long user_id;
    private List<List<String>> dimensions;
}
