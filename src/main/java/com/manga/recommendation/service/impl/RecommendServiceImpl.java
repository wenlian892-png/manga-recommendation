package com.manga.recommendation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.manga.recommendation.entity.MangaInfo;
import com.manga.recommendation.entity.MangaScore;
import com.manga.recommendation.entity.RecommendResult;
import com.manga.recommendation.entity.UserInfo;
import com.manga.recommendation.mapper.MangaInfoMapper;
import com.manga.recommendation.mapper.MangaScoreMapper;
import com.manga.recommendation.mapper.RecommendResultMapper;
import com.manga.recommendation.mapper.UserInfoMapper;
import com.manga.recommendation.service.RecommendService;
import com.manga.recommendation.vo.MangaVO;
import com.manga.recommendation.vo.RecommendHubVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RecommendServiceImpl implements RecommendService {

    private static final int ZONE_SIZE = 10;

    @Autowired
    private MangaInfoMapper mangaInfoMapper;

    @Autowired
    private MangaScoreMapper mangaScoreMapper;

    @Autowired
    private RecommendResultMapper recommendResultMapper;

    @Autowired
    private UserInfoMapper userInfoMapper;

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
        queryWrapper.orderByDesc("click_count");
        queryWrapper.last("LIMIT " + ZONE_SIZE);

        List<MangaInfo> mangaList = mangaInfoMapper.selectList(queryWrapper);
        return convertToVOList(mangaList);
    }

    private List<MangaVO> getScoreZone() {
        QueryWrapper<MangaInfo> queryWrapper = new QueryWrapper<>();
        List<MangaInfo> allManga = mangaInfoMapper.selectList(queryWrapper);

        Map<Long, Float> avgScoreMap = new HashMap<>();
        for (MangaInfo manga : allManga) {
            avgScoreMap.put(manga.getId(), getAverageScore(manga.getId()));
        }

        List<MangaInfo> sortedManga = new ArrayList<>(allManga);
        for (int i = 0; i < sortedManga.size() - 1; i++) {
            for (int j = 0; j < sortedManga.size() - i - 1; j++) {
                Long idA = sortedManga.get(j).getId();
                Long idB = sortedManga.get(j + 1).getId();
                Float scoreA = avgScoreMap.get(idA);
                Float scoreB = avgScoreMap.get(idB);
                if (scoreA == null) scoreA = 0.0f;
                if (scoreB == null) scoreB = 0.0f;
                if (scoreA < scoreB || (scoreA.equals(scoreB) && idA > idB)) {
                    MangaInfo temp = sortedManga.get(j);
                    sortedManga.set(j, sortedManga.get(j + 1));
                    sortedManga.set(j + 1, temp);
                }
            }
        }

        int size = Math.min(ZONE_SIZE, sortedManga.size());
        return convertToVOList(sortedManga.subList(0, size));
    }

    private List<MangaVO> getCategoryZone(Long userId) {
        List<MangaScore> userScores = getUserScores(userId);
        if (userScores == null || userScores.isEmpty()) {
            return getDefaultCategoryManga();
        }

        Map<String, Integer> categoryCountMap = new HashMap<>();
        for (MangaScore score : userScores) {
            MangaInfo manga = mangaInfoMapper.selectById(score.getMangaId());
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
        queryWrapper.orderByDesc("click_count");
        queryWrapper.last("LIMIT " + ZONE_SIZE);

        List<MangaInfo> mangaList = mangaInfoMapper.selectList(queryWrapper);
        return convertToVOList(mangaList);
    }

    private List<MangaVO> getCfZone(Long userId) {
        QueryWrapper<RecommendResult> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.orderByDesc("predict_score");
        queryWrapper.last("LIMIT " + ZONE_SIZE);

        List<RecommendResult> results = recommendResultMapper.selectList(queryWrapper);

        List<MangaVO> voList = new ArrayList<>();
        for (RecommendResult result : results) {
            MangaInfo manga = mangaInfoMapper.selectById(result.getMangaId());
            if (manga != null) {
                MangaVO vo = new MangaVO();
                BeanUtils.copyProperties(manga, vo);
                voList.add(vo);
            }
        }

        return voList;
    }

    private List<MangaVO> getDefaultCategoryManga() {
        QueryWrapper<MangaInfo> queryWrapper = new QueryWrapper<>();
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

    private Float getAverageScore(Long mangaId) {
        QueryWrapper<MangaScore> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("manga_id", mangaId);
        List<MangaScore> scoreList = mangaScoreMapper.selectList(queryWrapper);

        if (scoreList == null || scoreList.isEmpty()) {
            return 0.0f;
        }

        float sum = 0.0f;
        for (MangaScore score : scoreList) {
            sum += score.getScore();
        }

        return sum / scoreList.size();
    }

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
