package com.ai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user")
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /// 主键
    @TableId(value = "user_id",type = IdType.AUTO)
    private Long userId;

    /// 用户账号
    private String phone;

    /// 用户密码
    private String password;

    /// 用户昵称
    private String nickname;

    /// 账号创造时间
    private LocalDateTime createTime;

    /// 账号最后活跃时间
    private LocalDateTime updateTime;

    /// 密码岩
    private String salt;
    }
