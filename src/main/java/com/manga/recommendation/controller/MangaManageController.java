package com.manga.recommendation.controller;

import com.manga.recommendation.common.Result;
import com.manga.recommendation.service.MangaManageService;
import com.manga.recommendation.service.UserManageService;
import com.manga.recommendation.vo.AdminMangaVO;
import com.manga.recommendation.vo.MangaAddRequest;
import com.manga.recommendation.vo.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/admin")
public class MangaManageController {

    @Autowired
    private MangaManageService mangaManageService;

    @Autowired
    private UserManageService userManageService;

    @GetMapping("/manga/page")
    public Result<PageResponse<AdminMangaVO>> getMangaPage(
            @RequestParam(required = false, defaultValue = "1") Integer current,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author) {
        PageResponse<AdminMangaVO> pageResponse = mangaManageService.getMangaPage(current, size, title, author);
        return Result.success(pageResponse);
    }

    @GetMapping("/manga/{id}")
    public Result<AdminMangaVO> getMangaById(@PathVariable Long id) {
        AdminMangaVO manga = mangaManageService.getMangaById(id);
        if (manga == null) {
            return Result.error(404, "漫画不存在");
        }
        return Result.success(manga);
    }

    @PostMapping("/manga/add")
    public Result<Void> addManga(@Valid @RequestBody MangaAddRequest request) {
        try {
            mangaManageService.addManga(request);
            return Result.success();
        } catch (Exception e) {
            return Result.error(400, e.getMessage());
        }
    }

    @PutMapping("/manga/update/{id}")
    public Result<Void> updateManga(@PathVariable Long id, @Valid @RequestBody MangaAddRequest request) {
        try {
            mangaManageService.updateManga(id, request);
            return Result.success();
        } catch (Exception e) {
            return Result.error(400, e.getMessage());
        }
    }

    @DeleteMapping("/manga/delete/{id}")
    public Result<Void> deleteManga(@PathVariable Long id) {
        try {
            mangaManageService.deleteManga(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(400, e.getMessage());
        }
    }

    @GetMapping("/user/page")
    public Result<PageResponse<com.manga.recommendation.vo.AdminUserVO>> getUserPage(
            @RequestParam(required = false, defaultValue = "1") Integer current,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false) String username) {
        PageResponse<com.manga.recommendation.vo.AdminUserVO> pageResponse =
            userManageService.getUserPage(current, size, username);
        return Result.success(pageResponse);
    }

    @PostMapping("/user/ban/{id}")
    public Result<Void> banUser(@PathVariable Long id) {
        try {
            userManageService.banUser(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(400, e.getMessage());
        }
    }

    @PostMapping("/user/unban/{id}")
    public Result<Void> unbanUser(@PathVariable Long id) {
        try {
            userManageService.unbanUser(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(400, e.getMessage());
        }
    }
}
