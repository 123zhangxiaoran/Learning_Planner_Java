package com.ai.service.impl;

import com.ai.common.Result;
import com.ai.entity.User;
import com.ai.mapper.UserMapper;
import com.ai.service.TokenService;
import com.ai.util.JwtUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.ai.common.ResponseCode.PARAM_ERROR;
import static com.ai.common.ResponseCode.UNAUTHORIZED;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl extends ServiceImpl<UserMapper, User>
        implements TokenService {

    private final JwtUtil jwtUtil;


    @Override
    public Result<?> plushToken(HttpServletResponse response, String refreshToken, Boolean isLogout) {
        // 如果是退出操作，不返回新token
        if (isLogout != null && isLogout) {
            return Result.success(null);
        }
        try {
            Claims claims = jwtUtil.parseRefreshToken(refreshToken);
            Long id = Long.valueOf(claims.getSubject());
            String token = jwtUtil.generateAccessToken(id);
            return Result.success(token);
        } catch (ExpiredJwtException e) {
            return Result.fail(UNAUTHORIZED);
        } catch (Exception e) {
            // 其他解析异常（签名错误、格式错误等）
            System.out.println("错误原因:"+e);
            return Result.fail(PARAM_ERROR);
        }
    }
}
