package com.manga.recommendation.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
public class ForumReplyRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "帖子ID不能为空")
    private Long postId;

    @NotBlank(message = "回复内容不能为空")
    @Size(max = 2000, message = "回复内容不能超过2000个字符")
    private String content;
}
