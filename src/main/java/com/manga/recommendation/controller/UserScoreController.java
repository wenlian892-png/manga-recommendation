package com.manga.recommendation.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.manga.recommendation.common.Result;
import com.manga.recommendation.entity.MangaChapter;
import com.manga.recommendation.entity.MangaInfo;
import com.manga.recommendation.entity.MangaScore;
import com.manga.recommendation.entity.ReadHistory;
import com.manga.recommendation.entity.UserCollection;
import com.manga.recommendation.mapper.MangaChapterMapper;
import com.manga.recommendation.mapper.MangaInfoMapper;
import com.manga.recommendation.mapper.MangaScoreMapper;
import com.manga.recommendation.mapper.ReadHistoryMapper;
import com.manga.recommendation.mapper.UserCollectionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
public class UserScoreController {

    @Autowired
    private MangaScoreMapper scoreMapper;

    @Autowired
    private MangaInfoMapper mangaMapper;

    @Autowired
    private UserCollectionMapper userCollectionMapper;

    @Autowired
    private ReadHistoryMapper readHistoryMapper;

    @Autowired
    private MangaChapterMapper mangaChapterMapper;

    @GetMapping("/scores")
    public Result<List<Map<String, Object>>> getMyScores(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<Map<String, Object>> result = new ArrayList<>();
        if (userId == null) {
            return Result.success(result);
        }

        QueryWrapper<MangaScore> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.orderByDesc("create_time");
        List<MangaScore> scores = scoreMapper.selectList(queryWrapper);

        List<Long> mangaIds = scores.stream().map(MangaScore::getMangaId).distinct().collect(Collectors.toList());
        Map<Long, MangaInfo> mangaMap = new HashMap<>();
        if (!mangaIds.isEmpty()) {
            mangaMapper.selectBatchIds(mangaIds).forEach(m -> mangaMap.put(m.getId(), m));
        }

        for (MangaScore score : scores) {
            Map<String, Object> item = new HashMap<>();
            item.put("scoreId", score.getId());
            item.put("mangaId", score.getMangaId());
            item.put("score", score.getScore());
            item.put("createTime", score.getCreateTime());

            MangaInfo manga = mangaMap.get(score.getMangaId());
            if (manga != null) {
                item.put("mangaTitle", manga.getTitle());
                item.put("mangaCover", manga.getCoverUrl());
            }
            result.add(item);
        }
        return Result.success(result);
    }

    @GetMapping("/collections")
    public Result<List<Map<String, Object>>> getMyCollections(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<Map<String, Object>> result = new ArrayList<>();
        if (userId == null) {
            return Result.success(result);
        }

        QueryWrapper<UserCollection> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.orderByDesc("create_time");
        List<UserCollection> collections = userCollectionMapper.selectList(queryWrapper);

        List<Long> mangaIds = collections.stream().map(UserCollection::getMangaId).distinct().collect(Collectors.toList());
        Map<Long, MangaInfo> mangaMap = new HashMap<>();
        if (!mangaIds.isEmpty()) {
            mangaMapper.selectBatchIds(mangaIds).forEach(m -> mangaMap.put(m.getId(), m));
        }

        for (UserCollection uc : collections) {
            MangaInfo manga = mangaMap.get(uc.getMangaId());
            if (manga != null && manga.getIsDeleted() == 0) {
                Map<String, Object> item = new HashMap<>();
                item.put("mangaId", manga.getId());
                item.put("mangaTitle", manga.getTitle());
                item.put("mangaCover", manga.getCoverUrl());
                item.put("category", manga.getCategory());
                item.put("createTime", uc.getCreateTime());
                result.add(item);
            }
        }
        return Result.success(result);
    }

    @GetMapping("/read-history")
    public Result<List<Map<String, Object>>> getReadHistory(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<Map<String, Object>> result = new ArrayList<>();
        if (userId == null) {
            return Result.success(result);
        }

        QueryWrapper<ReadHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.orderByDesc("update_time");
        queryWrapper.last("LIMIT 50");
        List<ReadHistory> historyList = readHistoryMapper.selectList(queryWrapper);

        List<Long> mangaIds = historyList.stream().map(ReadHistory::getMangaId).distinct().collect(Collectors.toList());
        Map<Long, MangaInfo> mangaMap = new HashMap<>();
        if (!mangaIds.isEmpty()) {
            mangaMapper.selectBatchIds(mangaIds).forEach(m -> mangaMap.put(m.getId(), m));
        }

        List<Long> chapterIds = historyList.stream().map(ReadHistory::getChapterId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        Map<Long, MangaChapter> chapterMap = new HashMap<>();
        if (!chapterIds.isEmpty()) {
            mangaChapterMapper.selectBatchIds(chapterIds).forEach(c -> chapterMap.put(c.getId(), c));
        }

        for (ReadHistory rh : historyList) {
            MangaInfo manga = mangaMap.get(rh.getMangaId());
            if (manga != null && manga.getIsDeleted() == 0) {
                Map<String, Object> item = new HashMap<>();
                item.put("mangaId", manga.getId());
                item.put("mangaTitle", manga.getTitle());
                item.put("mangaCover", manga.getCoverUrl());
                item.put("category", manga.getCategory());
                item.put("chapterId", rh.getChapterId());
                item.put("readTime", rh.getUpdateTime());

                MangaChapter chapter = chapterMap.get(rh.getChapterId());
                if (chapter != null) {
                    item.put("chapterTitle", chapter.getTitle());
                }
                result.add(item);
            }
        }
        return Result.success(result);
    }
}
