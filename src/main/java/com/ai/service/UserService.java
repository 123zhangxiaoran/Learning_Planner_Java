package com.ai.service;

import com.ai.common.Result;
import com.ai.dto.UserDTO;
import com.ai.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.transaction.annotation.Transactional;

public interface UserService extends IService<User> {
    //发送验证码
    Result<String> sendCode(String phone);
    //手机验证码登录
    Result<UserDTO> phoneLogin(String phone, String code, HttpServletResponse response);
    //手机号密码登录
    @Transactional
    Result<UserDTO> accountLogin(String phone, String password, HttpServletResponse response);
    //注册
    @Transactional
    Result<UserDTO> sendUser(String phone, String password, String confirmPwd, String code, HttpServletResponse response);
    //退出
    Result<?> logout(HttpServletResponse response, String refreshToken);
}