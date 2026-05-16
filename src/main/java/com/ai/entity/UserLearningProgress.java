package com.ai.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_learning_progress")
public class UserLearningProgress implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    ///  联合主键
    private Long userId;
    private String skillName;
    private String knowledgeName;

    ///  知识点评分
    private Integer score;
}
