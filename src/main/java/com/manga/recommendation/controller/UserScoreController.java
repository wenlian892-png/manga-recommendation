package com.manga.recommendation.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.manga.recommendation.common.Result;
import com.manga.recommendation.entity.MangaInfo;
import com.manga.recommendation.entity.MangaScore;
import com.manga.recommendation.mapper.MangaInfoMapper;
import com.manga.recommendation.mapper.MangaScoreMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserScoreController {

    @Autowired
    private MangaScoreMapper scoreMapper;

    @Autowired
    private MangaInfoMapper mangaMapper;

    private Connection getConnection() throws Exception {
        String dbUrl = "jdbc:mysql://localhost:3306/manga_rec_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai";
        return DriverManager.getConnection(dbUrl, "root", "123456");
    }

    @GetMapping("/scores")
    public Result<List<Map<String, Object>>> getMyScores(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (userId == null) {
            return Result.success(result);
        }

        QueryWrapper<MangaScore> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.orderByDesc("create_time");
        List<MangaScore> scores = scoreMapper.selectList(queryWrapper);

        for (MangaScore score : scores) {
            Map<String, Object> item = new HashMap<>();
            item.put("scoreId", score.getId());
            item.put("mangaId", score.getMangaId());
            item.put("score", score.getScore());
            item.put("createTime", score.getCreateTime());

            MangaInfo manga = mangaMapper.selectById(score.getMangaId());
            if (manga != null) {
                item.put("mangaTitle", manga.getTitle());
                item.put("mangaCover", manga.getCoverUrl());
            }
            result.add(item);
        }
        return Result.success(result);
    }

    @GetMapping("/collections")
    public Result<List<Map<String, Object>>> getMyCollections(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (userId == null) {
            return Result.success(result);
        }

        String sql = "SELECT m.id, m.title, m.cover_url, m.category, uc.create_time " +
                     "FROM user_collection uc " +
                     "JOIN manga_info m ON uc.manga_id = m.id " +
                     "WHERE uc.user_id = " + userId + " AND m.is_deleted = 0 " +
                     "ORDER BY uc.create_time DESC";

        try (Connection conn = getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, Object> item = new HashMap<>();
                item.put("mangaId", rs.getInt("id"));
                item.put("mangaTitle", rs.getString("title"));
                item.put("mangaCover", rs.getString("cover_url"));
                item.put("category", rs.getString("category"));
                item.put("createTime", rs.getTimestamp("create_time"));
                result.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.success(result);
    }

    @GetMapping("/read-history")
    public Result<List<Map<String, Object>>> getReadHistory(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (userId == null) {
            return Result.success(result);
        }

        String sql = "SELECT m.id, m.title, m.cover_url, m.category, rh.update_time, rh.chapter_id, mc.title as chapter_title " +
                     "FROM read_history rh " +
                     "JOIN manga_info m ON rh.manga_id = m.id " +
                     "LEFT JOIN manga_chapter mc ON rh.chapter_id = mc.id " +
                     "WHERE rh.user_id = " + userId + " " +
                     "ORDER BY rh.update_time DESC LIMIT 50";

        try (Connection conn = getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, Object> item = new HashMap<>();
                item.put("mangaId", rs.getInt("id"));
                item.put("mangaTitle", rs.getString("title"));
                item.put("mangaCover", rs.getString("cover_url"));
                item.put("category", rs.getString("category"));
                item.put("chapterId", rs.getObject("chapter_id"));
                item.put("chapterTitle", rs.getString("chapter_title"));
                item.put("readTime", rs.getTimestamp("update_time"));
                result.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.success(result);
    }
}