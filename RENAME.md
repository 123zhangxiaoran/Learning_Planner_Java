# AI学路规划系统后端 (Learning Planner Backend)

## 项目简介

这是一个基于 Spring Boot 3.5 的学习规划平台后端项目，为"学路规划"前端提供完整的 RESTful API 服务。主要功能包括：

- **用户认证**: 手机号验证码登录、密码登录、用户注册、JWT 双 Token 认证
- **职业规划**: AI 辅助职业搜索与推荐、用户职业目标管理
- **技能分析**: 技能查询、技能分析、AI 智能问答
- **系统管理**: 管理员权限控制、数据管理
- **实时同步**: Redis 缓存支持、多端数据实时同步

## 技术栈

| 类别 | 技术 |
|------|------|
| 框架 | Spring Boot 3.5.13 |
| 语言 | Java 17 |
| 构建工具 | Maven |
| ORM 框架 | MyBatis-Plus 3.5.10.1 |
| 数据库 | MySQL 8+ |
| 缓存 | Redis |
| 认证授权 | JWT (jjwt 0.11.5) |
| 工具库 | Hutool 5.8.40 |
| 日志 | SLF4J + Logback |
| HTTP 客户端 | RestTemplate |

## 项目结构

```
com.ai
├── common/              # 通用模块
│   ├── BusinessException.java    # 业务异常类
│   ├── GlobalExceptionHandler.java # 全局异常处理器
│   ├── JwtConstant.java          # JWT 常量定义
│   ├── ResponseCode.java         # 统一响应码
│   └── Result.java               # 统一返回值封装
├── config/              # 配置类
│   ├── RestTemplateConfig.java  # RestTemplate 配置
│   └── WebMvcConfig.java        # Web MVC 配置（跨域、拦截器）
├── controller/          # 控制层
│   ├── AgentController.java     # AI 代理相关接口
│   ├── SysAdminController.java  # 系统管理接口
│   ├── TokenController.java     # Token 刷新接口
│   └── UserController.java      # 用户相关接口
├── dto/                 # 数据传输对象
│   ├── AgentTextDTO.java        # AI 对话 DTO
│   ├── AnalyticalSkillDTO.java  # 技能分析 DTO
│   ├── GetSkillsDTO.java        # 获取技能 DTO
│   ├── PhoneCodeLoginDTO.java   # 手机验证码登录 DTO
│   ├── PhoneLoginDTO.java       # 手机密码登录 DTO
│   ├── RegisterDTO.java         # 用户注册 DTO
│   └── UserDTO.java             # 用户信息 DTO
├── entity/              # 数据库实体类
│   ├── SysAdmin.java            # 管理员实体
│   ├── User.java                # 用户实体
│   └── UserCareerGoal.java      # 用户职业目标实体
├── interceptor/         # 拦截器
│   ├── ApiLockInterceptor.java # API 并发锁拦截器
│   └── TokenInterceptor.java    # Token 验证拦截器
├── mapper/              # 数据访问层
│   ├── SysAdminMapper.java
│   ├── UserCareerGoalMapper.java
│   └── UserMapper.java
├── service/             # 业务逻辑层
│   ├── impl/
│   │   ├── AgentServiceImpl.java
│   │   ├── SysAdminServiceImpl.java
│   │   ├── TokenServiceImpl.java
│   │   └── UserServiceImpl.java
│   ├── AgentService.java
│   ├── SysAdminService.java
│   ├── TokenService.java
│   └── UserService.java
├── util/                # 工具类
│   ├── DateUtil.java           # 日期工具
│   ├── JwtUtil.java           # JWT 工具
│   ├── MD5Util.java           # MD5 加密工具
│   ├── RandomUtil.java        # 随机数工具
│   ├── RedisLockUtil.java     # Redis 分布式锁
│   ├── RedisUtil.java         # Redis 工具
│   ├── RegexPatterns.java     # 正则表达式常量
│   └── RegexUtil.java         # 正则验证工具
└── MysticNumberGuessApplication.java # 主启动类
```

## API 接口

### 用户接口 (`/api/user`)
| 接口 | 方法 | 功能 |
|------|------|------|
| `/api/user/code` | POST | 发送手机验证码 |
| `/api/user/phoneLogin` | POST | 手机号+验证码登录 |
| `/api/user/accountLogin` | POST | 手机号+密码登录 |
| `/api/user/sendRegisterCode` | POST | 用户注册 |
| `/api/user/logout` | POST | 用户登出 |

### AI 代理接口 (`/api/agent`)
| 接口 | 方法 | 功能 |
|------|------|------|
| `/api/agent/searchJobs` | POST | 根据专业搜索职业 |
| `/api/agent/savejob` | POST | 保存用户选择的职业 |
| `/api/agent/searchSkills` | POST | 根据职业查询技能 |
| `/api/agent/submitMessage` | POST | AI 技能分析与问答 |
| `/api/agent/userJobData/{userId}` | GET | 获取用户职业数据 |

### Token 接口 (`/api/token`)
| 接口 | 方法 | 功能 |
|------|------|------|
| `/api/token` | POST | 刷新访问令牌 |

### 系统管理接口 (`/api/admin`)
| 接口 | 方法 | 功能 |
|------|------|------|
| `/api/admin/login` | POST | 管理员登录 |
| `/api/admin/info` | GET | 获取管理员信息 |
| `/api/admin/users` | GET | 获取用户列表 |

## 核心功能

### 用户认证系统
- **双 Token 机制**: Access Token（短期）+ Refresh Token（长期）
- **自动刷新**: Access Token 即将过期时自动刷新
- **并发控制**: 防止并发刷新 Token 请求
- **密码安全**: MD5 + 随机盐值加密

### AI 服务集成
- **职业搜索**: 调用外部 AI 服务（http://localhost:8000）根据专业推荐职业
- **技能分析**: 基于职业提供技能树和智能问答
- **异步处理**: 支持 RestTemplate 同步调用

### 数据缓存与同步
- **Redis 缓存**: 验证码、用户信息缓存
- **实时同步**: 多客户端数据实时同步
- **并发锁**: Redis 分布式锁防止并发问题

### 安全机制
- **Token 拦截器**: 验证 JWT Token 有效性
- **API 锁拦截器**: 防止接口并发调用
- **白名单机制**: 登录、注册等接口免 Token 验证
- **CORS 配置**: 支持前端跨域访问

## 数据库配置

### 数据库连接
- **数据库名**: learning_planner
- **字符集**: UTF-8
- **时区**: Asia/Shanghai

### 主要表结构
- **user**: 用户信息表（user_id, phone, password, nickname, salt, create_time, update_time）
- **user_career_goal**: 用户职业目标表
- **sys_admin**: 系统管理员表

### 初始化脚本
位置: `src/main/resources/db/schema.sql`

## 配置说明

### 多环境配置
- **开发环境**: `application-dev.yml`（端口 8820，开启 Debug 日志）
- **生产环境**: `application-prod.yml`（关闭调试日志，生成日志文件）
- **环境切换**: 修改 `application.yml` 中的 `spring.profiles.active`

### JWT 配置
```yaml
jwt:
  access-secret: # Access Token 密钥
  refresh-secret: # Refresh Token 密钥
```

### Redis 配置
```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      database: 0
```

### MyBatis-Plus 配置
- 驼峰命名映射: `map-underscore-to-camel-case: true`
- Mapper 位置: `classpath*:/mapper/**/*.xml`
- 实体包: `com.ai.entity`

## 启动方式

### Maven 启动
```bash
mvn spring-boot:run
```

### 打包运行
```bash
mvn clean package
java -jar target/Mystic_Number_Guess-0.0.1-SNAPSHOT.jar
```

### Windows 启动
```bash
mvnw.cmd spring-boot:run
```

## 项目特性

### 高性能
- Redis 缓存减少数据库查询
- 分布式锁防止并发问题
- 异步处理提升响应速度

### 安全性
- JWT 双 Token 机制
- 密码 MD5 + 盐值加密
- 接口并发锁防止重复提交
- 白名单机制保护关键接口

### 可扩展性
- 模块化设计，职责清晰
- 统一异常处理和返回格式
- 拦截器链灵活配置
- 多环境配置支持

### 开发友好
- 完善的日志记录
- 自动化测试支持
- 热部署（Spring Boot DevTools）
- 清晰的代码注释

## 依赖版本

```xml
Spring Boot: 3.5.13
Java: 17
MyBatis-Plus: 3.5.10.1
Hutool: 5.8.40
jjwt: 0.11.5
```

## 注意事项

1. **端口配置**: 开发环境默认端口 8820，Context Path 为 `/api`
2. **数据库初始化**: 首次运行需确保 MySQL 中存在 `learning_planner` 数据库
3. **Redis 依赖**: 确保本地或远程 Redis 服务正常运行
4. **外部 AI 服务**: 确保外部 AI 服务（localhost:8000）可用
5. **Token 刷新**: 建议在前端实现 Token 自动刷新机制
6. **并发控制**: 关键接口已配置并发锁，注意锁的释放时间