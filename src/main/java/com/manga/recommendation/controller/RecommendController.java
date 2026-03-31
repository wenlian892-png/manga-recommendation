package com.manga.recommendation.controller;

import com.manga.recommendation.common.Result;
import com.manga.recommendation.service.RecommendService;
import com.manga.recommendation.vo.RecommendHubVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recommend")
public class RecommendController {

    @Autowired
    private RecommendService recommendService;

    @GetMapping("/hub")
    public Result<RecommendHubVO> getRecommendHub(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        RecommendHubVO hubVO = recommendService.getRecommendHub(userId);
        return Result.success(hubVO);
    }
}
