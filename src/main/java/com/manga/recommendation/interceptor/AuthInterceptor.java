package com.manga.recommendation.interceptor;

import com.manga.recommendation.entity.UserInfo;
import com.manga.recommendation.mapper.UserInfoMapper;
import com.manga.recommendation.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendError(response, 401, "未登录或Token缺失");
            return false;
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            sendError(response, 401, "Token已过期或无效");
            return false;
        }

        Long userId = jwtUtil.parseUserId(token);
        UserInfo user = userInfoMapper.selectById(userId);
        if (user == null) {
            sendError(response, 401, "用户不存在");
            return false;
        }

        if (user.getStatus() != null && user.getStatus() == 0) {
            sendError(response, 403, "账号已被封禁");
            return false;
        }

        request.setAttribute("userId", userId);
        request.setAttribute("userRole", user.getRole());
        return true;
    }

    private void sendError(HttpServletResponse response, int code, String message) throws Exception {
        response.setStatus(code);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":" + code + ",\"message\":\"" + message + "\"}");
    }
}
