package com.ai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ai.entity.SysAdmin;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysAdminMapper extends BaseMapper<SysAdmin> {
    // 这里可以写自定义SQL方法，基础CRUD由BaseMapper提供
}