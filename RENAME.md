一、项目基础信息

- 项目名称：AI学路规划系统
- 英文项目名：learning-planner
- 基础包路径：com.ai.learning
- 主启动类：LearningPlannerApplication

二、核心配置文件说明

1. 多环境配置文件

- 主配置：application.yml（用于切换环境）
- 开发环境：application-dev.yml（本地调试，开启SQL日志、Debug级别日志）
- 生产环境：application-prod.yml（关闭调试日志，自动生成日志文件）

2. 数据库配置

- 数据库名称：learning_planner
- 初始化脚本：resources/db/schema.sql（建表语句，项目可自动执行）

3. 日志配置

- 日志文件路径：logs/learning-planner.log（项目自动创建logs文件夹，无需手动新建）
- 日志级别：开发环境Debug，生产环境Info，仅保留关键业务日志

三、项目包结构

com.ai.learning
├── config      配置类（Redis、跨域等）
├── controller  接口控制层
├── entity      数据库实体类
├── mapper      数据访问层
├── service     业务层
├── common      通用工具（统一返回值、全局异常）
└── util        工具类

四、关键配置备注

1.MyBatis-Plus：开发环境开启控制台SQL打印，生产环境关闭，避免控制台刷屏
2.Redis：无需配置忽略非空字段，Jackson默认序列化忽略null值
3.数据库脚本：仅建表，无业务数据，运行时数据由程序自动生成，外键约束可根据代码逻辑自行维护