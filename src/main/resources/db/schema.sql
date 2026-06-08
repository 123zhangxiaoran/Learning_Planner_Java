CREATE DATABASE IF NOT EXISTS learning_planner DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE learning_planner;

/*==================== 用户基础信息表 ====================*/
DROP TABLE IF EXISTS user;
CREATE TABLE user
(
    user_id     BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    phone      VARCHAR(20) UNIQUE COMMENT '手机号',
    password    VARCHAR(128) COMMENT '密码',
    nickname    VARCHAR(32) NOT NULL COMMENT '昵称',
    salt       VARCHAR(64) COMMENT '盐值',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户基础信息表';

/*==================== 用户目标岗位表 ====================*/
DROP TABLE IF EXISTS user_career_goal;
CREATE TABLE user_career_goal
(
    user_id   BIGINT NOT NULL COMMENT '用户ID（关联user表）',
    position1 VARCHAR(20) COMMENT '岗位名称1',
    position2 VARCHAR(20) COMMENT '岗位名称2',
    position3 VARCHAR(20) COMMENT '岗位名称3',
    PRIMARY KEY (user_id),
    FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户目标岗位表';

/*==================== 题目表 ====================*/
DROP TABLE IF EXISTS questions;
CREATE TABLE questions (
    id VARCHAR(32) PRIMARY KEY COMMENT '题目ID（UUID）',
    question_text TEXT NOT NULL COMMENT '题干',
    options JSON COMMENT '选项（JSON格式）',
    correct_answer VARCHAR(255) NOT NULL COMMENT '正确答案',
    explanation TEXT COMMENT '解析',
    question_type ENUM('choice','judge','fill','analysis') NOT NULL COMMENT '题型',
    difficulty_score INT NOT NULL COMMENT '题目难度（0-100）',
    skill_name VARCHAR(50) NOT NULL COMMENT '关联的技能名称（与user_learning_progress.skill_name对应）',
    knowledge_name VARCHAR(50) NOT NULL COMMENT '关联的知识点名称（与user_learning_progress.knowledge_name对应）',
    job_name VARCHAR(20) NOT NULL COMMENT '关联的岗位名称（与user_learning_progress.job_name对应）',
    INDEX idx_skill_knowledge (skill_name, knowledge_name, job_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目表';

/*==================== 用户答题记录 ====================*/
DROP TABLE IF EXISTS user_answers;
CREATE TABLE user_answers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID（与user_career_goal.user_id一致）',
    question_id VARCHAR(32) NOT NULL COMMENT '题目ID',
    is_correct BOOLEAN COMMENT '是否正确',
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE,
    INDEX idx_user (user_id),
    INDEX idx_question (question_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户答题记录';

/*==================== 用户学习进度详情表 ====================*/
DROP TABLE IF EXISTS user_learning_progress;
CREATE TABLE user_learning_progress
(
    user_id         BIGINT NOT NULL COMMENT '用户ID',
    skill_name      VARCHAR(50) NOT NULL COMMENT '技能名称',
    knowledge_name  VARCHAR(50) NOT NULL COMMENT '知识点名称',
    job_name        VARCHAR(20) NOT NULL COMMENT '岗位名称',
    score           INT DEFAULT 0 COMMENT '评分（0-100）',
    PRIMARY KEY (user_id, job_name, skill_name, knowledge_name)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户学习进度详情表';