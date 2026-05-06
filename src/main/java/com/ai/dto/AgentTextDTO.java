package com.ai.dto;

import lombok.Data;

import java.util.List;

@Data
public class AgentTextDTO {
    private String major;
    private List<String> jobs;
    private Long userId;
    private String jobToken;
}
