package com.manga.recommendation.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.manga.recommendation.entity.MangaScore;
import com.manga.recommendation.entity.RecommendResult;
import com.manga.recommendation.mapper.MangaScoreMapper;
import com.manga.recommendation.mapper.RecommendResultMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Component
@Slf4j
public class RecommendTask {

    private static final int TOP_N = 10;
    private static final float MAX_SCORE = 5.0f;

    @Autowired
    private MangaScoreMapper mangaScoreMapper;

    @Autowired
    private RecommendResultMapper recommendResultMapper;

    @Scheduled(cron = "0 0 2 * * ?")
    public void scheduledExecute() {
        executeItemCFAlgorithm();
    }

    public void executeItemCFAlgorithm() {
        long startTime = System.currentTimeMillis();

        log.info("========== Item-CF 推荐任务开始 ==========");
        log.info("开始构建漫画相似度矩阵...");

        List<MangaScore> allScores = mangaScoreMapper.selectList(null);

        if (allScores == null || allScores.isEmpty()) {
            log.info("无评分数据，推荐任务终止");
            return;
        }

        Map<Long, List<MangaScore>> userScoresMap = new HashMap<>();
        Map<Long, List<MangaScore>> itemScoresMap = new HashMap<>();

        for (MangaScore score : allScores) {
            Long userId = score.getUserId();
            Long mangaId = score.getMangaId();

            List<MangaScore> userList = userScoresMap.get(userId);
            if (userList == null) {
                userList = new ArrayList<>();
                userScoresMap.put(userId, userList);
            }
            userList.add(score);

            List<MangaScore> itemList = itemScoresMap.get(mangaId);
            if (itemList == null) {
                itemList = new ArrayList<>();
                itemScoresMap.put(mangaId, itemList);
            }
            itemList.add(score);
        }

        Set<Long> allMangaIds = itemScoresMap.keySet();
        int mangaCount = allMangaIds.size();
        log.info("共检测到 {} 部漫画，{} 条评分记录", mangaCount, allScores.size());

        Map<String, Float> similarityMatrix = new HashMap<>();
        List<Long> mangaIdList = new ArrayList<>(allMangaIds);

        for (int i = 0; i < mangaIdList.size(); i++) {
            Long mangaIdI = mangaIdList.get(i);
            List<MangaScore> scoresI = itemScoresMap.get(mangaIdI);

            for (int j = i + 1; j < mangaIdList.size(); j++) {
                Long mangaIdJ = mangaIdList.get(j);
                List<MangaScore> scoresJ = itemScoresMap.get(mangaIdJ);

                float similarity = calculateCosineSimilarity(scoresI, scoresJ);

                similarityMatrix.put(mangaIdI + "_" + mangaIdJ, similarity);
                similarityMatrix.put(mangaIdJ + "_" + mangaIdI, similarity);
            }
        }

        log.info("矩阵计算完成，开始计算预测评分...");

        List<RecommendResult> recommendResults = new ArrayList<>();

        for (Map.Entry<Long, List<MangaScore>> entry : userScoresMap.entrySet()) {
            Long userId = entry.getKey();
            List<MangaScore> userScoreList = entry.getValue();

            Map<Long, Float> userRatingMap = new HashMap<>();
            for (MangaScore s : userScoreList) {
                userRatingMap.put(s.getMangaId(), s.getScore());
            }

            Set<Long> ratedMangaIds = userRatingMap.keySet();
            List<Long> unratedMangaIds = new ArrayList<>();
            for (Long mangaId : allMangaIds) {
                if (!ratedMangaIds.contains(mangaId)) {
                    unratedMangaIds.add(mangaId);
                }
            }

            Map<Long, Float> predictScoreMap = new HashMap<>();

            for (Long targetMangaId : unratedMangaIds) {
                float predictScore = predictUserScore(targetMangaId, userRatingMap, similarityMatrix, itemScoresMap);
                predictScoreMap.put(targetMangaId, predictScore);
            }

            List<Map.Entry<Long, Float>> sortedPredictList = new ArrayList<>(predictScoreMap.entrySet());
            Collections.sort(sortedPredictList, new Comparator<Map.Entry<Long, Float>>() {
                @Override
                public int compare(Map.Entry<Long, Float> o1, Map.Entry<Long, Float> o2) {
                    return Float.compare(o2.getValue(), o1.getValue());
                }
            });

            int recommendCount = Math.min(TOP_N, sortedPredictList.size());
            for (int i = 0; i < recommendCount; i++) {
                Map.Entry<Long, Float> predictEntry = sortedPredictList.get(i);
                RecommendResult result = new RecommendResult();
                result.setUserId(userId);
                result.setMangaId(predictEntry.getKey());
                result.setPredictScore(predictEntry.getValue());
                result.setUpdateTime(LocalDateTime.now());
                recommendResults.add(result);
            }
        }

        persistRecommendResults(recommendResults);

        long endTime = System.currentTimeMillis();
        log.info("推荐数据持久化完成，总耗时 {} ms", (endTime - startTime));
        log.info("========== Item-CF 推荐任务结束 ==========");
    }

    private float calculateCosineSimilarity(List<MangaScore> scoresA, List<MangaScore> scoresB) {
        Map<Long, Float> ratingMapB = new HashMap<>();
        for (MangaScore score : scoresB) {
            ratingMapB.put(score.getUserId(), score.getScore());
        }

        float dotProduct = 0.0f;
        float normA = 0.0f;
        float normB = 0.0f;

        for (MangaScore scoreA : scoresA) {
            float ratingA = scoreA.getScore();
            normA += ratingA * ratingA;

            Float ratingB = ratingMapB.get(scoreA.getUserId());
            if (ratingB != null) {
                dotProduct += ratingA * ratingB;
            }
        }

        for (MangaScore scoreB : scoresB) {
            float ratingB = scoreB.getScore();
            normB += ratingB * ratingB;
        }

        float denominator = (float) Math.sqrt(normA) * (float) Math.sqrt(normB);

        if (denominator == 0.0f) {
            return 0.0f;
        }

        return dotProduct / denominator;
    }

    private float predictUserScore(Long targetMangaId, Map<Long, Float> userRatingMap,
                                   Map<String, Float> similarityMatrix,
                                   Map<Long, List<MangaScore>> itemScoresMap) {
        float numerator = 0.0f;
        float denominator = 0.0f;

        List<MangaScore> targetMangaScores = itemScoresMap.get(targetMangaId);
        if (targetMangaScores == null) {
            return 0.0f;
        }

        for (Map.Entry<Long, Float> ratingEntry : userRatingMap.entrySet()) {
            Long ratedMangaId = ratingEntry.getKey();
            float userRating = ratingEntry.getValue();

            String key = ratedMangaId + "_" + targetMangaId;
            Float similarity = similarityMatrix.get(key);

            if (similarity != null && similarity != 0.0f) {
                numerator += similarity * userRating;
                denominator += Math.abs(similarity);
            }
        }

        if (denominator == 0.0f) {
            return 0.0f;
        }

        float predictScore = numerator / denominator;

        return Math.min(predictScore, MAX_SCORE);
    }

    @Transactional(rollbackFor = Exception.class)
    public void persistRecommendResults(List<RecommendResult> results) {
        if (results == null || results.isEmpty()) {
            return;
        }

        Set<Long> userIds = new HashSet<>();
        for (RecommendResult result : results) {
            userIds.add(result.getUserId());
        }

        for (Long userId : userIds) {
            QueryWrapper<RecommendResult> deleteWrapper = new QueryWrapper<>();
            deleteWrapper.eq("user_id", userId);
            recommendResultMapper.delete(deleteWrapper);
        }

        for (RecommendResult result : results) {
            recommendResultMapper.insert(result);
        }
    }
}
