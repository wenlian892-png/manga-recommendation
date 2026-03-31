package com.manga.recommendation.service;

import com.manga.recommendation.vo.AdminStatsVO;
import com.manga.recommendation.vo.AlgorithmRunVO;

public interface AdminService {

    AdminStatsVO getDashboardStats();

    AlgorithmRunVO runItemCFAlgorithm();
}
