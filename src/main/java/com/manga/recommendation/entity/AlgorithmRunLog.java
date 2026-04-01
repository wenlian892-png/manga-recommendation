package com.manga.recommendation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("algorithm_run_log")
public class AlgorithmRunLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String algorithmType;

    private Integer status;

    private Integer userCount;

    private Integer mangaCount;

    private Integer scoreCount;

    private Integer recommendCount;

    private Long durationMs;

    private String errorMessage;

    private LocalDateTime createTime;
}
