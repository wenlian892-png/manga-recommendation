package com.manga.recommendation.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class RecommendHubVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<MangaVO> hotZone;

    private List<MangaVO> categoryZone;

    private List<MangaVO> cfZone;

    private List<MangaVO> scoreZone;
}
