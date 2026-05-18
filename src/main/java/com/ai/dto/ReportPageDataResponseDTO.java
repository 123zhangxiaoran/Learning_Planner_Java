package com.ai.dto;

import lombok.Data;

import java.util.List;

@Data
public class ReportPageDataResponseDTO {
    private List<ScoreItemDTO> scores;
}
