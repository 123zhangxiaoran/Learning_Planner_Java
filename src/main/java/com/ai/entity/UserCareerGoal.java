package com.ai.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_career_goal")
public class UserCareerGoal implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /// 主键
    @TableId(value = "user_id",type = IdType.AUTO)
    private Long userId;

    /// 用户岗位名称1
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String position1;

    /// 用户岗位1完成度
    private Integer progress1;

    /// 用户岗位名称2
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String position2;

    /// 用户岗位2完成度
    private Integer progress2;

    /// 用户岗位名称1
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String position3;

    /// 用户岗位1完成度
    private Integer progress3;
}
