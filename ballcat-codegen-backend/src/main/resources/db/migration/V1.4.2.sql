ALTER TABLE `gen_data_source_config`
    MODIFY COLUMN `url` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '数据库连接' AFTER `password`;