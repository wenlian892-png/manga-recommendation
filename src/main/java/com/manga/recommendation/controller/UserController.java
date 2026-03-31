package com.manga.recommendation.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.manga.recommendation.common.Result;
import com.manga.recommendation.entity.UserInfo;
import com.manga.recommendation.mapper.UserInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @GetMapping("/profile")
    public Result<Map<String, Object>> getProfile(HttpServletRequest request, @RequestHeader(value = "X-User-Id", required = false) Long headerUserId) {
        Map<String, Object> data = new HashMap<>();
        Long userId = (Long) request.getAttribute("X-User-Id");
        if (userId == null) {
            userId = headerUserId;
        }
        if (userId == null) {
            data.put("loggedIn", false);
            return Result.success(data);
        }

        UserInfo user = userInfoMapper.selectById(userId);
        data.put("loggedIn", true);
        if (user != null) {
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("username", user.getUsername());
            userInfo.put("role", user.getRole());
            userInfo.put("status", user.getStatus());
            userInfo.put("createTime", user.getCreateTime());
            data.put("user", userInfo);
        }
        return Result.success(data);
    }
}