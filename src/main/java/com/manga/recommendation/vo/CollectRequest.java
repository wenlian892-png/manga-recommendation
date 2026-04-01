package com.manga.recommendation.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class CollectRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "漫画ID不能为空")
    private Long mangaId;
}
