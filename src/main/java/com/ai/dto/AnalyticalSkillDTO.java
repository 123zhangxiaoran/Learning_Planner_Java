package com.ai.dto;

import lombok.Data;

import java.util.List;

@Data
public class AnalyticalSkillDTO {
    private String text;
    private List<String> job_names;
}
