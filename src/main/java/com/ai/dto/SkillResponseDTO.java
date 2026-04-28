package com.ai.dto;

import lombok.Data;

import java.util.List;

@Data
public class SkillResponseDTO {
    private boolean success;
    private List<String> query;
    private List<JobSkillDTO> skills;
}
