/**
 * 生成解析JWT
 */
package com.ai.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

import static com.ai.common.JwtConstant.ACCESS_EXPIRATION;
import static com.ai.common.JwtConstant.REFRESH_EXPIRATION;
import static com.ai.util.RandomUtil.randomHourOffsetMillis;


@Component
public class JwtUtil {

    @Value("${jwt.access-secret}")
    private String accessSecret;

    @Value("${jwt.refresh-secret}")
    private String refreshSecret;

    //  生成 Access Token
    public String generateAccessToken(Long playerId) {
        SecretKey key = Keys.hmacShaKeyFor(accessSecret.getBytes());
        String jti = UUID.randomUUID().toString();

        return Jwts.builder()
                .setSubject(playerId.toString())
                .setId(jti)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_EXPIRATION + randomHourOffsetMillis()))
                .signWith(key)
                .compact();
    }

    //  生成 Refresh Token
    public String generateRefreshToken(Long playerId) {
        SecretKey key = Keys.hmacShaKeyFor(refreshSecret.getBytes());
        String jti = UUID.randomUUID().toString();

        return Jwts.builder()
                .setSubject(playerId.toString())
                .setId(jti)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION))
                .signWith(key)
                .compact();
    }

    //  解析token
    public Claims parseToken(String token, boolean isRefreshToken) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("JWT Token 不能为 null 或空字符串");
        }
        
        SecretKey key = isRefreshToken
                ? Keys.hmacShaKeyFor(refreshSecret.getBytes())
                : Keys.hmacShaKeyFor(accessSecret.getBytes());

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    //  便捷方法：专门验证 Access Token
    public Claims parseAccessToken(String token) {
        return parseToken(token, false);
    }

    //  便捷方法：专门验证 Refresh Token
    public Claims parseRefreshToken(String token) {
        return parseToken(token, true);
    }
}
