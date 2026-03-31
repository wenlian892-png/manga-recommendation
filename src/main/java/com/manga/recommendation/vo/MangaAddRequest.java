package com.manga.recommendation.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class MangaAddRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String title;

    private String author;

    private String coverUrl;

    private String category;

    private Integer status;

    private String description;
}
