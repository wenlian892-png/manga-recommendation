package com.manga.recommendation.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class LeaderboardVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String title;

    private String author;

    private String coverUrl;

    private Integer clickCount;

    private Float avgScore;
}
