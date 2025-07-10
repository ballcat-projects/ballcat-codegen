-- 1. 添加属性类型字段：form 表单属性 / computed 计算属性
ALTER TABLE `gen_template_property`
    ADD COLUMN `prop_type` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '属性类型：1=配置属性，2=计算属性' AFTER `prop_key`,
-- 2. 添加表达式字段，仅计算属性使用
    ADD COLUMN `expression` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '表达式内容，仅计算属性使用' AFTER `prop_type`;