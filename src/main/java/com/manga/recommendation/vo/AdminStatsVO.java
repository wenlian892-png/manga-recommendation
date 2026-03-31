package com.manga.recommendation.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class AdminStatsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long totalUsers;

    private Long totalManga;

    private Long totalScores;

    private Long todayReadCount;

    private Long totalCollections;

    private Long totalPosts;

    private Long totalReplies;
}
