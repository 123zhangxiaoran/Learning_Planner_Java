package com.ai.service.impl;

import com.ai.common.Result;
import com.ai.dto.*;
import com.ai.entity.UserCareerGoal;
import com.ai.mapper.UserCareerGoalMapper;
import com.ai.service.AgentService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.ai.common.ResponseCode.FAIL;
import static com.ai.util.RedisUtil.*;

@Service
public class AgentServiceImpl extends ServiceImpl<UserCareerGoalMapper, UserCareerGoal>
        implements AgentService {

    @Resource
    private RestTemplate restTemplate;
    @Resource
    private UserCareerGoalMapper userCareerGoalMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 将岗位名称写入内存
     */
    private void setCareer(Long userId,List<String> career) {
        stringRedisTemplate.opsForHash().put(USER_CAREER_KEY + userId, "Position1", career.get(0));
        if (career.size() > 1 && career.get(1) != null){
            stringRedisTemplate.opsForHash().put(USER_CAREER_KEY + userId, "Position2", career.get(1));
        }else {
            stringRedisTemplate.opsForHash().delete(USER_CAREER_KEY + userId, "Position2");
        }
        if (career.size() > 2 && career.get(2) != null){
            stringRedisTemplate.opsForHash().put(USER_CAREER_KEY + userId, "Position3", career.get(2));
        }else {
            stringRedisTemplate.opsForHash().delete(USER_CAREER_KEY + userId, "Position3");
        }
        stringRedisTemplate.expire(USER_CAREER_KEY + userId, USER_CAREER_KEY_TTL, TimeUnit.SECONDS);
    }

    /**
     * 将更新凭证写入内存
     */
    private void setLoginToken(Long userId,String jobToken) {
        stringRedisTemplate.opsForValue().set(userId + ":saveJobs", jobToken,UPDATA_KEY_TTL, TimeUnit.SECONDS);
    }

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
    public Result<String> saveJobs(List<String> jobs, Long userId, String jobToken) {
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
        boolean Success;
        if(existing != null){
            existing.setPosition1(user.getPosition1());
            existing.setPosition2(user.getPosition2());
            existing.setPosition3(user.getPosition3());
            Success = this.updateById(existing);
        }else {
            Success = this.save(user);
        }
        if (!Success){
            return Result.fail(FAIL);
        }
        setCareer(userId,jobs);
        setLoginToken(userId,jobToken);
        return Result.success("数据保存成功");
    }

    //  根据岗位名称选择技能
    @Override
    public Result<String> getSkillsByJob(GetSkillsDTO dto) {
        dto.setIsNews(true);
        // 设置请求头为 JSON 格式
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Boolean newData = dto.getNewData();
        Long userId = dto.getUserId();
        String jobToken = dto.getJobToken();
        if (newData){
            setCareer(userId, dto.getJobs());
            setLoginToken(userId,dto.getJobToken());
            String result = restTemplate.postForObject(
                    "http://localhost:8000/api/skills/search",
                    new HttpEntity<>(dto, headers),
                    String.class
            );
            return Result.success(result);
        }
        if (stringRedisTemplate.hasKey(userId + ":saveJobs") && !Objects.equals(stringRedisTemplate.opsForValue().get(userId + ":saveJobs"), jobToken)){
            if (stringRedisTemplate.hasKey(USER_CAREER_KEY + userId)){
                List<String> jobs = new ArrayList<>();
                Object position1 = stringRedisTemplate.opsForHash().get(USER_CAREER_KEY + userId, "Position1");
                jobs.add((String) position1);
                if (stringRedisTemplate.opsForHash().hasKey(USER_CAREER_KEY + userId, "Position2")) {
                    Object position2 = stringRedisTemplate.opsForHash().get(USER_CAREER_KEY + userId, "Position2");
                    jobs.add((String) position2);
                }
                if (stringRedisTemplate.opsForHash().hasKey(USER_CAREER_KEY + userId, "Position3")) {
                    Object position3 = stringRedisTemplate.opsForHash().get(USER_CAREER_KEY + userId, "Position3");
                    jobs.add((String) position3);
                }
                dto.setJobs(jobs);
            }else {
                UserCareerGoal user = userCareerGoalMapper.selectById(userId);
                List<String> jobs = new ArrayList<>();
                jobs.add(user.getPosition1());
                if (user.getPosition2() != null) {
                    jobs.add(user.getPosition2());
                }
                if (user.getPosition3() != null) {
                    jobs.add(user.getPosition3());
                }
                setCareer(userId,jobs);
                dto.setJobs(jobs);
            }
            dto.setIsNews(false);
            dto.setJobToken(stringRedisTemplate.opsForValue().get(userId + ":saveJobs"));
        }
        String result = restTemplate.postForObject(
                "http://localhost:8000/api/skills/search",
                new HttpEntity<>(dto, headers),
                String.class
        );
        return Result.success(result);
    }

    //  根据用户信息及技能匹配学习资料
    @Override
    public Result<String> getSkill(AnalyticalSkillDTO dto) {
        // 设置请求头为 JSON 格式
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 包装成 HttpEntity
        String result = restTemplate.postForObject(
                "http://localhost:8000/api/skill/analytical",
                new HttpEntity<>(dto, headers),
                String.class
        );
        return Result.success(result);
    }

    //  根据用户唯一id返回数据
    @Override
    public Result<Object> getUserJobData(Long userId) {
        GetSkillsDTO jobs = new GetSkillsDTO();
        List<String> NewJobs = new ArrayList<>();
        UserCareerGoal goal = userCareerGoalMapper.selectById(userId);
        if (goal.getPosition1() == null) return Result.fail(FAIL);
        NewJobs.add(goal.getPosition1());
        if (goal.getPosition2() != null) NewJobs.add(goal.getPosition2());
        if (goal.getPosition3() != null) NewJobs.add(goal.getPosition3());
        jobs.setJobs(NewJobs);

        // 设置请求头为 JSON 格式
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String result = restTemplate.postForObject(
                "http://localhost:8000/api/skills/search",
                new HttpEntity<>(jobs, headers),
                String.class
        );
        setCareer(userId,NewJobs);
        return Result.success(result);
    }

    @Override
    public Result<String> fectchSkill(FetchSkillKnowDTO dto) {
        // 设置请求头为 JSON 格式
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 包装成 HttpEntity
        String result = restTemplate.postForObject(
                "http://localhost:8000/api/skill/fetchSkill",
                new HttpEntity<>(dto, headers),
                String.class
        );

        return Result.success(result);
    }

    //  保存用户选择的技能
}
