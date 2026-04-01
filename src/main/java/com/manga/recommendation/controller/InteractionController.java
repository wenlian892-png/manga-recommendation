package com.manga.recommendation.controller;

import com.manga.recommendation.common.Result;
import com.manga.recommendation.service.InteractionService;
import com.manga.recommendation.vo.CollectRequest;
import com.manga.recommendation.vo.ScoreRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/interaction")
public class InteractionController {

    @Autowired
    private InteractionService interactionService;

    @PostMapping("/score")
    public Result<Void> score(HttpServletRequest request, @Valid @RequestBody ScoreRequest scoreRequest) {
        Long userId = (Long) request.getAttribute("userId");
        try {
            interactionService.score(userId, scoreRequest);
            return Result.success();
        } catch (Exception e) {
            return Result.error(400, e.getMessage());
        }
    }

    @PostMapping("/collect")
    public Result<Void> collect(HttpServletRequest request, @Valid @RequestBody CollectRequest collectRequest) {
        Long userId = (Long) request.getAttribute("userId");
        try {
            interactionService.collect(userId, collectRequest);
            return Result.success();
        } catch (Exception e) {
            return Result.error(400, e.getMessage());
        }
    }

    @GetMapping("/score/current")
    public Result<Map<String, Float>> getUserScore(HttpServletRequest request,
                                                    @RequestParam Long mangaId) {
        Long userId = (Long) request.getAttribute("userId");
        Float score = interactionService.getUserScore(userId, mangaId);
        Map<String, Float> result = new HashMap<>();
        result.put("score", score);
        return Result.success(result);
    }

    @GetMapping("/collect/status")
    public Result<Map<String, Boolean>> isCollected(HttpServletRequest request,
                                                     @RequestParam Long mangaId) {
        Long userId = (Long) request.getAttribute("userId");
        Boolean isCollected = interactionService.isCollected(userId, mangaId);
        Map<String, Boolean> result = new HashMap<>();
        result.put("collected", isCollected);
        return Result.success(result);
    }

    @DeleteMapping("/collect")
    public Result<Void> uncollect(HttpServletRequest request,
                                  @RequestParam Long mangaId) {
        Long userId = (Long) request.getAttribute("userId");
        try {
            interactionService.uncollect(userId, mangaId);
            return Result.success();
        } catch (Exception e) {
            return Result.error(400, e.getMessage());
        }
    }
}
