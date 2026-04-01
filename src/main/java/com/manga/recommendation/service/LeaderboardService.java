package com.manga.recommendation.service;

import com.manga.recommendation.vo.LeaderboardVO;

import java.util.List;

public interface LeaderboardService {

    List<LeaderboardVO> getHotLeaderboard();

    List<LeaderboardVO> getScoreLeaderboard();
}
