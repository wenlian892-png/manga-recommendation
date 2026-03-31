package com.manga.recommendation.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ForumDetailVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private ForumPostVO post;

    private List<ForumReplyVO> replies;
}
