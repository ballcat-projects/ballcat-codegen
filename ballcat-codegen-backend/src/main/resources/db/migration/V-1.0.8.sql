-- ----------------------------
-- Table structure for gen_field_type
-- ----------------------------
CREATE TABLE IF NOT EXISTS  `gen_field_type`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `group_id` bigint(0) NULL DEFAULT NULL COMMENT '模板组id',
  `column_key` varchar(25) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'DB属性类型',
  `column_value` varchar(25) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '对应属性类型',
  `db_type` char(1) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '数据库类型（1:MySQL，2:Oracle，3:PostGreSql，4:SqlServer）',
  `package_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '属性包路径+类名',
  `deleted` char(1) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT '0' COMMENT '逻辑删除字段（1删除0正常）',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'DB和后端数据类型' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gen_field_type
-- ----------------------------
INSERT INTO `gen_field_type` VALUES (1, 0, 'tinyint', 'Integer', '1', NULL, '0', '2022-07-01 10:20:32', NULL);
INSERT INTO `gen_field_type` VALUES (2, 0, 'smallint', 'Integer', '1', NULL, '0', '2022-07-01 10:21:02', NULL);
INSERT INTO `gen_field_type` VALUES (3, 0, 'mediumint', 'Integer', '1', NULL, '0', '2022-07-01 10:23:54', NULL);
INSERT INTO `gen_field_type` VALUES (4, 0, 'int', 'Integer', '1', NULL, '0', '2022-07-01 10:23:57', NULL);
INSERT INTO `gen_field_type` VALUES (5, 0, 'bigint', 'Long', '1', NULL, '0', '2022-07-01 10:23:59', NULL);
INSERT INTO `gen_field_type` VALUES (6, 0, 'float', 'Float', '1', NULL, '0', '2022-07-01 10:24:02', NULL);
INSERT INTO `gen_field_type` VALUES (7, 0, 'double', 'Double', '1', NULL, '0', '2022-07-01 10:24:04', NULL);
INSERT INTO `gen_field_type` VALUES (8, 0, 'decimal', 'BigDecimal', '1', 'java.math.BigDecimal', '0', '2022-07-01 10:24:06', NULL);
INSERT INTO `gen_field_type` VALUES (9, 0, 'numeric', 'BigDecimal', '1', 'java.math.BigDecimal', '0', '2022-07-01 10:24:09', NULL);
INSERT INTO `gen_field_type` VALUES (10, 0, 'bit', 'Boolean', '1', NULL, '0', '2022-07-01 10:24:12', NULL);
INSERT INTO `gen_field_type` VALUES (11, 0, 'char', 'String', '1', NULL, '0', '2022-07-01 10:24:14', NULL);
INSERT INTO `gen_field_type` VALUES (12, 0, 'varchar', 'String', '1', NULL, '0', '2022-07-01 10:24:37', NULL);
INSERT INTO `gen_field_type` VALUES (13, 0, 'tinytext', 'String', '1', NULL, '0', '2022-07-01 10:24:16', NULL);
INSERT INTO `gen_field_type` VALUES (14, 0, 'text', 'String', '1', NULL, '0', '2022-07-01 10:24:34', NULL);
INSERT INTO `gen_field_type` VALUES (15, 0, 'mediumtext', 'String', '1', NULL, '0', '2022-07-01 10:24:18', NULL);
INSERT INTO `gen_field_type` VALUES (16, 0, 'longtext', 'String', '1', NULL, '0', '2022-07-01 10:24:20', NULL);
INSERT INTO `gen_field_type` VALUES (17, 0, 'json', 'String', '1', NULL, '0', '2022-07-01 10:24:30', NULL);
INSERT INTO `gen_field_type` VALUES (18, 0, 'date', 'LocalDate', '1', 'java.time.LocalDate', '0', '2022-07-01 10:24:32', NULL);
INSERT INTO `gen_field_type` VALUES (19, 0, 'time', 'LocalTime', '1', 'java.time.LocalTime', '0', '2022-07-01 10:24:23', NULL);
INSERT INTO `gen_field_type` VALUES (20, 0, 'datetime', 'LocalDateTime', '1', 'java.time.LocalDateTime', '0', '2022-07-01 10:24:25', NULL);
INSERT INTO `gen_field_type` VALUES (21, 0, 'timestamp', 'LocalDateTime', '1', 'java.time.LocalDateTime', '0', '2022-07-01 10:24:27', NULL);
INSERT INTO `gen_field_type` VALUES (22, 0, 'INTEGER', 'Integer', '2', NULL, '0', '2022-07-01 10:32:34', NULL);
INSERT INTO `gen_field_type` VALUES (23, 0, 'BINARY_FLOAT', 'Float', '2', NULL, '0', '2022-07-01 10:32:37', NULL);
INSERT INTO `gen_field_type` VALUES (24, 0, 'BINARY_DOUBLE', 'Double', '2', NULL, '0', '2022-07-01 10:32:39', NULL);
INSERT INTO `gen_field_type` VALUES (25, 0, 'FLOAT', 'Float', '2', NULL, '0', '2022-07-01 10:32:42', NULL);
INSERT INTO `gen_field_type` VALUES (26, 0, 'number', 'Double', '2', NULL, '0', '2022-07-01 10:32:45', NULL);
INSERT INTO `gen_field_type` VALUES (27, 0, 'bit', 'Boolean', '2', NULL, '0', '2022-07-01 10:32:47', NULL);
INSERT INTO `gen_field_type` VALUES (28, 0, 'char', 'String', '2', NULL, '0', '2022-07-01 10:32:50', NULL);
INSERT INTO `gen_field_type` VALUES (29, 0, 'varchar', 'String', '2', NULL, '0', '2022-07-01 10:32:50', NULL);
INSERT INTO `gen_field_type` VALUES (30, 0, 'varchar2', 'String', '2', NULL, '0', '2022-07-01 10:32:50', NULL);
INSERT INTO `gen_field_type` VALUES (31, 0, 'nvarchar', 'String', '2', NULL, '0', '2022-07-01 10:32:50', NULL);
INSERT INTO `gen_field_type` VALUES (32, 0, 'nvarchar2', 'String', '2', NULL, '0', '2022-07-01 10:32:50', NULL);
INSERT INTO `gen_field_type` VALUES (33, 0, 'date', 'LocalDate', '2', 'java.time.LocalDate', '0', '2022-07-01 10:32:50', NULL);
INSERT INTO `gen_field_type` VALUES (34, 0, 'timestamp', 'LocalDateTime', '2', 'java.time.LocalDateTime', '0', '2022-07-01 10:24:27', NULL);
INSERT INTO `gen_field_type` VALUES (35, 0, 'smallint', 'Integer', '3', NULL, '0', '2022-07-01 10:24:27', NULL);
INSERT INTO `gen_field_type` VALUES (36, 0, 'integer', 'Integer', '3', NULL, '0', '2022-07-01 10:24:27', NULL);
INSERT INTO `gen_field_type` VALUES (37, 0, 'bigint', 'Long', '3', NULL, '0', '2022-07-01 10:24:27', NULL);
INSERT INTO `gen_field_type` VALUES (38, 0, 'decimal', 'BigDecimal', '3', 'java.math.BigDecimal', '0', '2022-07-01 10:24:27', NULL);
INSERT INTO `gen_field_type` VALUES (39, 0, 'numeric', 'BigDecimal', '3', 'java.math.BigDecimal', '0', '2022-07-01 10:24:27', NULL);
INSERT INTO `gen_field_type` VALUES (40, 0, 'mediumint', 'Integer', '3', NULL, '0', '2022-07-01 10:24:27', NULL);
INSERT INTO `gen_field_type` VALUES (41, 0, 'int', 'Integer', '3', NULL, '0', '2022-07-01 10:24:27', NULL);
INSERT INTO `gen_field_type` VALUES (42, 0, 'float', 'Float', '3', NULL, '0', '2022-07-01 10:24:27', NULL);
INSERT INTO `gen_field_type` VALUES (43, 0, 'double', 'Double', '3', NULL, '0', '2022-07-01 10:24:27', NULL);
INSERT INTO `gen_field_type` VALUES (44, 0, 'bit', 'Boolean', '3', NULL, '0', '2022-07-01 10:24:27', NULL);
INSERT INTO `gen_field_type` VALUES (45, 0, 'boolean', 'Boolean', '3', NULL, '0', '2022-07-01 10:24:27', NULL);
INSERT INTO `gen_field_type` VALUES (46, 0, 'char', 'String', '3', NULL, '0', '2022-07-01 10:24:27', NULL);
INSERT INTO `gen_field_type` VALUES (47, 0, 'varchar', 'String', '3', NULL, '0', '2022-07-01 10:24:27', NULL);
INSERT INTO `gen_field_type` VALUES (48, 0, 'character varying', 'String', '3', NULL, '0', '2022-07-01 10:24:27', NULL);
INSERT INTO `gen_field_type` VALUES (49, 0, 'character', 'String', '3', NULL, '0', '2022-07-01 10:24:27', NULL);
INSERT INTO `gen_field_type` VALUES (50, 0, 'text', 'String', '3', NULL, '0', '2022-07-01 10:24:27', NULL);
INSERT INTO `gen_field_type` VALUES (51, 0, 'json', 'String', '3', NULL, '0', '2022-07-01 10:24:27', NULL);
INSERT INTO `gen_field_type` VALUES (52, 0, 'date', 'LocalDate', '3', 'java.time.LocalDate', '0', '2022-07-01 10:24:27', NULL);
INSERT INTO `gen_field_type` VALUES (53, 0, 'time', 'LocalTime', '3', 'java.time.LocalTime', '0', '2022-07-01 10:24:27', NULL);
INSERT INTO `gen_field_type` VALUES (54, 0, 'timestamp', 'LocalDateTime', '3', 'java.time.LocalDateTime', '0', '2022-07-01 10:24:27', NULL);
INSERT INTO `gen_field_type` VALUES (55, 0, 'bit', 'Boolean', '4', NULL, '0', '2022-07-01 10:24:27', NULL);
INSERT INTO `gen_field_type` VALUES (56, 0, 'tinyint', 'Integer', '4', NULL, '0', '2022-07-01 10:24:27', NULL);
INSERT INTO `gen_field_type` VALUES (57, 0, 'smallint', 'Integer', '4', NULL, '0', '2022-07-01 10:24:27', NULL);
INSERT INTO `gen_field_type` VALUES (58, 0, 'int', 'Integer', '4', NULL, '0', '2022-07-01 10:24:27', NULL);
INSERT INTO `gen_field_type` VALUES (59, 0, 'bigint', 'Long', '4', NULL, '0', '2022-07-01 10:24:27', NULL);
INSERT INTO `gen_field_type` VALUES (60, 0, 'decimal', 'BigDecimal', '4', 'java.math.BigDecimal', '0', '2022-07-01 10:24:27', NULL);
INSERT INTO `gen_field_type` VALUES (61, 0, 'numeric', 'BigDecimal', '4', 'java.math.BigDecimal', '0', '2022-07-01 10:24:27', NULL);
INSERT INTO `gen_field_type` VALUES (62, 0, 'smallmoney', 'BigDecimal', '4', 'java.math.BigDecimal', '0', '2022-07-01 10:24:27', NULL);
INSERT INTO `gen_field_type` VALUES (63, 0, 'money', 'BigDecimal', '4', 'java.math.BigDecimal', '0', '2022-07-01 10:24:27', NULL);
INSERT INTO `gen_field_type` VALUES (64, 0, 'float', 'Float', '4', NULL, '0', '2022-07-01 10:24:27', NULL);
INSERT INTO `gen_field_type` VALUES (65, 0, 'real', 'Double', '4', NULL, '0', '2022-07-01 10:24:27', NULL);
INSERT INTO `gen_field_type` VALUES (66, 0, 'char', 'String', '4', NULL, '0', '2022-07-01 10:24:27', NULL);
INSERT INTO `gen_field_type` VALUES (67, 0, 'varchar', 'String', '4', NULL, '0', '2022-07-01 10:24:27', NULL);
INSERT INTO `gen_field_type` VALUES (68, 0, 'text', 'String', '4', NULL, '0', '2022-07-01 10:24:27', NULL);
INSERT INTO `gen_field_type` VALUES (69, 0, 'nchar', 'String', '4', NULL, '0', '2022-07-01 10:24:27', NULL);
INSERT INTO `gen_field_type` VALUES (70, 0, 'nvarchar', 'String', '4', NULL, '0', '2022-07-01 10:24:27', NULL);
INSERT INTO `gen_field_type` VALUES (71, 0, 'ntext', 'String', '4', NULL, '0', '2022-07-01 10:24:27', NULL);
INSERT INTO `gen_field_type` VALUES (72, 0, 'binary', 'String', '4', NULL, '0', '2022-07-01 10:24:27', NULL);
INSERT INTO `gen_field_type` VALUES (73, 0, 'date', 'LocalDate', '4', 'java.time.LocalDate', '0', '2022-07-01 10:24:27', NULL);
INSERT INTO `gen_field_type` VALUES (74, 0, 'time', 'LocalTime', '4', 'java.time.LocalTime', '0', '2022-07-01 10:24:27', NULL);
INSERT INTO `gen_field_type` VALUES (75, 0, 'datetime', 'LocalDateTime', '4', 'java.time.LocalDateTime', '0', '2022-07-01 10:24:27', NULL);
INSERT INTO `gen_field_type` VALUES (76, 0, 'timestamp', 'LocalDateTime', '4', 'java.time.LocalDateTime', '0', '2022-07-01 10:24:27', NULL);

-- ----------------------------
-- Table structure for gen_type_script_type
-- ----------------------------
CREATE TABLE IF NOT EXISTS  `gen_type_script_type`  (
    `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `group_id` bigint(0) NULL DEFAULT NULL COMMENT '模板组id',
    `code_key` varchar(15) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'Java对应类型',
    `code_value` varchar(15) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '界面对应类型',
    `deleted` char(1) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '0' COMMENT '逻辑删除（0：正常，1：删除）',
    `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '前端和后端数据类型管理' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gen_type_script_type
-- ----------------------------
INSERT INTO `gen_type_script_type` VALUES (1, 0, 'Short', 'number', '0', '2022-07-01 13:46:32', NULL);
INSERT INTO `gen_type_script_type` VALUES (2, 0, 'Integer', 'number', '0', '2022-07-01 13:46:32', NULL);
INSERT INTO `gen_type_script_type` VALUES (3, 0, 'Long', 'number', '0', '2022-07-01 13:46:32', NULL);
INSERT INTO `gen_type_script_type` VALUES (4, 0, 'Float', 'string', '0', '2022-07-01 13:46:32', NULL);
INSERT INTO `gen_type_script_type` VALUES (5, 0, 'Double', 'string', '0', '2022-07-01 13:46:32', NULL);
INSERT INTO `gen_type_script_type` VALUES (6, 0, 'BigDecimal', 'string', '0', '2022-07-01 13:46:32', NULL);
INSERT INTO `gen_type_script_type` VALUES (7, 0, 'Boolean', 'boolean', '0', '2022-07-01 13:46:32', NULL);
INSERT INTO `gen_type_script_type` VALUES (8, 0, 'Char', 'string', '0', '2022-07-01 13:46:32', NULL);
INSERT INTO `gen_type_script_type` VALUES (9, 0, 'String', 'string', '0', '2022-07-01 13:46:32', NULL);
INSERT INTO `gen_type_script_type` VALUES (10, 0, 'LocalDate', 'string', '0', '2022-07-01 13:46:32', NULL);
INSERT INTO `gen_type_script_type` VALUES (11, 0, 'LocalDateTime', 'string', '0', '2022-07-01 13:46:32', NULL);