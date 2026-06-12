package com.ai.mapper;

import com.ai.dto.ScoreKeyDTO;
import com.ai.entity.UserLearningProgress;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserLearningProgressMapper extends BaseMapper<UserLearningProgress> {
    boolean batchInsert(@Param("userId") Long userId,
                    @Param("skillName") String skillName,
                    @Param("knowledgeNames") List<String> knowledgeNames,
                    @Param("jobName") String jobName);

    List<UserLearningProgress> selectBatchByKeys(@Param("list") List<ScoreKeyDTO> keys);

    void deleteByIdAndName(@Param("user_id") Integer user_id, @Param("skill_name") String skill_name, @Param("job_name") String job_name);

    void updateUserScore(@Param("user_id") Long user_id,
                           @Param("job_name") String job_name,
                           @Param("skill_name") String skill_name,
                         @Param("knowledge_name") String knowledge_name,
                         @Param("score") int score);
}
