package com.ai.controller;

import com.ai.service.SysAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class SysAdminController {

    @Autowired
    private SysAdminService sysAdminService;

    // 后续在这里补充管理员登录、玩家管理等接口方法
}