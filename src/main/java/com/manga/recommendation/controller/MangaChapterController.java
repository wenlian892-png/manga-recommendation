package com.manga.recommendation.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.manga.recommendation.entity.MangaChapter;
import com.manga.recommendation.entity.ReadHistory;
import com.manga.recommendation.mapper.MangaChapterMapper;
import com.manga.recommendation.mapper.ReadHistoryMapper;
import com.manga.recommendation.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/manga")
public class MangaChapterController {

    @Autowired
    private MangaChapterMapper chapterMapper;

    @Autowired
    private ReadHistoryMapper readHistoryMapper;

    @GetMapping("/{mangaId}/chapters")
    public Result<List<MangaChapter>> getChapters(@PathVariable Long mangaId) {
        QueryWrapper<MangaChapter> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("manga_id", mangaId);
        queryWrapper.eq("is_deleted", 0);
        queryWrapper.orderByAsc("chapter_number");
        List<MangaChapter> chapters = chapterMapper.selectList(queryWrapper);
        return Result.success(chapters);
    }

    @PostMapping("/{mangaId}/chapters/{chapterId}/read")
    public Result<Void> recordRead(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                   @PathVariable Long mangaId,
                                   @PathVariable Long chapterId) {
        if (userId != null) {
            ReadHistory readHistory = new ReadHistory();
            readHistory.setUserId(userId);
            readHistory.setMangaId(mangaId);
            readHistory.setChapterId(chapterId);
            readHistory.setUpdateTime(LocalDateTime.now());
            readHistoryMapper.insert(readHistory);
        }
        return Result.success();
    }

    @GetMapping("/read-stats")
    public Result<Map<String, Object>> getReadStats(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        Map<String, Object> stats = new HashMap<>();
        if (userId != null) {
            LocalDate today = LocalDate.now();
            LocalDateTime startOfDay = today.atStartOfDay();
            LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

            QueryWrapper<ReadHistory> todayQuery = new QueryWrapper<>();
            todayQuery.eq("user_id", userId);
            todayQuery.ge("update_time", startOfDay);
            todayQuery.lt("update_time", endOfDay);
            Long todayCount = readHistoryMapper.selectCount(todayQuery);

            QueryWrapper<ReadHistory> totalQuery = new QueryWrapper<>();
            totalQuery.eq("user_id", userId);
            Long totalCount = readHistoryMapper.selectCount(totalQuery);

            stats.put("todayRead", todayCount != null ? todayCount.intValue() : 0);
            stats.put("totalRead", totalCount != null ? totalCount.intValue() : 0);
        } else {
            stats.put("todayRead", 0);
            stats.put("totalRead", 0);
        }
        return Result.success(stats);
    }
}