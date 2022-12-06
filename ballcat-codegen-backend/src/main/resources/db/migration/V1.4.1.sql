ALTER TABLE `gen_data_source_config`
    MODIFY COLUMN `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '数据库连接' AFTER `password`,
    MODIFY COLUMN `deleted` bigint NOT NULL DEFAULT 0 COMMENT '逻辑删除标识' AFTER `url`,
    MODIFY COLUMN `create_time` datetime NOT NULL COMMENT '创建时间' AFTER `deleted`,
    MODIFY COLUMN `update_time` datetime NULL COMMENT '删除时间' AFTER `create_time`