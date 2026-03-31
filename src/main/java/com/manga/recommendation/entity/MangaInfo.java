package com.manga.recommendation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("manga_info")
public class MangaInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String author;

    private String coverUrl;

    private String category;

    private Integer status;

    private String description;

    private Integer clickCount;

    private Integer isDeleted;

    private Integer version;

    private LocalDateTime createTime;
}
