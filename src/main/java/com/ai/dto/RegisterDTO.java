package com.ai.dto;

import lombok.Data;

@Data
public class RegisterDTO {
    private String phone;
    private String password;
    private String confirmPwd;
    private String code;
}