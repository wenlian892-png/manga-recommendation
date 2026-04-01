package com.manga.recommendation.service;

import com.manga.recommendation.vo.CollectRequest;
import com.manga.recommendation.vo.ScoreRequest;

public interface InteractionService {

    void score(Long userId, ScoreRequest request);

    Float getUserScore(Long userId, Long mangaId);

    void collect(Long userId, CollectRequest request);

    Boolean isCollected(Long userId, Long mangaId);

    void uncollect(Long userId, Long mangaId);
}
