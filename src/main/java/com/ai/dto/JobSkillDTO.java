package com.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class JobSkillDTO {
    @JsonProperty("job_name")
    private String jobName;
    private List<SkillDTO> skills;
    private String major;
}
