package com.ai.dto;

import lombok.Data;

@Data
public class DeleteSkillDTO {
    private Long user_id;
    private String skill_name;
    private String job_name;
}
