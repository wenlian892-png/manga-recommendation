package com.manga.recommendation.controller;

import com.manga.recommendation.common.Result;
import com.manga.recommendation.service.AdminService;
import com.manga.recommendation.vo.AdminStatsVO;
import com.manga.recommendation.vo.AlgorithmRunVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/dashboard/stats")
    public Result<AdminStatsVO> getDashboardStats() {
        AdminStatsVO stats = adminService.getDashboardStats();
        return Result.success(stats);
    }

    @PostMapping("/algorithm/run")
    public Result<AlgorithmRunVO> runAlgorithm() {
        AlgorithmRunVO result = adminService.runItemCFAlgorithm();
        if (result.getSuccess()) {
            return Result.success(result);
        } else {
            return Result.error(500, result.getMessage());
        }
    }
}
