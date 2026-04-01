package com.manga.recommendation.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class MangaPageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer current = 1;

    private Integer size = 10;

    private String title;

    private String author;

    private String category;

    private Integer status;
}
