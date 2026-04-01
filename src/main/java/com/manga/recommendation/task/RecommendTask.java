package com.manga.recommendation.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.manga.recommendation.entity.AlgorithmRunLog;
import com.manga.recommendation.entity.MangaInfo;
import com.manga.recommendation.entity.MangaScore;
import com.manga.recommendation.entity.RecommendResult;
import com.manga.recommendation.mapper.AlgorithmRunLogMapper;
import com.manga.recommendation.mapper.MangaInfoMapper;
import com.manga.recommendation.mapper.MangaScoreMapper;
import com.manga.recommendation.mapper.RecommendResultMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class RecommendTask {

    private static final int TOP_N = 10;
    private static final float MAX_SCORE = 5.0f;
    private static final float MIN_SCORE = 1.0f;

    private volatile boolean running = false;

    @Autowired
    private MangaScoreMapper mangaScoreMapper;

    @Autowired
    private RecommendResultMapper recommendResultMapper;

    @Autowired
    private MangaInfoMapper mangaInfoMapper;

    @Autowired
    private AlgorithmRunLogMapper algorithmRunLogMapper;

    @Scheduled(cron = "0 0 2 * * ?")
    public void scheduledExecute() {
        log.info("========== 定时推荐任务触发 ==========");
        try {
            executeItemCFAlgorithm();
            log.info("========== 定时推荐任务完成 ==========");
        } catch (Exception e) {
            log.error("定时推荐任务执行失败", e);
        }
    }

    public void executeItemCFAlgorithm() {
        if (running) {
            log.warn("推荐任务正在执行中，跳过本次执行");
            return;
        }
        running = true;
        long startTime = System.currentTimeMillis();
        try {
            log.info("========== Item-CF 推荐任务开始 ==========");
            log.info("开始构建漫画相似度矩阵...");

            // 过滤已删除漫画的评分数据
            QueryWrapper<MangaInfo> deletedQuery = new QueryWrapper<>();
            deletedQuery.eq("is_deleted", 1);
            List<MangaInfo> deletedManga = mangaInfoMapper.selectList(deletedQuery);
            Set<Long> deletedMangaIds = new HashSet<>();
            for (MangaInfo m : deletedManga) {
                deletedMangaIds.add(m.getId());
            }

            List<MangaScore> allScores = mangaScoreMapper.selectList(null);
            allScores = allScores.stream()
                    .filter(s -> !deletedMangaIds.contains(s.getMangaId()))
                    .collect(Collectors.toList());

            if (allScores == null || allScores.isEmpty()) {
                log.info("无评分数据，推荐任务终止");
                return;
            }

            int scoreCount = allScores.size();

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

            // 计算每个用户的平均评分（用于均值中心化）
            Map<Long, Float> userAvgRatingMap = new HashMap<>();
            for (Map.Entry<Long, List<MangaScore>> entry : userScoresMap.entrySet()) {
                float sum = 0.0f;
                for (MangaScore s : entry.getValue()) {
                    sum += s.getScore();
                }
                userAvgRatingMap.put(entry.getKey(), sum / entry.getValue().size());
            }

            Set<Long> allMangaIds = itemScoresMap.keySet();
            int mangaCount = allMangaIds.size();
            int userCount = userScoresMap.size();
            log.info("共检测到 {} 部漫画，{} 条评分记录（已过滤已删除漫画）", mangaCount, allScores.size());

            Map<String, Float> similarityMatrix = new HashMap<>();
            List<Long> mangaIdList = new ArrayList<>(allMangaIds);

            for (int i = 0; i < mangaIdList.size(); i++) {
                Long mangaIdI = mangaIdList.get(i);
                List<MangaScore> scoresI = itemScoresMap.get(mangaIdI);

                for (int j = i + 1; j < mangaIdList.size(); j++) {
                    Long mangaIdJ = mangaIdList.get(j);
                    List<MangaScore> scoresJ = itemScoresMap.get(mangaIdJ);

                    float similarity = calculateCosineSimilarity(scoresI, scoresJ, userAvgRatingMap);

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

                float userAvg = userAvgRatingMap.getOrDefault(userId, 3.0f);
                Map<Long, Float> predictScoreMap = new HashMap<>();

                for (Long targetMangaId : unratedMangaIds) {
                    float predictScore = predictUserScore(targetMangaId, userRatingMap, similarityMatrix, itemScoresMap, userAvg);
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

            int recommendCount = recommendResults.size();
            long endTime = System.currentTimeMillis();
            log.info("推荐数据持久化完成，总耗时 {} ms", (endTime - startTime));
            log.info("========== Item-CF 推荐任务结束 ==========");

            // 记录成功日志
            AlgorithmRunLog runLog = new AlgorithmRunLog();
            runLog.setAlgorithmType("Item-CF");
            runLog.setStatus(1);
            runLog.setUserCount(userCount);
            runLog.setMangaCount(mangaCount);
            runLog.setScoreCount(scoreCount);
            runLog.setRecommendCount(recommendCount);
            runLog.setDurationMs(System.currentTimeMillis() - startTime);
            runLog.setCreateTime(java.time.LocalDateTime.now());
            algorithmRunLogMapper.insert(runLog);
        } catch (Exception e) {
            log.error("Item-CF 推荐任务执行失败", e);
            // 记录失败日志
            try {
                AlgorithmRunLog runLog = new AlgorithmRunLog();
                runLog.setAlgorithmType("Item-CF");
                runLog.setStatus(0);
                runLog.setDurationMs(System.currentTimeMillis() - startTime);
                runLog.setErrorMessage(e.getMessage());
                runLog.setCreateTime(java.time.LocalDateTime.now());
                algorithmRunLogMapper.insert(runLog);
            } catch (Exception logEx) {
                log.error("记录算法运行日志失败", logEx);
            }
        } finally {
            running = false;
        }
    }

    /**
     * 计算调整后的余弦相似度（Adjusted Cosine Similarity）
     * 减去用户平均评分后再计算，避免高评分用户主导相似度
     */
    private float calculateCosineSimilarity(List<MangaScore> scoresA, List<MangaScore> scoresB,
                                             Map<Long, Float> userAvgRatingMap) {
        Map<Long, Float> centeredMapB = new HashMap<>();
        for (MangaScore score : scoresB) {
            Float avg = userAvgRatingMap.getOrDefault(score.getUserId(), 3.0f);
            centeredMapB.put(score.getUserId(), score.getScore() - avg);
        }

        float dotProduct = 0.0f;
        float normA = 0.0f;
        float normB = 0.0f;

        for (MangaScore scoreA : scoresA) {
            Float avgA = userAvgRatingMap.getOrDefault(scoreA.getUserId(), 3.0f);
            float centeredA = scoreA.getScore() - avgA;
            normA += centeredA * centeredA;

            Float centeredB = centeredMapB.get(scoreA.getUserId());
            if (centeredB != null) {
                dotProduct += centeredA * centeredB;
            }
        }

        for (MangaScore scoreB : scoresB) {
            Float avgB = userAvgRatingMap.getOrDefault(scoreB.getUserId(), 3.0f);
            float centeredB = scoreB.getScore() - avgB;
            normB += centeredB * centeredB;
        }

        float denominator = (float) Math.sqrt(normA) * (float) Math.sqrt(normB);

        if (denominator == 0.0f) {
            return 0.0f;
        }

        return dotProduct / denominator;
    }

    /**
     * 基于相似度预测用户对目标漫画的评分
     * 使用标准公式: predict(u,i) = avg(u) + sum(sim(i,j) * (r(u,j) - avg(u))) / sum(|sim(i,j)|)
     */
    private float predictUserScore(Long targetMangaId, Map<Long, Float> userRatingMap,
                                   Map<String, Float> similarityMatrix,
                                   Map<Long, List<MangaScore>> itemScoresMap,
                                   float userAvgRating) {
        float numerator = 0.0f;
        float denominator = 0.0f;

        List<MangaScore> targetMangaScores = itemScoresMap.get(targetMangaId);
        if (targetMangaScores == null) {
            return userAvgRating;
        }

        for (Map.Entry<Long, Float> ratingEntry : userRatingMap.entrySet()) {
            Long ratedMangaId = ratingEntry.getKey();
            float deviation = ratingEntry.getValue() - userAvgRating;

            String key = ratedMangaId + "_" + targetMangaId;
            Float similarity = similarityMatrix.get(key);

            if (similarity != null && similarity != 0.0f) {
                numerator += similarity * deviation;
                denominator += Math.abs(similarity);
            }
        }

        if (denominator == 0.0f) {
            return userAvgRating;
        }

        float predictScore = userAvgRating + numerator / denominator;
        return Math.min(Math.max(predictScore, MIN_SCORE), MAX_SCORE);
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
