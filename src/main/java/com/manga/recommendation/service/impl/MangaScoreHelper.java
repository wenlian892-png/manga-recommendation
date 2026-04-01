package com.manga.recommendation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.manga.recommendation.entity.MangaScore;
import com.manga.recommendation.mapper.MangaScoreMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MangaScoreHelper {

    @Autowired
    private MangaScoreMapper mangaScoreMapper;

    public Float getAverageScore(Long mangaId) {
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
}
