package com.ai.controller;

import com.ai.common.Result;
import com.ai.dto.AgentTextDTO;
import com.ai.dto.AnalyticalSkillDTO;
import com.ai.dto.GetSkillsDTO;
import com.ai.service.AgentService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/agent")
public class AgentController {

    @Resource
    private AgentService agentService;

    // 接收前端页面1的会话返回岗位名称
    @PostMapping("/searchJobs")
    public Result<String> getJobsByMajor(@RequestBody AgentTextDTO dto) {
        return agentService.getJobsByMajor(dto);
    }

    // 保存前端页面1用户选择岗位名称
    @PostMapping("/savejob")
    public Result<String> saveJobs(@RequestBody AgentTextDTO dto) {
        List<String> jobs = dto.getJobs();
        Long userId = dto.getUserId();
        String jobToken = dto.getJobToken();
        return agentService.saveJobs(jobs, userId, jobToken);
    }

    //  接收前端页面2的会话返回技能
    @PostMapping("/searchSkills")
    public Result<String> getSkillsByJob(@RequestBody GetSkillsDTO dto) {
        return agentService.getSkillsByJob(dto);
    }

    //  接收前端页面2的会话分析技能
    @PostMapping("/submitMessage")
    public Result<String> analyticalSkill(@RequestBody AnalyticalSkillDTO dto) {
        return agentService.getSkill(dto);
    }

    //  本地不存在读取数据库拿已选择的岗位名称
    @GetMapping("/userJobData/{userId}")
    public Result<Object> getUserJobData(@PathVariable Long userId) {
        return agentService.getUserJobData(userId);
    }
}