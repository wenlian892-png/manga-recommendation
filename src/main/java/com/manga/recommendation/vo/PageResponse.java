package com.manga.recommendation.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PageResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long total;

    private List<T> records;

    public PageResponse(Long total, List<T> records) {
        this.total = total;
        this.records = records;
    }
}
