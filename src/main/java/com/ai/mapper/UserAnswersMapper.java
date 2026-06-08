package com.ai.mapper;

import com.ai.entity.UserAnswers;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserAnswersMapper extends BaseMapper<UserAnswers> {
    // 新增批量插入方法
    void batchInsert(List<UserAnswers> list);
}