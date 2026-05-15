package com.ai.service;

import com.ai.common.Result;
import com.ai.dto.AgentTextDTO;
import com.ai.dto.AnalyticalSkillDTO;
import com.ai.dto.FetchSkillKnowDTO;
import com.ai.dto.GetSkillsDTO;
import com.ai.entity.UserCareerGoal;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface AgentService extends IService<UserCareerGoal> {
    //  接收页面1的会话
    Result<String> getJobsByMajor(AgentTextDTO dto);
    //  保存用户选择的岗位
    Result<String> saveJobs(List<String>jobs, Long userId, String jobToken);
    //  接收页面2的会话
    Result<String> getSkillsByJob(GetSkillsDTO dto);
    //  页面2分析技能
    Result<String> getSkill(AnalyticalSkillDTO dto);
    //  页面2拿已选岗位数据
    Result<Object> getUserJobData(Long userId);
    //  页面2找技能具体的知识点
    Result<String> fectchSkill(FetchSkillKnowDTO dto);
}