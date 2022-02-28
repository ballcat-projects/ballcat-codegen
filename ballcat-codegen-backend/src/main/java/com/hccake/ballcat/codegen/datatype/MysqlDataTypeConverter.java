package com.hccake.ballcat.codegen.datatype;

import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Hccake
 * @version 1.0
 * @date 2020/6/22 17:53
 */
@UtilityClass
public class MysqlDataTypeConverter {

	public static final String DEFAULT_TYPE = "unknownType";

	static final Map<String, String> MYSQL_TO_JAVA = new HashMap<>();

	static {
		MYSQL_TO_JAVA.put("tinyint", "Integer");
		MYSQL_TO_JAVA.put("smallint", "Integer");
		MYSQL_TO_JAVA.put("mediumint", "Integer");
		MYSQL_TO_JAVA.put("int", "Integer");
		MYSQL_TO_JAVA.put("integer", "Integer");
		MYSQL_TO_JAVA.put("bigint", "Long");
		MYSQL_TO_JAVA.put("float", "Float");
		MYSQL_TO_JAVA.put("double", "Double");
		MYSQL_TO_JAVA.put("decimal", "BigDecimal");
		MYSQL_TO_JAVA.put("bit", "Boolean");

		MYSQL_TO_JAVA.put("char", "String");
		MYSQL_TO_JAVA.put("varchar", "String");
		MYSQL_TO_JAVA.put("tinytext", "String");
		MYSQL_TO_JAVA.put("text", "String");
		MYSQL_TO_JAVA.put("mediumtext", "String");
		MYSQL_TO_JAVA.put("longtext", "String");

		MYSQL_TO_JAVA.put("date", "LocalDateTime");
		MYSQL_TO_JAVA.put("datetime", "LocalDateTime");
		MYSQL_TO_JAVA.put("timestamp", "LocalDateTime");
	}

	public static String getJavaOfMysql(String mysqlType) {
		return MYSQL_TO_JAVA.getOrDefault(mysqlType, DEFAULT_TYPE);
	}

}
