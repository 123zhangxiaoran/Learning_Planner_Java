package com.ai.controller;

import com.ai.dto.PhoneCodeLoginDTO;
import com.ai.dto.PhoneLoginDTO;
import com.ai.dto.UserDTO;
import com.ai.dto.RegisterDTO;
import com.ai.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import com.ai.common.Result;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    /*
      发送手机验证码
     */
    @PostMapping("/code")
    public Result<String> sendCode(@RequestBody PhoneCodeLoginDTO dto) {
        //从dto里面拿参数
        String phone = dto.getPhone();
        return userService.sendCode(phone);
    }

    /*
      手机验证码登录
     */
    @PostMapping("/phoneLogin")
    public Result<UserDTO> phoneLogin(@RequestBody PhoneCodeLoginDTO dto, HttpServletResponse response) {
        //从dto里面拿参数
        String phone = dto.getPhone();
        String code = dto.getCode();
        return userService.phoneLogin(phone,code,response);
    }

    /*
      手机号密码登录
     */
    @PostMapping("/accountLogin")
    public Result<UserDTO> accountLogin(@RequestBody PhoneLoginDTO dto, HttpServletResponse response) {
        //从dto里面拿参数
        String phone = dto.getPhone();
        String password = dto.getPassword();
        return userService.accountLogin(phone,password,response);
    }

    /*
      注册
     */
    @PostMapping("/sendRegisterCode")
    public Result<UserDTO> sendUser(@RequestBody RegisterDTO dto, HttpServletResponse response) {
        //从dto里面拿参数
        String phone = dto.getPhone();
        String password = dto.getPassword();
        String confirmPwd = dto.getConfirmPwd();
        String code = dto.getCode();
        return userService.sendUser(phone,password,confirmPwd,code,response);
    }

    /*
      退出
     */
    @PostMapping("/logout")
    public Result<?> logout(
            HttpServletResponse response,
            @CookieValue(value = "refreshToken", required = false) String refreshToken
    ){
        return userService.logout(response, refreshToken);
    }
}