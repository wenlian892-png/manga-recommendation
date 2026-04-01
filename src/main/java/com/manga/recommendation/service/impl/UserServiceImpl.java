package com.manga.recommendation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.manga.recommendation.entity.UserInfo;
import com.manga.recommendation.mapper.UserInfoMapper;
import com.manga.recommendation.service.UserService;
import com.manga.recommendation.util.JwtUtil;
import com.manga.recommendation.vo.LoginRequest;
import com.manga.recommendation.vo.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public Map<String, Object> register(RegisterRequest request) {
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", request.getUsername());
        if (userInfoMapper.selectOne(queryWrapper) != null) {
            Map<String, Object> result = new HashMap<>();
            result.put("code", 400);
            result.put("message", "用户名已存在");
            return result;
        }

        UserInfo user = new UserInfo();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(0);
        user.setStatus(1);
        userInfoMapper.insert(user);

        String token = jwtUtil.generateToken(user.getId(), user.getRole());
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userId", user.getId());

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "注册成功");
        result.put("data", data);
        return result;
    }

    @Override
    public Map<String, Object> login(LoginRequest request) {
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", request.getUsername());
        UserInfo user = userInfoMapper.selectOne(queryWrapper);

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            Map<String, Object> result = new HashMap<>();
            result.put("code", 400);
            result.put("message", "用户名或密码错误");
            return result;
        }

        if (user.getStatus() != null && user.getStatus() == 0) {
            Map<String, Object> result = new HashMap<>();
            result.put("code", 403);
            result.put("message", "账号已被封禁");
            return result;
        }

        String token = jwtUtil.generateToken(user.getId(), user.getRole());
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userId", user.getId());
        data.put("username", user.getUsername());
        data.put("role", user.getRole());

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "登录成功");
        result.put("data", data);
        return result;
    }

    @Override
    public UserInfo getUserInfoById(Long userId) {
        return userInfoMapper.selectById(userId);
    }
}
