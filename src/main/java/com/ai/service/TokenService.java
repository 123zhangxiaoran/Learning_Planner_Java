package com.ai.service;

import com.ai.common.Result;
import com.ai.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletResponse;

public interface TokenService extends IService<User> {
    //刷新短token
    Result<?> plushToken(HttpServletResponse response, String refreshToken, Boolean isLogout);
}
