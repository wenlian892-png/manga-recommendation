package com.manga.recommendation.controller;

import com.manga.recommendation.common.Result;
import com.manga.recommendation.service.RecommendService;
import com.manga.recommendation.vo.RecommendHubVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/recommend")
public class RecommendController {

    @Autowired
    private RecommendService recommendService;

    @GetMapping("/hub")
    public Result<RecommendHubVO> getRecommendHub(
            HttpServletRequest request,
            @RequestParam(required = false) Long userId) {
        // 优先从拦截器设置的属性获取（有 Token 时），其次从请求参数获取（无 Token 时前端传递）
        Long finalUserId = (Long) request.getAttribute("userId");
        if (finalUserId == null) {
            finalUserId = userId;
        }
        RecommendHubVO hubVO = recommendService.getRecommendHub(finalUserId);
        return Result.success(hubVO);
    }
}
