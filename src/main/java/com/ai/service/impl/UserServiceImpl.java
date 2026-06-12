package com.ai.service.impl;

import com.ai.common.ResponseCode;
import com.ai.common.Result;
import com.ai.dto.*;
import com.ai.entity.User;
import com.ai.entity.UserAnswers;
import com.ai.entity.UserLearningProgress;
import com.ai.mapper.QuestionsMapper;
import com.ai.mapper.UserAnswersMapper;
import com.ai.mapper.UserLearningProgressMapper;
import com.ai.mapper.UserMapper;
import com.ai.service.UserService;
import com.ai.util.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.ai.common.JwtConstant.REFRESH_TOKEN_MAX_AGE;
import static com.ai.common.ResponseCode.*;
import static com.ai.util.RedisUtil.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    private final StringRedisTemplate stringRedisTemplate;
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final UserLearningProgressMapper userLearningProgressMapper;
    private final QuestionsMapper questionsMapper;
    private final UserAnswersMapper userAnswersMapper;

    /**
     * 生成刷新令牌并设置Cookie，清理相关缓存
     */
    private void generateRefreshTokenAndCleanup(String phone, Long userId, HttpServletResponse response) {
        String refreshToken = jwtUtil.generateRefreshToken(userId);

        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setDomain(null);
        cookie.setMaxAge(REFRESH_TOKEN_MAX_AGE);
        response.addCookie(cookie);

        // 清理相关缓存
        stringRedisTemplate.delete(LOGIN_CODE_KEY + phone);
        stringRedisTemplate.delete(REGISTER_LOCK_KEY + phone);
        stringRedisTemplate.delete(USER_EMPTY_PREFIX + phone);
        stringRedisTemplate.delete(USER_COOL_KEY + phone);
    }

    /**
     * 构建用户DTO并生成访问令牌
     */
    private UserDTO buildUserDTO(User user) {
        String accessToken = jwtUtil.generateAccessToken(user.getUserId());

        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getUserId());
        userDTO.setNickname(user.getNickname());
        userDTO.setAccessToken(accessToken);
        return userDTO;
    }

    /**
     * 保存用户并返回登录结果
     */
    private Result<UserDTO> saveUserAndReturn(String phone, User user, HttpServletResponse response) {
        if (!save(user)) {
            return Result.fail(FAIL);
        }
        UserDTO userDTO = buildUserDTO(user);
        generateRefreshTokenAndCleanup(phone, user.getUserId(), response);
        setHash(phone, user);
        return Result.success(userDTO);
    }

    /**
     * 将id和昵称写入内存
     */
    private void setHash(String phone, User user) {
        String id = String.valueOf(user.getUserId());
        String nickname = user.getNickname();
        stringRedisTemplate.opsForHash().put(USER_EXIST_KEY + phone, "ID", id);
        stringRedisTemplate.opsForHash().put(USER_EXIST_KEY + phone, "NickName", nickname);
        stringRedisTemplate.expire(USER_EXIST_KEY + phone, USER_EXIST_KEY_TTL, TimeUnit.SECONDS);
    }

    /**
     * 根据手机号查询用户（带缓存）
     */
    private User findUserByPhone(String phone) {
        return userMapper.selectOne(Wrappers.lambdaQuery(User.class).eq(User::getPhone, phone));
    }

    /**
     * 创建新用户
     */
    private User createNewUser(String phone) {
        User user = new User();
        user.setPhone(phone);
        user.setPassword(null);
        user.setNickname("用户_" + RandomUtil.generateCode(12));
        LocalDate now = LocalDate.now();
        user.setCreateTime(now.atStartOfDay());
        user.setUpdateTime(now.atStartOfDay());
        user.setSalt(null);
        return user;
    }

    /**
     * 验证验证码
     */
    private boolean validateCode(String phone, String code) {
        String cachedCode = stringRedisTemplate.opsForValue().get(LOGIN_CODE_KEY + phone);
        return !Objects.equals(cachedCode, code);
    }

    @NonNull
    private static ReportPageDataResponseDTO getReportPageDataResponseDTO(List<UserLearningProgress> entities, List<ScoreKeyDTO> keys) {
        Map<String, Integer> scoreMap = new HashMap<>();
        for (UserLearningProgress e : entities) {
            scoreMap.put(e.getSkillName() + "::" + e.getKnowledgeName(), e.getScore());
        }

        // 按 keys 原始顺序组装响应
        List<ScoreItemDTO> scores = new ArrayList<>();
        for (ScoreKeyDTO key : keys) {
            ScoreItemDTO si = new ScoreItemDTO();
            si.setOrder(key.getOrder());
            si.setScore(scoreMap.getOrDefault(key.getSkillName() + "::" + key.getKnowledgeName(), 0));
            scores.add(si);
        }

        ReportPageDataResponseDTO resp = new ReportPageDataResponseDTO();
        resp.setScores(scores);
        return resp;
    }

    //  发送验证码
    @Override
    public Result<String> sendCode(String phone) {
        if (RegexUtil.isPhoneInvalid(phone)) {
            return Result.fail(PHONE_INVALID);
        }

        if (stringRedisTemplate.hasKey(LOGIN_COOL_KEY + phone)) {
            return Result.fail(TOO_MANY_REQUESTS);
        }

        String code = RandomUtil.generateCode(6);
        stringRedisTemplate.opsForValue().set(LOGIN_CODE_KEY + phone, code, LOGIN_CODE_TTL, TimeUnit.MINUTES);
        stringRedisTemplate.opsForValue().set(LOGIN_COOL_KEY + phone, "1", LOGIN_COOL_TTL, TimeUnit.SECONDS);

        return Result.success(code);
    }

    //  手机号验证码登录
    @Override
    public Result<UserDTO> phoneLogin(String phone, String code, HttpServletResponse response) {
        if (RegexUtil.isPhoneInvalid(phone)) {
            return Result.fail(PHONE_INVALID);
        }

        if (RegexUtil.isCodeInvalid(code) || validateCode(phone, code)) {
            return Result.fail(CODE_MISMATCH);
        }
        // 检查注册锁
        if (stringRedisTemplate.hasKey(REGISTER_LOCK_KEY + phone)) {
            return Result.fail(ACCOUNT_EXISTS);
        }

        User user;
        // 检查缓存判断用户是否存在
        if (!stringRedisTemplate.hasKey(USER_EXIST_KEY + phone)) {
            user = findUserByPhone(phone);
            if (user == null) {
                // 新用户注册
                stringRedisTemplate.opsForValue().set(REGISTER_LOCK_KEY + phone, "1", REGISTER_LOCK_KEY_TTL, TimeUnit.SECONDS);
                return saveUserAndReturn(phone, createNewUser(phone), response);
            }else {
                // 缓存用户存在标记
                setHash(phone, user);
                UserDTO userDTO = buildUserDTO(user);
                generateRefreshTokenAndCleanup(phone, user.getUserId(), response);
                return Result.success(userDTO);
            }
        }
        if (validateCode(phone, code)) {
            return Result.fail(CODE_MISMATCH);
        }
        String id = (String) stringRedisTemplate.opsForHash().get(USER_EXIST_KEY + phone, "ID");
        String nickname = (String) stringRedisTemplate.opsForHash().get(USER_EXIST_KEY + phone, "NickName");
        assert id != null;
        String accessToken = jwtUtil.generateAccessToken(Long.valueOf(id));
        UserDTO userDTO = new UserDTO();
        userDTO.setId(Long.valueOf(id));
        userDTO.setNickname(nickname);
        userDTO.setAccessToken(accessToken);
        // 清理相关缓存
        stringRedisTemplate.delete(LOGIN_CODE_KEY + phone);
        stringRedisTemplate.delete(REGISTER_LOCK_KEY + phone);
        stringRedisTemplate.delete(USER_EMPTY_PREFIX + phone);
        stringRedisTemplate.delete(USER_COOL_KEY + phone);
        return Result.success(userDTO);
    }

    //  手机号密码登录
    @Override
    public Result<UserDTO> accountLogin(String phone, String password, HttpServletResponse response) {
        if (RegexUtil.isPhoneInvalid(phone)) {
            return Result.fail(PHONE_INVALID);
        }

        if (RegexUtil.isPassWordInvalid(password)) {
            return Result.fail(PASSWORD_INVALID);
        }

        // 检查空值缓存
        if (stringRedisTemplate.hasKey(USER_EMPTY_PREFIX + phone)) {
            return Result.fail(ACCOUNT_NOT_EXISTS);
        }

        User user = findUserByPhone(phone);
        if (user == null) {
            stringRedisTemplate.opsForValue().set(USER_EMPTY_PREFIX + phone, "1", USER_EMPTY_PREFIX_TTL, TimeUnit.SECONDS);
            return Result.fail(ACCOUNT_NOT_EXISTS);
        }

        String dbEncryptedPwd = user.getPassword();
        if (dbEncryptedPwd == null) {
            return Result.fail(PASSWORD_NOT_SET);
        }

        String newEncryptedPwd = MD5Util.md5WithSalt(password, user.getSalt());
        if (!dbEncryptedPwd.equals(newEncryptedPwd)) {
            return Result.fail(PASSWORD_NOT);
        }
        setHash(phone,user);
        UserDTO userDTO = buildUserDTO(user);
        generateRefreshTokenAndCleanup(phone, user.getUserId(), response);
        return Result.success(userDTO);
    }

    //  注册账号
    @Override
    public Result<UserDTO> sendUser(String phone, String password, String confirmPwd, String code, HttpServletResponse response) {
        if (RegexUtil.isPhoneInvalid(phone)) {
            return Result.fail(PHONE_INVALID);
        }

        if (RegexUtil.isPassWordInvalid(password)) {
            return Result.fail(PASSWORD_INVALID);
        }

        if (RegexUtil.isPassWordInvalid(confirmPwd)) {
            return Result.fail(REPASSWORD_INVALID);
        }

        if (!Objects.equals(password, confirmPwd)) {
            return Result.fail(PASSWORD_MISMATCH);
        }

        if (RegexUtil.isCodeInvalid(code) || validateCode(phone, code)) {
            return Result.fail(CODE_MISMATCH);
        }

        // 检查用户是否已存在
        if (stringRedisTemplate.hasKey(REGISTER_LOCK_KEY + phone)
                || stringRedisTemplate.hasKey(USER_COOL_KEY + phone)
                || stringRedisTemplate.hasKey(USER_EXIST_KEY + phone)) {
            return Result.fail(ACCOUNT_EXISTS);
        }

        if (userMapper.selectCount(new QueryWrapper<User>().eq("phone", phone)) > 0) {
            stringRedisTemplate.opsForValue().set(USER_COOL_KEY + phone, "1", USER_COOL_KEY_TTL, TimeUnit.MINUTES);
            return Result.fail(ACCOUNT_EXISTS);
        }

        // 创建新用户
        stringRedisTemplate.opsForValue().set(REGISTER_LOCK_KEY + phone, "1", REGISTER_LOCK_KEY_TTL, TimeUnit.SECONDS);

        String salt = RandomUtil.generateRandomString();
        String encryptedPassword = MD5Util.md5WithSalt(password, salt);

        User user = new User();
        user.setPhone(phone);
        user.setPassword(encryptedPassword);
        user.setNickname("用户_" + RandomUtil.generateCode(12));
        LocalDate now = LocalDate.now();
        user.setCreateTime(now.atStartOfDay());
        user.setUpdateTime(now.atStartOfDay());
        user.setSalt(salt);

        return saveUserAndReturn(phone, user, response);
    }

    //  退出登录
    @Override
    public Result<?> logout(HttpServletResponse response, String refreshToken) {

        // 将长Token加入黑名单并清除Cookie
        if (refreshToken != null) {
            // 提取JTI存入Redis黑名单
            Claims refreshClaims = jwtUtil.parseRefreshToken(refreshToken);
            String refreshJti = refreshClaims.getId();
            long refreshExpTime = refreshClaims.getExpiration().getTime();
            long refreshTTL = refreshExpTime - System.currentTimeMillis();
            // 长token退出之后加入黑名单
            stringRedisTemplate.opsForValue().set(ACCESS_TOKEN_KEY + refreshJti, "1", refreshTTL, TimeUnit.MILLISECONDS);

            // 清除浏览器中的Cookie
            Cookie cookie = new Cookie("refreshToken", null);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(0);  // 立即过期 = 清除
            response.addCookie(cookie);
        }

        return Result.success();
    }

    @Override
    public Result<ReportPageDataResponseDTO> reportData(ReportPageDataRequestDTO dto) {
        Integer userId = dto.getUserid();

        // 收集所有查询 key（带 order）
        List<ScoreKeyDTO> keys = new ArrayList<>();
        for (SkillReportDTO skill : dto.getSkills()) {
            for (KnowledgeItemDTO item : skill.getItems()) {
                ScoreKeyDTO key = new ScoreKeyDTO();
                key.setUserid(userId);
                key.setSkillName(skill.getSkill_name());
                key.setKnowledgeName(item.getKnowledge_name());
                key.setOrder(item.getOrder());
                keys.add(key);
            }
        }

        List<UserLearningProgress> entities = userLearningProgressMapper.selectBatchByKeys(keys);

        // 构建 score 映射
        ReportPageDataResponseDTO resp = getReportPageDataResponseDTO(entities, keys);
        return Result.success(resp);
    }

    //  获取用户保持的技能数据
    @Override
    public Result<List<UserLearningProgress>> userSelectedSkills(Long userId) {
        List<UserLearningProgress> entities = userLearningProgressMapper.selectList(
                new LambdaQueryWrapper<UserLearningProgress>()
                        .eq(UserLearningProgress::getUserId, userId)
        );
        return Result.success(entities);
    }

    //  获取用户已有的技能数据
    @Override
    public Result<FetchSkillKnowDTO> userSkills(Long userId) {
        
        return null;
    }

    //  获取用户已经生成题目的数据
    @Override
    public Result<List<QuestionWithAnswerStatusDTO>> getQuestions(Long userId) {
        List<QuestionWithAnswerStatusDTO> question = questionsMapper.selectAllQuestionsWithUserAnswerStatus(userId);
        return Result.success(question);
    }

    //  删除用户的选择的技能
    @Override
    public Result<ResponseCode> deleteSkill(DeleteSkillDTO dto) {
        try{
            userLearningProgressMapper.deleteByIdAndName(Math.toIntExact(dto.getUser_id()),dto.getSkill_name(),dto.getJob_name());
            return Result.success(SUCCESS);
        }catch (Exception e){
            System.out.println("数据删除异常" + e);
            return Result.success(FAIL);
        }
    }

    //  更新用户评分和题目的状态
    @Override
    public Result<ResponseCode> submitQuestionAnswer(SubmitQuestionAnswerDTO dto) {
        System.out.println(dto);
        try{
            //原本的分数
            QueryWrapper<UserLearningProgress> wrapper = new QueryWrapper<>();
            wrapper.select("score")
                    .eq("user_id", dto.getUser_id())
                    .eq("job_name", dto.getJob_name())
                    .eq("skill_name", dto.getSkill_name())
                    .eq("knowledge_name", dto.getKnowledge_name());
            UserLearningProgress user = userLearningProgressMapper.selectOne(wrapper);
            int score = user.getScore();

            //原本的题目状态
            QueryWrapper<UserAnswers> wrapper2 = new QueryWrapper<>();
            wrapper2.select("is_correct")
                    .eq("user_id", dto.getUser_id())
                    .eq("question_id", dto.getQuestion_id());
            UserAnswers questions = userAnswersMapper.selectOne(wrapper2);
            int correct = questions.getIsCorrect();

            //获取的分数
            int getscore = 0;
            if (correct == 0){
                userAnswersMapper.updateUserCorrect(dto.getUser_id(),dto.getQuestion_id(),dto.getIs_correct());
                if (dto.getIs_correct() == 1){
                    if (dto.getQuestion_type().equals("judge")){
                        getscore = 1;
                    } else if (dto.getQuestion_type().equals("choice")) {
                        getscore = 2;
                    }
                }else {
                    return Result.success(SUCCESS);
                }
            } else if (correct == 1) {
                return Result.success(SUCCESS);
            }else {
                return Result.success(FAIL);
            }

            //更新本知识点的评分
            userLearningProgressMapper.updateUserScore(dto.getUser_id(),dto.getJob_name(),dto.getSkill_name(),dto.getKnowledge_name(),score + getscore);

            return Result.success(SUCCESS);
        }catch (Exception e){
            System.out.println("用户评分/题目状态错误" + e);
            return Result.success(FAIL);
        }
    }

}
