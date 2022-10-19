-- 模板组添加 group_key 属性，以前的模板组的 group_key 字段为其 id
ALTER TABLE `gen_template_group`
    ADD COLUMN `group_key` varchar(50) NULL COMMENT '标识' AFTER `id`;
UPDATE `gen_template_group` SET `group_key` = `id`;
ALTER TABLE `gen_template_group`
    MODIFY COLUMN `group_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '标识' AFTER `id`,
    ADD UNIQUE INDEX `group_key`(`group_key`, `deleted`);

-- 修改 template entry id 为 string 使用雪花算法
ALTER TABLE `gen_template_entry`
    MODIFY COLUMN `id` varchar(20) NOT NULL FIRST,
    MODIFY COLUMN `parent_id` varchar(20) NULL DEFAULT NULL COMMENT '父级Id' AFTER `type`;

-- 修改 group_id 关联为 group_key 关联
ALTER TABLE `gen_template_entry`
    CHANGE COLUMN `group_id` `group_key` varchar(50) NOT NULL COMMENT '模板组标识' AFTER `id`;
ALTER TABLE `gen_template_property`
    CHANGE COLUMN `group_id` `group_key` varchar(50) NOT NULL COMMENT '模板组标识' AFTER `id`;
ALTER TABLE `gen_field_type`
    CHANGE COLUMN `group_id` `group_key` varchar(50) NOT NULL COMMENT '模板组id' AFTER `id`;
ALTER TABLE `gen_type_script_type`
    CHANGE COLUMN `group_id` `group_key` varchar(50) NOT NULL COMMENT '模板组标识' AFTER `id`;


-- 唯一索引添加逻辑删除键做为联合索引
ALTER TABLE `gen_data_source_config`
DROP INDEX `ds_key`,
ADD UNIQUE INDEX `ds_key`(`ds_key`, `deleted`) USING BTREE;

ALTER TABLE `gen_template_property`
DROP INDEX `uk_group_id_prop_key`,
ADD UNIQUE INDEX `uk_group_id_prop_key`(`group_key`, `prop_key`, `deleted`) USING BTREE