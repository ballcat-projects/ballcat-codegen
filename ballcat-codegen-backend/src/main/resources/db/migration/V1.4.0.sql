ALTER TABLE `gen_template_property`
    ADD COLUMN `component_type` varchar(10) NOT NULL DEFAULT 'input' COMMENT '组件类型，前端显示的组件类型，input、select、radio' AFTER `default_value`,
    ADD COLUMN `component_options` json NULL COMMENT '组件选项，多选组件的选项配置' AFTER `component_type`,
    ADD COLUMN `order_value` int NOT NULL DEFAULT 0 COMMENT '排序值，值越小越靠前' AFTER `required`;