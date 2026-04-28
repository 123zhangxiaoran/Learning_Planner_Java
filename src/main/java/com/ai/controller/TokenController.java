package com.ai.controller;

import com.ai.common.Result;
import com.ai.service.TokenService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class TokenController {

    @Resource
    private TokenService tokenService;

    /*
      刷新token
     */
    @PostMapping("/token")
    public Result<?> plushToken(
            HttpServletResponse response,
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            @RequestBody Map<String, Boolean> payload
            ){
        Boolean isLogout = payload.get("isLogout");
        return tokenService.plushToken(response, refreshToken,isLogout);
    }
}
