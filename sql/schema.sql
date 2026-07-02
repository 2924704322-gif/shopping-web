-- ============================================================
-- 购物平台信息系统 - 数据库建表脚本
-- 版本: 1.0.0
-- 数据库: MySQL 8.0+
-- 字符集: utf8mb4
-- ============================================================

-- 创建数据库（如已存在则跳过）
CREATE DATABASE IF NOT EXISTS shopping
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_general_ci;

USE shopping;

-- ============================================================
-- 1. 用户表 (user)
-- ============================================================
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '用户ID',
    `username`        VARCHAR(50)  NOT NULL                 COMMENT '登录用户名',
    `password`        VARCHAR(200) NOT NULL                 COMMENT 'BCrypt加密密码',
    `nickname`        VARCHAR(50)  DEFAULT NULL             COMMENT '昵称/显示名',
    `email`           VARCHAR(100) DEFAULT NULL             COMMENT '邮箱',
    `phone`           VARCHAR(20)  DEFAULT NULL             COMMENT '手机号',
    `avatar`          VARCHAR(255) DEFAULT NULL             COMMENT '头像URL',
    `gender`          TINYINT(1)   DEFAULT 0                COMMENT '性别: 0=未知 1=男 2=女',
    `birthday`        DATE         DEFAULT NULL             COMMENT '生日',
    `status`          TINYINT(1)   DEFAULT 1                COMMENT '账户状态: 0=禁用 1=启用',
    `last_login_time` DATETIME     DEFAULT NULL             COMMENT '最后登录时间',
    `last_login_ip`   VARCHAR(50)  DEFAULT NULL             COMMENT '最后登录IP',
    `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`         TINYINT(1)   DEFAULT 0                COMMENT '逻辑删除: 0=正常 1=已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_email`    (`email`),
    UNIQUE KEY `uk_phone`    (`phone`),
    KEY `idx_status`         (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户表';

-- ============================================================
-- 2. 角色表 (role)
-- ============================================================
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '角色ID',
    `role_name`   VARCHAR(50)  NOT NULL                 COMMENT '角色名称（显示用）',
    `role_code`   VARCHAR(50)  NOT NULL                 COMMENT '角色编码（如 ROLE_ADMIN）',
    `description` VARCHAR(200) DEFAULT NULL             COMMENT '角色描述',
    `status`      TINYINT(1)   DEFAULT 1                COMMENT '状态: 0=禁用 1=启用',
    `sort`        INT          DEFAULT 0                COMMENT '排序号',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     TINYINT(1)   DEFAULT 0                COMMENT '逻辑删除: 0=正常 1=已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_name` (`role_name`),
    UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色表';

-- ============================================================
-- 3. 用户角色关联表 (user_role)
--    注意: 此表不使用逻辑删除，撤销角色即物理删除行
-- ============================================================
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role` (
    `id`          BIGINT   NOT NULL AUTO_INCREMENT  COMMENT '关联ID',
    `user_id`     BIGINT   NOT NULL                 COMMENT '用户ID（逻辑外键 → user.id）',
    `role_id`     BIGINT   NOT NULL                 COMMENT '角色ID（逻辑外键 → role.id）',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
    KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户角色关联表';

-- ============================================================
-- 4. 分类表 (category) - 两级邻接表模型
--    parent_id 为 NULL 表示一级分类
--    level 字段为有意冗余，避免递归计算层级
-- ============================================================
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '分类ID',
    `name`        VARCHAR(50)  NOT NULL                 COMMENT '分类名称',
    `parent_id`   BIGINT       DEFAULT NULL             COMMENT '父分类ID（NULL=一级分类）',
    `level`       TINYINT(1)   DEFAULT 1                COMMENT '层级: 1=一级 2=二级',
    `sort`        INT          DEFAULT 0                COMMENT '同级排序号',
    `icon`        VARCHAR(255) DEFAULT NULL             COMMENT '图标URL',
    `status`      TINYINT(1)   DEFAULT 1                COMMENT '状态: 0=禁用 1=启用',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     TINYINT(1)   DEFAULT 0                COMMENT '逻辑删除: 0=正常 1=已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_parent_name` (`parent_id`, `name`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_level`     (`level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='商品分类表';

-- ============================================================
-- 5. 商品表 (product)
--    sub_images 以 JSON 数组存储副图 URL
-- ============================================================
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
    `id`             BIGINT         NOT NULL AUTO_INCREMENT  COMMENT '商品ID',
    `name`           VARCHAR(200)   NOT NULL                 COMMENT '商品名称',
    `description`    TEXT           DEFAULT NULL             COMMENT '商品描述/详情',
    `category_id`    BIGINT         NOT NULL                 COMMENT '所属分类ID（逻辑外键 → category.id）',
    `price`          DECIMAL(10,2)  NOT NULL                 COMMENT '售价',
    `original_price` DECIMAL(10,2)  DEFAULT NULL             COMMENT '原价（划线价）',
    `stock`          INT            DEFAULT 0                COMMENT '库存数量',
    `main_image`     VARCHAR(255)   DEFAULT NULL             COMMENT '商品主图URL',
    `sub_images`     TEXT           DEFAULT NULL             COMMENT '副图URL列表（JSON数组: ["url1","url2"]）',
    `unit`           VARCHAR(20)    DEFAULT '件'             COMMENT '计量单位',
    `status`         TINYINT(1)     DEFAULT 1                COMMENT '上下架: 0=下架 1=上架',
    `sales`          INT            DEFAULT 0                COMMENT '累计销量',
    `sort`           INT            DEFAULT 0                COMMENT '排序号',
    `create_time`    DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`        TINYINT(1)     DEFAULT 0                COMMENT '逻辑删除: 0=正常 1=已删除',
    PRIMARY KEY (`id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_status`      (`status`),
    KEY `idx_name`        (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='商品表';

-- ============================================================
-- 6. 预置数据：默认角色
-- ============================================================
INSERT INTO `role` (`role_name`, `role_code`, `description`, `sort`) VALUES
('系统管理员', 'ROLE_ADMIN',  '系统最高权限，管理所有模块', 1),
('普通用户',   'ROLE_USER',   '基础用户权限，浏览和购买商品', 2),
('商家',       'ROLE_SELLER', '商家权限，管理自有商品', 3);

-- ============================================================
-- 执行完毕
-- ============================================================
