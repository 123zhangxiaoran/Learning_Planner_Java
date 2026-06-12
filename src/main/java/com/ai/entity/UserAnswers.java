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
@TableName("user_answers")
public class UserAnswers implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /// 用户id
    private Long userId;

    /// 题目id
    private String questionId;

    /// 题目状态
    private Integer isCorrect;
}
