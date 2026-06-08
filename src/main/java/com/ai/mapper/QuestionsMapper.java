package com.ai.mapper;

import com.ai.entity.Questions;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface QuestionsMapper extends BaseMapper<Questions> {
    // 新增批量插入方法
    void batchInsert(List<Questions> list);
}
