package com.manga.recommendation.controller;

import com.manga.recommendation.common.Result;
import com.manga.recommendation.service.ForumService;
import com.manga.recommendation.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/forum")
public class ForumController {

    @Autowired
    private ForumService forumService;

    @GetMapping("/list")
    public Result<PageResponse<ForumPostVO>> getPostList(
            @RequestParam(required = false, defaultValue = "1") Integer current,
            @RequestParam(required = false, defaultValue = "10") Integer size) {
        PageResponse<ForumPostVO> pageResponse = forumService.getPostList(current, size);
        return Result.success(pageResponse);
    }

    @GetMapping("/detail/{id}")
    public Result<ForumDetailVO> getPostDetail(@PathVariable Long id) {
        ForumDetailVO detailVO = forumService.getPostDetail(id);
        if (detailVO == null) {
            return Result.error(404, "帖子不存在");
        }
        return Result.success(detailVO);
    }

    @PostMapping("/add")
    public Result<Void> addPost(@RequestHeader("X-User-Id") Long userId,
                                 @RequestBody ForumAddRequest request) {
        try {
            forumService.addPost(userId, request);
            return Result.success();
        } catch (Exception e) {
            return Result.error(400, e.getMessage());
        }
    }

    @PostMapping("/reply")
    public Result<Void> addReply(@RequestHeader("X-User-Id") Long userId,
                                 @RequestBody ForumReplyRequest request) {
        try {
            forumService.addReply(userId, request);
            return Result.success();
        } catch (Exception e) {
            return Result.error(400, e.getMessage());
        }
    }
}
