package com.manga.recommendation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.manga.recommendation.entity.UserInfo;
import com.manga.recommendation.mapper.UserInfoMapper;
import com.manga.recommendation.service.UserService;
import com.manga.recommendation.vo.LoginRequest;
import com.manga.recommendation.vo.RegisterRequest;
import com.manga.recommendation.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.manga.recommendation.common.JwtUtils;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public UserVO register(RegisterRequest request) {
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", request.getUsername());
        UserInfo existUser = userInfoMapper.selectOne(queryWrapper);

        if (existUser != null) {
            throw new RuntimeException("用户名已存在");
        }

        UserInfo userInfo = new UserInfo();
        userInfo.setUsername(request.getUsername());
        userInfo.setPassword(passwordEncoder.encode(request.getPassword()));
        userInfo.setRole(0);
        userInfo.setStatus(1);
        userInfo.setCreateTime(java.time.LocalDateTime.now());

        userInfoMapper.insert(userInfo);

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userInfo, userVO);
        userVO.setToken(generateToken(userInfo.getId(), userInfo.getUsername(), userInfo.getRole()));

        return userVO;
    }

    @Override
    public UserVO login(LoginRequest request) {
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", request.getUsername());
        UserInfo userInfo = userInfoMapper.selectOne(queryWrapper);

        if (userInfo == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        if (!passwordEncoder.matches(request.getPassword(), userInfo.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        if (userInfo.getStatus() != null && userInfo.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userInfo, userVO);
        userVO.setToken(generateToken(userInfo.getId(), userInfo.getUsername(), userInfo.getRole()));

        return userVO;
    }

    @Override
    public UserVO getUserInfoById(Long userId) {
        UserInfo userInfo = userInfoMapper.selectById(userId);
        if (userInfo == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userInfo, userVO);
        return userVO;
    }

    private String generateToken(Long userId, String username, Integer role) {
        return JwtUtils.generateToken(userId, username, role);
    }
}
