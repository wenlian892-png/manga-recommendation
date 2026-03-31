package com.manga.recommendation.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class AdminMangaVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String title;

    private String author;

    private String coverUrl;

    private String category;

    private Integer status;

    private String description;

    private Integer clickCount;

    private Integer isDeleted;

    private Integer version;

    private LocalDateTime createTime;
}
