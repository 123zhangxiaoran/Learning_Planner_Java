CREATE DATABASE IF NOT EXISTS learning_planner DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE learning_planner;

/*==================== 管理员表 ====================*/
DROP TABLE IF EXISTS sys_admin;
CREATE TABLE sys_admin
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '管理员ID',
    username    VARCHAR(32)  NOT NULL UNIQUE COMMENT '账号',
    password    VARCHAR(128) NOT NULL COMMENT '密码(加密存储)',
    nickname    VARCHAR(32) COMMENT '昵称',
    status      TINYINT  DEFAULT 1 COMMENT '0-禁用 1-正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='系统管理员表';

/*==================== 用户基础信息表 ====================*/
DROP TABLE IF EXISTS user;
CREATE TABLE user
(
    user_id     BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    phone      VARCHAR(20) UNIQUE COMMENT '手机号',
    password    VARCHAR(128) NOT NULL COMMENT '密码',
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
    user_id               BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '关联用户ID',
    position1      VARCHAR(64) COMMENT '岗位名称1',
    position2      VARCHAR(64) COMMENT '岗位名称2',
    position3      VARCHAR(64) COMMENT '岗位名称3',
    progress1      INT DEFAULT 0 COMMENT '岗位1完成度(0-100)',
    progress2      INT DEFAULT 0 COMMENT '岗位2完成度(0-100)',
    progress3      INT DEFAULT 0 COMMENT '岗位3完成度(0-100)',
    UNIQUE KEY uk_user_id (user_id),
    INDEX idx_user_id (user_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户目标岗位表';

/*==================== 用户学习进度详情表 ====================*/
DROP TABLE IF EXISTS user_learning_progress;
CREATE TABLE user_learning_progress
(
    user_id         BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '关联用户ID',
    position_name   VARCHAR(64) NOT NULL COMMENT '岗位名称',
    course_name     VARCHAR(128) NOT NULL COMMENT '课程/知识点名称',
    progress        INT DEFAULT 0 COMMENT '课程完成度(0-100)',
    UNIQUE KEY uk_user_position_course (user_id, position_name, course_name),
    INDEX idx_user_position (user_id, position_name)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户学习进度详情表';