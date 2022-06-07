-- 重命名
RENAME TABLE `gen_template_directory_entry` TO `gen_template_entry`;

-- 字段添加
ALTER TABLE `gen_template_entry` ADD COLUMN `content` LONGTEXT NULL COMMENT '模板内容' AFTER `parent_id`,
                                 ADD COLUMN `engine_type` TINYINT ( 1 ) NULL COMMENT '模板引擎类型 1：velocity' AFTER `content`,
                                 ADD COLUMN `remarks` VARCHAR ( 255 ) NULL COMMENT '备注' AFTER `engine_type`;

-- 数据迁移
UPDATE gen_template_entry e,gen_template_info i SET e.content = i.content, e.engine_type = i.engine_type, e.remarks = i.remarks WHERE e.id = i.directory_entry_id;

-- 删除无用表
DROP TABLE `gen_template_info`;

-- 修改文件名字段 file_name => filename
ALTER TABLE `gen_template_entry` CHANGE COLUMN `file_name` `filename` VARCHAR ( 255 ) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件夹路径/模板文件名称（支持占位符）' AFTER `group_id`;

-- 新增数据源标题
ALTER TABLE `gen_data_source_config` ADD COLUMN title VARCHAR ( 255 ) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标题' AFTER `id`;

-- 修改字段名name => ds_key
ALTER TABLE `gen_data_source_config` CHANGE COLUMN `name` `ds_key` VARCHAR ( 255 ) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '数据源key' AFTER `title`;

-- 初始化 title 数据
UPDATE `gen_data_source_config` SET title = `ds_key`;
-- 设置不允许为 null，ds_key 唯一
ALTER TABLE `gen_data_source_config` MODIFY COLUMN `title` VARCHAR ( 255 ) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '标题' AFTER `id`,
                                     MODIFY COLUMN `ds_key` VARCHAR ( 255 ) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '数据源key' AFTER `title`,ADD UNIQUE INDEX ( `ds_key` );