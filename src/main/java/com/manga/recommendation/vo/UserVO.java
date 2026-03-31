package com.manga.recommendation.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class UserVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private Integer role;

    private Integer status;

    private String token;

    private LocalDateTime createTime;
}
