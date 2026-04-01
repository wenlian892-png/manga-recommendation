package com.manga.recommendation.vo;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class ScoreRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "漫画ID不能为空")
    private Long mangaId;

    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分最低为1分")
    @Max(value = 5, message = "评分最高为5分")
    private Float score;
}
