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
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserInfoMapper userInfoMapper;

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
        userInfo.setPassword(hashPassword(request.getPassword()));
        userInfo.setRole(0);
        userInfo.setStatus(0);
        userInfo.setCreateTime(java.time.LocalDateTime.now());

        userInfoMapper.insert(userInfo);

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userInfo, userVO);
        userVO.setToken(generateToken(userInfo.getId()));

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

        if (!userInfo.getPassword().equals(hashPassword(request.getPassword()))) {
            throw new RuntimeException("用户名或密码错误");
        }

        if (userInfo.getStatus() != null && userInfo.getStatus() == 1) {
            throw new RuntimeException("账号已被禁用");
        }

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userInfo, userVO);
        userVO.setToken(generateToken(userInfo.getId()));

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

    private String hashPassword(String password) {
        return DigestUtils.md5DigestAsHex((password + "manga_salt").getBytes(StandardCharsets.UTF_8));
    }

    private String generateToken(Long userId) {
        String raw = userId + "_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString();
        return DigestUtils.md5DigestAsHex(raw.getBytes(StandardCharsets.UTF_8));
    }
}
