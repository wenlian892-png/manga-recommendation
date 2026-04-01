package com.manga.recommendation.controller;

import com.manga.recommendation.common.Result;
import com.manga.recommendation.service.LeaderboardService;
import com.manga.recommendation.vo.LeaderboardVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/manga")
public class LeaderboardController {

    @Autowired
    private LeaderboardService leaderboardService;

    @GetMapping("/leaderboard")
    public Result<List<LeaderboardVO>> getLeaderboard(@RequestParam(required = false, defaultValue = "hot") String type) {
        List<LeaderboardVO> list;
        if ("hot".equals(type)) {
            list = leaderboardService.getHotLeaderboard();
        } else if ("score".equals(type)) {
            list = leaderboardService.getScoreLeaderboard();
        } else {
            return Result.error(400, "无效的榜单类型");
        }
        return new Result<>(200, "success", list);
    }
}
