package com.ai.service.impl;

import com.ai.common.Result;
import com.ai.dto.*;
import com.ai.entity.UserCareerGoal;
import com.ai.mapper.UserCareerGoalMapper;
import com.ai.service.AgentService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static com.ai.common.ResponseCode.FAIL;

@Service
public class AgentServiceImpl extends ServiceImpl<UserCareerGoalMapper, UserCareerGoal>
        implements AgentService {

    @Resource
    private RestTemplate restTemplate;
    @Resource
    private UserCareerGoalMapper userCareerGoalMapper;


    //  专业筛选岗位名称
    @Override
    public Result<String> getJobsByMajor(AgentTextDTO dto) {
        // 设置请求头为 JSON 格式
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String result = restTemplate.postForObject(
                "http://localhost:8000/api/jobs/search",
                new HttpEntity<>(dto, headers),
                String.class
        );
        return Result.success(result);
    }

    //  保存用户选择的岗位名称
    @Override
    public Result<String> saveJobs(List<String> jobs, Long userId) {
        //  判断是否是空值
        if(jobs == null || jobs.isEmpty() || jobs.size() > 3){
            return Result.fail(FAIL);
        }
        //  判断是否已有数据
        UserCareerGoal existing = userCareerGoalMapper.selectOne(Wrappers.<UserCareerGoal>lambdaQuery().eq(UserCareerGoal::getUserId, userId));
        //  赋值准备加入/更新数据库
        UserCareerGoal user = new UserCareerGoal();
        user.setUserId(userId);
        user.setPosition1(jobs.get(0));
        user.setPosition2(jobs.size() > 1 ? jobs.get(1) : null);
        user.setPosition3(jobs.size() > 2 ? jobs.get(2) : null);
        //  判断数据保存成功或失败
        boolean success;
        if(existing != null){
            existing.setPosition1(user.getPosition1());
            existing.setPosition2(user.getPosition2());
            existing.setPosition3(user.getPosition3());
            success = this.updateById(existing);
        }else {
            success = this.save(user);
        }
        if (!success){
            return Result.fail(FAIL);
        }
        return Result.success("数据保存成功");
    }

    //  根据岗位名称选择技能
    @Override
    public Result<String> getSkillsByJob(GetSkillsDTO dto) {
        // 设置请求头为 JSON 格式
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String result = restTemplate.postForObject(
                "http://localhost:8000/api/skills/search",
                new HttpEntity<>(dto, headers),
                String.class
        );
        return Result.success(result);
    }

    //  根据用户信息匹配技能
    @Override
    public Result<String> getSkill(AnalyticalSkillDTO dto) {
        // 设置请求头为 JSON 格式
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 包装成 HttpEntity
        HttpEntity<AnalyticalSkillDTO> entity = new HttpEntity<>(dto, headers);
        String result = restTemplate.postForObject(
                "http://localhost:8000/api/skill/analytical",
                entity,
                String.class
        );
        return Result.success(result);
    }

    //  根据用户唯一id返回数据
    @Override
    public Result<Object> getUserJobData(Long userId) {
        GetSkillsDTO jobs = new GetSkillsDTO();
        if (jobs.getJobs() == null) {
            jobs.setJobs(new ArrayList<>());
        }
        UserCareerGoal goal = userCareerGoalMapper.selectById(userId);
        String job1 = goal.getPosition1();
        String job2 = goal.getPosition2();
        String job3 = goal.getPosition3();
        if (job1 == null) return Result.fail(FAIL);
        jobs.getJobs().add(job1);
        if (job2 != null) jobs.getJobs().add(job2);
        if (job3 != null) jobs.getJobs().add(job3);

        // 设置请求头为 JSON 格式
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String result = restTemplate.postForObject(
                "http://localhost:8000/api/skills/search",
                new HttpEntity<>(jobs, headers),
                String.class
        );
        return Result.success(result);
    }

    //  保存用户选择的技能
}
