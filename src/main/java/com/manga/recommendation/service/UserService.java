package com.manga.recommendation.service;

import com.manga.recommendation.entity.UserInfo;
import com.manga.recommendation.vo.LoginRequest;
import com.manga.recommendation.vo.RegisterRequest;

import java.util.Map;

public interface UserService {

    Map<String, Object> register(RegisterRequest request);

    Map<String, Object> login(LoginRequest request);

    UserInfo getUserInfoById(Long userId);
}
