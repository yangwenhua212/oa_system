-- 讨论区回复表
CREATE TABLE IF NOT EXISTS `aoa_discuss_reply` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '回复ID',
    `discuss_id` BIGINT NOT NULL COMMENT '讨论ID',
    `content` TEXT COMMENT '回复内容',
    `user_id` BIGINT COMMENT '回复用户ID',
    `user_name` VARCHAR(50) COMMENT '回复用户名',
    `reply_id` BIGINT DEFAULT 0 COMMENT '父回复ID（回复某条回复时）',
    `like_count` INT DEFAULT 0 COMMENT '点赞数',
    `del_flag` TINYINT DEFAULT 0 COMMENT '删除标志',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT COMMENT '创建者',
    `update_by` BIGINT COMMENT '更新者',
    PRIMARY KEY (`id`),
    KEY `idx_discuss_id` (`discuss_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='讨论回复表';

-- 讨论点赞表
CREATE TABLE IF NOT EXISTS `aoa_discuss_like` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `discuss_id` BIGINT DEFAULT NULL COMMENT '帖子ID（点赞帖子时）',
    `reply_id` BIGINT DEFAULT NULL COMMENT '回复ID（点赞回复时）',
    `user_id` BIGINT NOT NULL COMMENT '点赞用户ID',
    `del_flag` TINYINT DEFAULT 0,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_by` BIGINT COMMENT '创建者',
    `update_by` BIGINT COMMENT '更新者',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_discuss_user` (`discuss_id`, `user_id`),
    UNIQUE KEY `uk_reply_user` (`reply_id`, `user_id`),
    KEY `idx_discuss_id` (`discuss_id`),
    KEY `idx_reply_id` (`reply_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='讨论点赞表';

-- 投票选项表
CREATE TABLE IF NOT EXISTS `aoa_discuss_vote_item` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `discuss_id` BIGINT NOT NULL COMMENT '讨论ID',
    `content` VARCHAR(200) NOT NULL COMMENT '选项内容',
    `vote_count` INT DEFAULT 0 COMMENT '得票数',
    `del_flag` TINYINT DEFAULT 0,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_by` BIGINT COMMENT '创建者',
    `update_by` BIGINT COMMENT '更新者',
    PRIMARY KEY (`id`),
    KEY `idx_discuss_id` (`discuss_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='投票选项表';

-- 投票记录表
CREATE TABLE IF NOT EXISTS `aoa_discuss_vote_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `discuss_id` BIGINT NOT NULL COMMENT '讨论ID',
    `vote_item_id` BIGINT NOT NULL COMMENT '选项ID',
    `user_id` BIGINT NOT NULL COMMENT '投票用户ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_by` BIGINT COMMENT '创建者',
    `update_by` BIGINT COMMENT '更新者',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_discuss_user` (`discuss_id`, `user_id`),
    KEY `idx_discuss_id` (`discuss_id`),
    KEY `idx_vote_item_id` (`vote_item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='投票记录表';

-- =========================================
-- 通讯录分享表
-- =========================================
CREATE TABLE IF NOT EXISTS `oa_address_share` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `contact_id` BIGINT NOT NULL COMMENT '联系人ID',
    `shared_by` BIGINT NOT NULL COMMENT '分享者用户ID',
    `shared_with` BIGINT NOT NULL COMMENT '被分享用户ID',
    `del_flag` TINYINT DEFAULT 0 COMMENT '删除标志',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT COMMENT '创建者',
    `update_by` BIGINT COMMENT '更新者',
    PRIMARY KEY (`id`),
    KEY `idx_contact_id` (`contact_id`),
    KEY `idx_shared_by` (`shared_by`),
    KEY `idx_shared_with` (`shared_with`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通讯录分享表';

-- =========================================
-- 用户表补充字段（地址、学历、银行账号等）
-- 注意：init.sql 已包含以下字段定义，此处仅作为增量补丁兼容旧库
-- =========================================
SET @schema = DATABASE();
SET @col_exists = (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @schema AND TABLE_NAME = 'sys_user' AND COLUMN_NAME = 'address');
SET @sql = IF(@col_exists = 0,
    'ALTER TABLE `sys_user`
     ADD COLUMN `address` VARCHAR(255) DEFAULT NULL COMMENT ''地址'' AFTER `remark`,
     ADD COLUMN `school` VARCHAR(100) DEFAULT NULL COMMENT ''毕业院校'' AFTER `address`,
     ADD COLUMN `bank_account` VARCHAR(50) DEFAULT NULL COMMENT ''银行账号'' AFTER `school`,
     ADD COLUMN `education` VARCHAR(50) DEFAULT NULL COMMENT ''学历'' AFTER `bank_account`,
     ADD COLUMN `id_card` VARCHAR(20) DEFAULT NULL COMMENT ''身份证号'' AFTER `education`,
     ADD COLUMN `salary` DECIMAL(10,2) DEFAULT NULL COMMENT ''工资'' AFTER `id_card`,
     ADD COLUMN `skin` VARCHAR(20) DEFAULT ''blue'' COMMENT ''皮肤'' AFTER `salary`',
    'SELECT 1 AS status');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
