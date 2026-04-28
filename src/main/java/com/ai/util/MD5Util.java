/**
 * 密码加密
 */
package com.ai.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {
    /**
     * MD5 加密方法
     * @param plaintext 明文密码
     * @return 加密后的密文（32位小写）
     */
    public static String md5(String plaintext) {
        if (plaintext == null || plaintext.isEmpty()) {
            return "";
        }
        try {
            // 获取MD5加密实例
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(plaintext.getBytes());
            StringBuilder sb = new StringBuilder();
            // 遍历字节数组，转成16进制字符串
            for (byte b : bytes) {
                String hex = Integer.toHexString(b & 0xFF);
                if (hex.length() == 1) {
                    sb.append("0");
                }
                sb.append(hex);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5加密失败", e);
        }
    }

    /**
     * 加盐MD5加密（更安全，推荐使用）
     * @param plaintext 明文密码
     * @param salt 盐值（可以是用户手机号、随机字符串等）
     * @return 加密后的密文
     */
    public static String md5WithSalt(String plaintext, String salt) {
        // 明文 + 盐值 拼接后再加密，防止彩虹表破解
        return md5(plaintext + salt);
    }
}
