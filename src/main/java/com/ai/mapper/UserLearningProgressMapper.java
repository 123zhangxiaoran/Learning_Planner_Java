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
}
