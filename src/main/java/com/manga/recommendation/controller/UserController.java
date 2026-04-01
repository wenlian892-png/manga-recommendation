package com.manga.recommendation.controller;

import com.manga.recommendation.common.Result;
import com.manga.recommendation.entity.UserInfo;
import com.manga.recommendation.service.UserService;
import com.manga.recommendation.vo.LoginRequest;
import com.manga.recommendation.vo.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result<Map<String, Object>> register(@Valid @RequestBody RegisterRequest request) {
        Map<String, Object> result = userService.register(request);
        int code = (int) result.get("code");
        if (code == 200) {
            return Result.success((Map<String, Object>) result.get("data"));
        } else {
            return Result.error(code, (String) result.get("message"));
        }
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        Map<String, Object> result = userService.login(request);
        int code = (int) result.get("code");
        if (code == 200) {
            return Result.success((Map<String, Object>) result.get("data"));
        } else {
            return Result.error(code, (String) result.get("message"));
        }
    }

    @GetMapping("/profile")
    public Result<Map<String, Object>> getProfile(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Map<String, Object> data = new HashMap<>();
        if (userId == null) {
            data.put("loggedIn", false);
            return Result.success(data);
        }

        UserInfo user = userService.getUserInfoById(userId);
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
