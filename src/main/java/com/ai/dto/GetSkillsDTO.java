package com.ai.dto;

import lombok.Data;

import java.util.List;

@Data
public class GetSkillsDTO {
    private Long userId;
    private List<String> jobs;
    private String jobToken;
    private Boolean isNews;
    private Boolean newData;
}


