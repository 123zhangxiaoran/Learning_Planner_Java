package com.ai.dto;

import lombok.Data;

import java.util.List;

@Data
public class SkillReportDTO {
    private String skill_name;
    private List<KnowledgeItemDTO> items;
}
