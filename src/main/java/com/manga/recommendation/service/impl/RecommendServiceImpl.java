package com.manga.recommendation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.manga.recommendation.entity.MangaInfo;
import com.manga.recommendation.entity.MangaScore;
import com.manga.recommendation.entity.RecommendResult;
import com.manga.recommendation.mapper.MangaInfoMapper;
import com.manga.recommendation.mapper.MangaScoreMapper;
import com.manga.recommendation.mapper.RecommendResultMapper;
import com.manga.recommendation.service.RecommendService;
import com.manga.recommendation.vo.MangaVO;
import com.manga.recommendation.vo.RecommendHubVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendServiceImpl implements RecommendService {

    private static final int ZONE_SIZE = 10;

    @Autowired
    private MangaInfoMapper mangaInfoMapper;

    @Autowired
    private MangaScoreMapper mangaScoreMapper;

    @Autowired
    private RecommendResultMapper recommendResultMapper;

    @Override
    public RecommendHubVO getRecommendHub(Long userId) {
        RecommendHubVO hubVO = new RecommendHubVO();

        hubVO.setHotZone(getHotZone());
        hubVO.setScoreZone(getScoreZone());

        if (userId != null) {
            hubVO.setCategoryZone(getCategoryZone(userId));
            hubVO.setCfZone(getCfZone(userId));
        } else {
            hubVO.setCategoryZone(new ArrayList<>());
            hubVO.setCfZone(new ArrayList<>());
        }

        return hubVO;
    }

    private List<MangaVO> getHotZone() {
        QueryWrapper<MangaInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_deleted", 0);
        queryWrapper.orderByDesc("click_count");
        queryWrapper.last("LIMIT " + ZONE_SIZE);

        List<MangaInfo> mangaList = mangaInfoMapper.selectList(queryWrapper);
        return convertToVOList(mangaList);
    }

    private List<MangaVO> getScoreZone() {
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
        List<MangaInfo> allManga = mangaInfoMapper.selectBatchIds(mangaIds);
        Map<Long, MangaInfo> mangaMap = new HashMap<>();
        for (MangaInfo manga : allManga) {
            if (manga.getIsDeleted() == 0) {
                mangaMap.put(manga.getId(), manga);
            }
        }

        List<MangaInfo> sortedManga = new ArrayList<>();
        for (Map.Entry<Long, Float> entry : avgScoreMap.entrySet()) {
            MangaInfo manga = mangaMap.get(entry.getKey());
            if (manga != null) {
                sortedManga.add(manga);
            }
        }

        Collections.sort(sortedManga, new Comparator<MangaInfo>() {
            @Override
            public int compare(MangaInfo a, MangaInfo b) {
                Float scoreA = avgScoreMap.get(a.getId());
                Float scoreB = avgScoreMap.get(b.getId());
                if (!scoreA.equals(scoreB)) {
                    return Float.compare(scoreB, scoreA);
                }
                return Long.compare(a.getId(), b.getId());
            }
        });

        int size = Math.min(ZONE_SIZE, sortedManga.size());
        List<MangaVO> voList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            MangaInfo manga = sortedManga.get(i);
            MangaVO vo = new MangaVO();
            BeanUtils.copyProperties(manga, vo);
            vo.setAvgScore(avgScoreMap.get(manga.getId()));
            voList.add(vo);
        }
        return voList;
    }

    private List<MangaVO> getCategoryZone(Long userId) {
        List<MangaScore> userScores = getUserScores(userId);
        if (userScores == null || userScores.isEmpty()) {
            return getDefaultCategoryManga();
        }

        // 批量查询漫画信息，避免N+1
        Set<Long> mangaIds = userScores.stream()
                .map(MangaScore::getMangaId)
                .collect(Collectors.toSet());
        List<MangaInfo> mangaList = mangaInfoMapper.selectBatchIds(mangaIds);
        Map<Long, MangaInfo> mangaMap = new HashMap<>();
        for (MangaInfo manga : mangaList) {
            if (manga.getIsDeleted() == 0) {
                mangaMap.put(manga.getId(), manga);
            }
        }

        Map<String, Integer> categoryCountMap = new HashMap<>();
        for (MangaScore score : userScores) {
            MangaInfo manga = mangaMap.get(score.getMangaId());
            if (manga != null && manga.getCategory() != null) {
                String category = manga.getCategory();
                categoryCountMap.put(category, categoryCountMap.getOrDefault(category, 0) + 1);
            }
        }

        if (categoryCountMap.isEmpty()) {
            return getDefaultCategoryManga();
        }

        String topCategory = null;
        int maxCount = 0;
        for (Map.Entry<String, Integer> entry : categoryCountMap.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                topCategory = entry.getKey();
            }
        }

        QueryWrapper<MangaInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category", topCategory);
        queryWrapper.eq("is_deleted", 0);
        queryWrapper.orderByDesc("click_count");
        queryWrapper.last("LIMIT " + ZONE_SIZE);

        List<MangaInfo> categoryMangaList = mangaInfoMapper.selectList(queryWrapper);
        return convertToVOList(categoryMangaList);
    }

    private List<MangaVO> getCfZone(Long userId) {
        QueryWrapper<RecommendResult> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.orderByDesc("predict_score");
        queryWrapper.last("LIMIT " + ZONE_SIZE);

        List<RecommendResult> results = recommendResultMapper.selectList(queryWrapper);

        if (results.isEmpty()) {
            return new ArrayList<>();
        }

        // 批量查询漫画信息
        Set<Long> mangaIds = results.stream()
                .map(RecommendResult::getMangaId)
                .collect(Collectors.toSet());
        List<MangaInfo> mangaList = mangaInfoMapper.selectBatchIds(mangaIds);
        Map<Long, MangaInfo> mangaMap = new HashMap<>();
        for (MangaInfo manga : mangaList) {
            mangaMap.put(manga.getId(), manga);
        }

        List<MangaVO> voList = new ArrayList<>();
        for (RecommendResult result : results) {
            MangaInfo manga = mangaMap.get(result.getMangaId());
            if (manga != null && manga.getIsDeleted() == 0) {
                MangaVO vo = new MangaVO();
                BeanUtils.copyProperties(manga, vo);
                voList.add(vo);
            }
        }

        return voList;
    }

    private List<MangaVO> getDefaultCategoryManga() {
        QueryWrapper<MangaInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_deleted", 0);
        queryWrapper.orderByDesc("click_count");
        queryWrapper.last("LIMIT " + ZONE_SIZE);

        List<MangaInfo> mangaList = mangaInfoMapper.selectList(queryWrapper);
        return convertToVOList(mangaList);
    }

    private List<MangaScore> getUserScores(Long userId) {
        QueryWrapper<MangaScore> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return mangaScoreMapper.selectList(queryWrapper);
    }

    @Autowired
    private MangaScoreHelper mangaScoreHelper;

    private List<MangaVO> convertToVOList(List<MangaInfo> mangaList) {
        List<MangaVO> voList = new ArrayList<>();
        for (MangaInfo manga : mangaList) {
            MangaVO vo = new MangaVO();
            BeanUtils.copyProperties(manga, vo);
            voList.add(vo);
        }
        return voList;
    }
}
