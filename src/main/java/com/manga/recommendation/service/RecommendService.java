package com.manga.recommendation.service;

import com.manga.recommendation.vo.RecommendHubVO;

public interface RecommendService {

    RecommendHubVO getRecommendHub(Long userId);
}
