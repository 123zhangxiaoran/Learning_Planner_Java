package com.ai.service;

import com.ai.common.Result;
import com.ai.dto.FetchSkillKnowDTO;
import com.ai.dto.ReportPageDataRequestDTO;
import com.ai.dto.ReportPageDataResponseDTO;
import com.ai.dto.UserDTO;
import com.ai.entity.User;
import com.ai.entity.UserLearningProgress;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    //获取用户画像评分
    Result<ReportPageDataResponseDTO> reportData(ReportPageDataRequestDTO dto);
    //获取用户选择的技能数据
    Result<List<UserLearningProgress>> userSelectedSkills(Long userId);
    //获取用户已有的技能数据
    Result<FetchSkillKnowDTO> userSkills(Long userId);
}