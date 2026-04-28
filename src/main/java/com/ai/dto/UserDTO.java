package com.ai.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String nickname;
    private String accessToken;
}
