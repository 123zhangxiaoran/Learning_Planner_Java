package com.ai.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("questions")
public class Questions implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /// 主键
    @TableId(value = "id",type = IdType.AUTO)
    private String Id;

    /// 题集id
    private String questionId;

    /// 问题题干
    private String questionText;

    /// 题目选项
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> options;

    /// 题目答案
    private String correctAnswer;

    /// 题目解析
    private String explanation;

    /// 题目类型
    private String questionType;

    /// 题目难度
    private Integer difficultyScore;

    /// 技能名称
    private String skillName;

    /// 知识点名称
    private String knowledgeName;

    /// 岗位名称
    private String jobName;
}
