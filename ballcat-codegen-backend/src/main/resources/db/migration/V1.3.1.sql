ALTER TABLE `gen_template_entry`
    CHANGE COLUMN `content` `file_content` longblob NULL COMMENT '模板内容' AFTER `parent_id`;

ALTER TABLE `gen_template_entry`
    MODIFY COLUMN `type` tinyint(1) NULL DEFAULT NULL COMMENT '文件类型 1：文件夹 2：模板文件 3: 二进制文件' AFTER `filename`;