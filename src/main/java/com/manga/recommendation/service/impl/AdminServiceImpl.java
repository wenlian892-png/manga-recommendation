package com.manga.recommendation.service.impl;

import com.manga.recommendation.entity.*;
import com.manga.recommendation.mapper.*;
import com.manga.recommendation.service.AdminService;
import com.manga.recommendation.task.RecommendTask;
import com.manga.recommendation.vo.AdminStatsVO;
import com.manga.recommendation.vo.AlgorithmRunVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private MangaInfoMapper mangaInfoMapper;

    @Autowired
    private MangaScoreMapper mangaScoreMapper;

    @Autowired
    private UserCollectionMapper userCollectionMapper;

    @Autowired
    private ForumPostMapper forumPostMapper;

    @Autowired
    private ForumReplyMapper forumReplyMapper;

    @Autowired
    private ReadHistoryMapper readHistoryMapper;

    @Autowired
    private RecommendTask recommendTask;

    @Override
    public AdminStatsVO getDashboardStats() {
        AdminStatsVO stats = new AdminStatsVO();

        Long totalUsers = userInfoMapper.selectCount(null);
        stats.setTotalUsers(totalUsers);

        Long totalManga = mangaInfoMapper.selectCount(null);
        stats.setTotalManga(totalManga);

        Long totalScores = mangaScoreMapper.selectCount(null);
        stats.setTotalScores(totalScores);

        Long totalCollections = userCollectionMapper.selectCount(null);
        stats.setTotalCollections(totalCollections);

        Long totalPosts = forumPostMapper.selectCount(null);
        stats.setTotalPosts(totalPosts);

        Long totalReplies = forumReplyMapper.selectCount(null);
        stats.setTotalReplies(totalReplies);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date todayStart = calendar.getTime();

        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date tomorrowStart = calendar.getTime();

        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<ReadHistory> readQueryWrapper =
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        readQueryWrapper.ge("update_time", todayStart);
        readQueryWrapper.lt("update_time", tomorrowStart);
        Long todayReadCount = readHistoryMapper.selectCount(readQueryWrapper);
        stats.setTodayReadCount(todayReadCount);

        return stats;
    }

    @Override
    public AlgorithmRunVO runItemCFAlgorithm() {
        AlgorithmRunVO result = new AlgorithmRunVO();
        long startTime = System.currentTimeMillis();

        try {
            recommendTask.executeItemCFAlgorithm();
            long endTime = System.currentTimeMillis();
            result.setSuccess(true);
            result.setMessage("Item-CF 算法执行成功");
            result.setDurationMs(endTime - startTime);
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            result.setSuccess(false);
            result.setMessage("算法执行失败: " + e.getMessage());
            result.setDurationMs(endTime - startTime);
        }

        return result;
    }
}
