package com.manga.recommendation.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ForumReplyRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long postId;

    private String content;
}
