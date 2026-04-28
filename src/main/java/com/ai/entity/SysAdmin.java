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
@TableName("sys_admin")
public class SysAdmin implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /// 主键
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    /// 管理员账号
    private String userName;

    /// 管理员密码
    private String password;

    /// 管理员昵称
    private String nickName;

    /// 管理员状态
    private Integer status;

    /// 账号创造时间
    private LocalDateTime createTime;

    /// 账号最后活跃时间
    private LocalDateTime updateTime;

    ///
}
