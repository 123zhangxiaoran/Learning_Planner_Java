package com.ai.dto;

import lombok.Data;

@Data
public class FetchSkillKnowDTO {
    private Long user_id;
    private String job_names;
    private String selected_skill;
}
