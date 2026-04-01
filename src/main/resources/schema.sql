-- ============================================================
-- 漫画推荐系统 · 数据库建表脚本
-- 数据库: manga_rec_db
-- 需先在 Navicat 中创建数据库: CREATE DATABASE manga_rec_db DEFAULT CHARACTER SET utf8mb4;
-- ============================================================

USE manga_rec_db;

-- ============================================================
-- 1. 用户信息表
-- ============================================================
CREATE TABLE IF NOT EXISTS `user_info` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(255) NOT NULL COMMENT '密码（BCrypt加密）',
  `role` TINYINT NOT NULL DEFAULT 0 COMMENT '角色: 0=普通用户, 1=管理员',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 1=正常, 0=封禁',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';

-- ============================================================
-- 2. 漫画信息表
-- ============================================================
CREATE TABLE IF NOT EXISTS `manga_info` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '漫画ID',
  `title` VARCHAR(200) NOT NULL COMMENT '漫画标题',
  `author` VARCHAR(100) NOT NULL COMMENT '作者',
  `cover_url` VARCHAR(500) NOT NULL COMMENT '封面图URL',
  `category` VARCHAR(50) NOT NULL COMMENT '分类',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 1=连载中, 0=已完结',
  `description` TEXT COMMENT '简介',
  `click_count` INT NOT NULL DEFAULT 0 COMMENT '点击量',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0=未删除, 1=已删除',
  `version` INT NOT NULL DEFAULT 1 COMMENT '乐观锁版本号',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_category` (`category`),
  KEY `idx_status` (`status`),
  KEY `idx_click_count` (`click_count`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='漫画信息表';

-- ============================================================
-- 3. 漫画章节表
-- ============================================================
CREATE TABLE IF NOT EXISTS `manga_chapter` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '章节ID',
  `manga_id` BIGINT NOT NULL COMMENT '漫画ID',
  `chapter_number` INT NOT NULL COMMENT '章节编号',
  `title` VARCHAR(200) NOT NULL COMMENT '章节标题',
  `content` TEXT COMMENT '章节内容/图片地址',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_manga_id` (`manga_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='漫画章节表';

-- ============================================================
-- 4. 漫画评分表（核心 - Item-CF 依赖此表）
-- ============================================================
CREATE TABLE IF NOT EXISTS `manga_score` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评分记录ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `manga_id` BIGINT NOT NULL COMMENT '漫画ID',
  `score` FLOAT NOT NULL COMMENT '评分值(1.0-5.0)',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评分时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_manga` (`user_id`, `manga_id`),
  KEY `idx_manga_id` (`manga_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='漫画评分表';

-- ============================================================
-- 5. 用户收藏表
-- ============================================================
CREATE TABLE IF NOT EXISTS `user_collection` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `manga_id` BIGINT NOT NULL COMMENT '漫画ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_manga` (`user_id`, `manga_id`),
  KEY `idx_manga_id` (`manga_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收藏表';

-- ============================================================
-- 6. 阅读历史表
-- ============================================================
CREATE TABLE IF NOT EXISTS `read_history` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `manga_id` BIGINT NOT NULL COMMENT '漫画ID',
  `chapter_id` BIGINT DEFAULT NULL COMMENT '章节ID',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后阅读时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '首次阅读时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_update_time` (`update_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='阅读历史表';

-- ============================================================
-- 7. 漫画评论表
-- ============================================================
CREATE TABLE IF NOT EXISTS `manga_comment` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `manga_id` BIGINT NOT NULL COMMENT '漫画ID',
  `content` TEXT NOT NULL COMMENT '评论内容',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间',
  PRIMARY KEY (`id`),
  KEY `idx_manga_id` (`manga_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='漫画评论表';

-- ============================================================
-- 8. 推荐结果表（Item-CF 算法输出）
-- ============================================================
CREATE TABLE IF NOT EXISTS `recommend_result` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '推荐记录ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `manga_id` BIGINT NOT NULL COMMENT '漫画ID',
  `predict_score` FLOAT NOT NULL COMMENT '预测评分',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_manga` (`user_id`, `manga_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='推荐结果表';

-- ============================================================
-- 9. 系统公告表
-- ============================================================
CREATE TABLE IF NOT EXISTS `sys_announcement` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `title` VARCHAR(200) NOT NULL COMMENT '公告标题',
  `content` TEXT NOT NULL COMMENT '公告内容',
  `type` TINYINT NOT NULL DEFAULT 1 COMMENT '类型: 1=系统公告, 2=活动公告',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统公告表';

-- ============================================================
-- 10. 论坛帖子表
-- ============================================================
CREATE TABLE IF NOT EXISTS `forum_post` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '帖子ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `title` VARCHAR(200) NOT NULL COMMENT '帖子标题',
  `content` TEXT NOT NULL COMMENT '帖子内容',
  `view_count` INT NOT NULL DEFAULT 0 COMMENT '浏览量',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='论坛帖子表';

-- ============================================================
-- 11. 论坛回复表
-- ============================================================
CREATE TABLE IF NOT EXISTS `forum_reply` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '回复ID',
  `post_id` BIGINT NOT NULL COMMENT '帖子ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `content` TEXT NOT NULL COMMENT '回复内容',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '回复时间',
  PRIMARY KEY (`id`),
  KEY `idx_post_id` (`post_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='论坛回复表';

-- ============================================================
-- 12. 算法运行日志表
-- ============================================================
CREATE TABLE IF NOT EXISTS `algorithm_run_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `algorithm_type` VARCHAR(50) NOT NULL DEFAULT 'Item-CF' COMMENT '算法类型',
  `status` TINYINT NOT NULL COMMENT '执行状态: 0=失败, 1=成功',
  `user_count` INT DEFAULT 0 COMMENT '参与计算的用户数',
  `manga_count` INT DEFAULT 0 COMMENT '参与计算的漫画数',
  `score_count` INT DEFAULT 0 COMMENT '参与计算的评分记录数',
  `recommend_count` INT DEFAULT 0 COMMENT '生成的推荐结果数',
  `duration_ms` BIGINT DEFAULT 0 COMMENT '执行耗时(毫秒)',
  `error_message` TEXT COMMENT '错误信息',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '执行时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='算法运行日志表';
