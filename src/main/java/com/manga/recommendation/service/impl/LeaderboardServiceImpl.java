package com.manga.recommendation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.manga.recommendation.entity.MangaInfo;
import com.manga.recommendation.mapper.MangaInfoMapper;
import com.manga.recommendation.mapper.MangaScoreMapper;
import com.manga.recommendation.service.LeaderboardService;
import com.manga.recommendation.vo.LeaderboardVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class LeaderboardServiceImpl implements LeaderboardService {

    @Autowired
    private MangaInfoMapper mangaInfoMapper;

    @Autowired
    private MangaScoreMapper mangaScoreMapper;

    @Override
    public List<LeaderboardVO> getHotLeaderboard() {
        QueryWrapper<MangaInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_deleted", 0);
        queryWrapper.orderByDesc("click_count");
        queryWrapper.last("LIMIT 10");

        List<MangaInfo> mangaList = mangaInfoMapper.selectList(queryWrapper);

        // 批量获取平均评分
        Set<Long> mangaIds = mangaList.stream().map(MangaInfo::getId).collect(Collectors.toSet());
        Map<Long, Float> avgScoreMap = getAvgScoreMap(mangaIds);

        List<LeaderboardVO> result = new ArrayList<>();
        for (MangaInfo manga : mangaList) {
            LeaderboardVO vo = new LeaderboardVO();
            BeanUtils.copyProperties(manga, vo);
            vo.setClickCount(manga.getClickCount());
            vo.setAvgScore(avgScoreMap.getOrDefault(manga.getId(), 0.0f));
            result.add(vo);
        }

        return result;
    }

    @Override
    public List<LeaderboardVO> getScoreLeaderboard() {
        // 批量获取平均评分
        List<Map<String, Object>> avgScoreList = mangaScoreMapper.selectAvgScoreGroupByManga();
        Map<Long, Float> avgScoreMap = new HashMap<>();
        List<Long> mangaIds = new ArrayList<>();
        for (Map<String, Object> row : avgScoreList) {
            Long mangaId = ((Number) row.get("manga_id")).longValue();
            Float avgScore = ((Number) row.get("avg_score")).floatValue();
            avgScoreMap.put(mangaId, avgScore);
            mangaIds.add(mangaId);
        }

        if (mangaIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 批量查询漫画信息，过滤已删除
        List<MangaInfo> mangaList = mangaInfoMapper.selectBatchIds(mangaIds);
        Map<Long, MangaInfo> mangaMap = new HashMap<>();
        for (MangaInfo manga : mangaList) {
            if (manga.getIsDeleted() == 0) {
                mangaMap.put(manga.getId(), manga);
            }
        }

        List<LeaderboardVO> result = new ArrayList<>();
        for (Map.Entry<Long, Float> entry : avgScoreMap.entrySet()) {
            MangaInfo manga = mangaMap.get(entry.getKey());
            if (manga == null) continue;
            LeaderboardVO vo = new LeaderboardVO();
            BeanUtils.copyProperties(manga, vo);
            vo.setAvgScore(entry.getValue());
            result.add(vo);
        }

        Collections.sort(result, new Comparator<LeaderboardVO>() {
            @Override
            public int compare(LeaderboardVO a, LeaderboardVO b) {
                Float scoreA = a.getAvgScore() != null ? a.getAvgScore() : 0.0f;
                Float scoreB = b.getAvgScore() != null ? b.getAvgScore() : 0.0f;
                if (!scoreA.equals(scoreB)) {
                    return Float.compare(scoreB, scoreA);
                }
                return Long.compare(a.getId(), b.getId());
            }
        });

        if (result.size() > 10) {
            result = result.subList(0, 10);
        }

        return result;
    }

    private Map<Long, Float> getAvgScoreMap(Set<Long> mangaIds) {
        List<Map<String, Object>> avgScoreList = mangaScoreMapper.selectAvgScoreGroupByManga();
        Map<Long, Float> map = new HashMap<>();
        for (Map<String, Object> row : avgScoreList) {
            Long mangaId = ((Number) row.get("manga_id")).longValue();
            if (mangaIds.contains(mangaId)) {
                Float avgScore = ((Number) row.get("avg_score")).floatValue();
                map.put(mangaId, avgScore);
            }
        }
        return map;
    }
}
