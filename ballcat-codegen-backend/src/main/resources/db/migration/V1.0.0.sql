-- ----------------------------
-- Table structure for gen_template_property
-- ----------------------------
CREATE TABLE IF NOT EXISTS `gen_template_property`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `group_id` int(11) NULL DEFAULT NULL COMMENT '模板组ID',
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标题',
  `prop_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '属性键',
  `default_value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '默认值',
  `required` tinyint(1) NULL DEFAULT NULL COMMENT '必填，1：是，0：否',
  `remarks` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注信息',
  `deleted` bigint(20) NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_group_id_prop_key`(`group_id`, `prop_key`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '模板属性配置' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for gen_template_group
-- ----------------------------
CREATE TABLE IF NOT EXISTS `gen_template_group`  (
   `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
   `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '名称',
   `remarks` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
   `deleted` bigint(20) NULL DEFAULT 0 COMMENT '逻辑删除',
   `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
   `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
   PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '模板组' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for gen_template_entry
-- ----------------------------
CREATE TABLE IF NOT EXISTS `gen_template_entry`  (
   `id` int(11) NOT NULL AUTO_INCREMENT,
   `group_id` int(11) NULL DEFAULT NULL COMMENT '模板组Id',
   `filename` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件夹路径/模板文件名称（支持占位符）',
   `type` tinyint(1) NULL DEFAULT NULL COMMENT '文件类型 1：文件夹 2：模板文件',
   `parent_id` int(11) NULL DEFAULT NULL COMMENT '父级Id',
   `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '模板内容',
   `engine_type` tinyint(1) NULL DEFAULT NULL COMMENT '模板引擎类型 1：velocity',
   `remarks` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
   `deleted` bigint(255) NULL DEFAULT NULL COMMENT '逻辑删除标识',
   `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
   `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
   PRIMARY KEY (`id`) USING BTREE,
   UNIQUE INDEX `uk_name_parent_id`(`deleted`, `group_id`, `parent_id`, `filename`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '模板文件目录项' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for gen_data_source_config
-- ----------------------------
CREATE TABLE IF NOT EXISTS `gen_data_source_config`  (
   `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
   `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '标题',
   `ds_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '数据源key',
   `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '数据库用户名',
   `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '数据库密码',
   `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '数据库连接',
   `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
   `update_time` datetime NULL DEFAULT NULL COMMENT '删除时间',
   PRIMARY KEY (`id`) USING BTREE,
   UNIQUE INDEX `ds_key`(`ds_key`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '数据源' ROW_FORMAT = DYNAMIC;
