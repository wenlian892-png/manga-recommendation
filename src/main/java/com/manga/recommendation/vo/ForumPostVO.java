package com.manga.recommendation.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class ForumPostVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long userId;

    private String username;

    private String title;

    private String content;

    private Integer viewCount;

    private LocalDateTime createTime;
}
