package com.manga.recommendation.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class AlgorithmRunVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean success;

    private String message;

    private Long durationMs;
}
