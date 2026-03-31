package com.manga.recommendation.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ScoreRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long mangaId;

    private Float score;
}
