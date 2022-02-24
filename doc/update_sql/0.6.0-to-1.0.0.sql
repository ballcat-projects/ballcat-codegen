-- 重命名
RENAME TABLE `gen_template_directory_entry` TO `gen_template_entry`;

-- 字段添加
ALTER TABLE `gen_template_entry`
    ADD COLUMN `content` longtext NULL COMMENT '模板内容' AFTER `parent_id`,
ADD COLUMN `engine_type` tinyint(1) NULL COMMENT '模板引擎类型 1：velocity' AFTER `content`,
ADD COLUMN `remarks` varchar(255) NULL COMMENT '备注' AFTER `engine_type`

-- 数据迁移
update gen_template_entry e, gen_template_info i
set e.content = i.content, e.engine_type = i.engine_type, e.remarks = i.remarks
where e.id = i.directory_entry_id

-- 删除无用表
DROP TABLE `gen_template_info`;