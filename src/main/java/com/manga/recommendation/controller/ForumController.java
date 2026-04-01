package com.manga.recommendation.controller;

import com.manga.recommendation.common.Result;
import com.manga.recommendation.service.ForumService;
import com.manga.recommendation.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/forum")
public class ForumController {

    @Autowired
    private ForumService forumService;

    @GetMapping("/list")
    public Result<PageResponse<ForumPostVO>> getPostList(
            @RequestParam(required = false, defaultValue = "1") Integer current,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String order) {
        PageResponse<ForumPostVO> pageResponse = forumService.getPostList(current, size, title, sortBy, order);
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
    public Result<Void> addPost(HttpServletRequest request,
                                 @Valid @RequestBody ForumAddRequest addRequest) {
        Long userId = (Long) request.getAttribute("userId");
        try {
            forumService.addPost(userId, addRequest);
            return Result.success();
        } catch (Exception e) {
            return Result.error(400, e.getMessage());
        }
    }

    @PostMapping("/reply")
    public Result<Void> addReply(HttpServletRequest request,
                                 @Valid @RequestBody ForumReplyRequest replyRequest) {
        Long userId = (Long) request.getAttribute("userId");
        try {
            forumService.addReply(userId, replyRequest);
            return Result.success();
        } catch (Exception e) {
            return Result.error(400, e.getMessage());
        }
    }
}
