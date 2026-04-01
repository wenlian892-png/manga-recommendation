package com.manga.recommendation.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
public class MangaAddRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "漫画标题不能为空")
    @Size(max = 200, message = "标题不能超过200个字符")
    private String title;

    @NotBlank(message = "作者不能为空")
    @Size(max = 100, message = "作者不能超过100个字符")
    private String author;

    private String coverUrl;

    @NotBlank(message = "分类不能为空")
    private String category;

    @NotNull(message = "状态不能为空")
    private Integer status;

    @Size(max = 2000, message = "简介不能超过2000个字符")
    private String description;
}
