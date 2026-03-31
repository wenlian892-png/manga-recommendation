package com.manga.recommendation.controller;

import com.manga.recommendation.common.Result;
import com.manga.recommendation.service.InteractionService;
import com.manga.recommendation.vo.CollectRequest;
import com.manga.recommendation.vo.ScoreRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/interaction")
public class InteractionController {

    @Autowired
    private InteractionService interactionService;

    @PostMapping("/score")
    public Result<Void> score(@RequestHeader("X-User-Id") Long userId, @RequestBody ScoreRequest request) {
        try {
            interactionService.score(userId, request);
            return Result.success();
        } catch (Exception e) {
            return Result.error(400, e.getMessage());
        }
    }

    @PostMapping("/collect")
    public Result<Void> collect(@RequestHeader("X-User-Id") Long userId, @RequestBody CollectRequest request) {
        try {
            interactionService.collect(userId, request);
            return Result.success();
        } catch (Exception e) {
            return Result.error(400, e.getMessage());
        }
    }

    @GetMapping("/score/current")
    public Result<Map<String, Float>> getUserScore(@RequestHeader("X-User-Id") Long userId,
                                                    @RequestParam Long mangaId) {
        Float score = interactionService.getUserScore(userId, mangaId);
        Map<String, Float> result = new HashMap<>();
        result.put("score", score);
        return Result.success(result);
    }

    @GetMapping("/collect/status")
    public Result<Map<String, Boolean>> isCollected(@RequestHeader("X-User-Id") Long userId,
                                                     @RequestParam Long mangaId) {
        Boolean isCollected = interactionService.isCollected(userId, mangaId);
        Map<String, Boolean> result = new HashMap<>();
        result.put("collected", isCollected);
        return Result.success(result);
    }

    @DeleteMapping("/collect")
    public Result<Void> uncollect(@RequestHeader("X-User-Id") Long userId,
                                  @RequestParam Long mangaId) {
        try {
            interactionService.uncollect(userId, mangaId);
            return Result.success();
        } catch (Exception e) {
            return Result.error(400, e.getMessage());
        }
    }
}
