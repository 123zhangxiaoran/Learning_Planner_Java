package com.ai.dto;

import lombok.Data;

import java.util.List;

@Data
public class ReportPageDataRequestDTO {
    private Integer userid;
    private List<SkillReportDTO> skills;
}
