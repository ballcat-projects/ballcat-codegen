-- 模板组添加缩略图
ALTER TABLE `gen_template_group`
    ADD COLUMN `icon` longtext NULL COMMENT '缩略图' AFTER `name`;

-- 模板组添加标识以区分生成时是否需要选择数据表
ALTER TABLE `gen_template_group`
    ADD COLUMN `use_table` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否需要使用使用数据表' AFTER `icon`;

-- 设置部分字段不为 null
ALTER TABLE `gen_template_group`
    MODIFY COLUMN `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '名称' AFTER `group_key`,
    MODIFY COLUMN `deleted` bigint(20) NOT NULL DEFAULT 0 COMMENT '逻辑删除' AFTER `remarks`;