package com.ai.service.impl;

import com.ai.entity.SysAdmin;
import com.ai.mapper.SysAdminMapper;
import com.ai.service.SysAdminService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class SysAdminServiceImpl extends ServiceImpl<SysAdminMapper,SysAdmin>
        implements SysAdminService{
}
