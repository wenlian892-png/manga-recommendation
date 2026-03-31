package com.manga.recommendation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.manga.recommendation.entity.MangaInfo;
import com.manga.recommendation.entity.MangaScore;
import com.manga.recommendation.mapper.MangaInfoMapper;
import com.manga.recommendation.mapper.MangaScoreMapper;
import com.manga.recommendation.service.LeaderboardService;
import com.manga.recommendation.vo.LeaderboardVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LeaderboardServiceImpl implements LeaderboardService {

    @Autowired
    private MangaInfoMapper mangaInfoMapper;

    @Autowired
    private MangaScoreMapper mangaScoreMapper;

    @Override
    public List<LeaderboardVO> getHotLeaderboard() {
        QueryWrapper<MangaInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("click_count");
        queryWrapper.last("LIMIT 10");

        List<MangaInfo> mangaList = mangaInfoMapper.selectList(queryWrapper);

        List<LeaderboardVO> result = new ArrayList<>();
        for (MangaInfo manga : mangaList) {
            LeaderboardVO vo = new LeaderboardVO();
            BeanUtils.copyProperties(manga, vo);
            vo.setClickCount(manga.getClickCount());
            Float avgScore = getAverageScore(manga.getId());
            vo.setAvgScore(avgScore);
            result.add(vo);
        }

        return result;
    }

    @Override
    public List<LeaderboardVO> getScoreLeaderboard() {
        QueryWrapper<MangaInfo> queryWrapper = new QueryWrapper<>();
        List<MangaInfo> mangaList = mangaInfoMapper.selectList(queryWrapper);

        List<LeaderboardVO> result = new ArrayList<>();
        for (MangaInfo manga : mangaList) {
            LeaderboardVO vo = new LeaderboardVO();
            BeanUtils.copyProperties(manga, vo);
            Float avgScore = getAverageScore(manga.getId());
            vo.setAvgScore(avgScore);
            result.add(vo);
        }

        for (int i = 0; i < result.size() - 1; i++) {
            for (int j = 0; j < result.size() - i - 1; j++) {
                Float scoreJ = result.get(j).getAvgScore();
                Float scoreJ1 = result.get(j + 1).getAvgScore();
                if (scoreJ == null) scoreJ = 0.0f;
                if (scoreJ1 == null) scoreJ1 = 0.0f;
                if (scoreJ < scoreJ1 || (scoreJ.equals(scoreJ1) && result.get(j).getId() > result.get(j + 1).getId())) {
                    LeaderboardVO temp = result.get(j);
                    result.set(j, result.get(j + 1));
                    result.set(j + 1, temp);
                }
            }
        }

        if (result.size() > 10) {
            result = result.subList(0, 10);
        }

        return result;
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
}
