package com.manga.recommendation.service;

import com.manga.recommendation.vo.LoginRequest;
import com.manga.recommendation.vo.RegisterRequest;
import com.manga.recommendation.vo.UserVO;

public interface UserService {

    UserVO register(RegisterRequest request);

    UserVO login(LoginRequest request);

    UserVO getUserInfoById(Long userId);
}
