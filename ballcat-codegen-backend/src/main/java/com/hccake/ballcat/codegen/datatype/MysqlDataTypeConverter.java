package com.hccake.ballcat.codegen.datatype;

import java.util.HashMap;
import java.util.Map;
import lombok.experimental.UtilityClass;

/**
 * @author Hccake
 * @version 1.0
 * @date 2020/6/22 17:53
 */
@UtilityClass
public class MysqlDataTypeConverter {

	public static final String DEFAULT_TYPE = "unknownType";

	public static final String DEFAULT_TS_TYPE = "any";

	static final Map<String, String> MYSQL_TO_JAVA = new HashMap<>();
	static final Map<String, String> MYSQL_TO_TS = new HashMap<>();

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

		MYSQL_TO_TS.put("tinyint", "number");
		MYSQL_TO_TS.put("smallint", "number");
		MYSQL_TO_TS.put("mediumint", "number");
		MYSQL_TO_TS.put("int", "number");
		MYSQL_TO_TS.put("integer", "number");
		MYSQL_TO_TS.put("bigint", "string");
		MYSQL_TO_TS.put("float", "string");
		MYSQL_TO_TS.put("double", "string");
		MYSQL_TO_TS.put("decimal", "string");
		MYSQL_TO_TS.put("bit", "boolean");

		MYSQL_TO_TS.put("char", "string");
		MYSQL_TO_TS.put("varchar", "string");
		MYSQL_TO_TS.put("tinytext", "string");
		MYSQL_TO_TS.put("text", "string");
		MYSQL_TO_TS.put("mediumtext", "string");
		MYSQL_TO_TS.put("longtext", "string");

		MYSQL_TO_TS.put("date", "string");
		MYSQL_TO_TS.put("datetime", "string");
		MYSQL_TO_TS.put("timestamp", "string");
	}

	public static String getJavaOfMysql(String mysqlType) {
		return MYSQL_TO_JAVA.getOrDefault(mysqlType, DEFAULT_TYPE);
	}

	public static String getTsOfMysql(String mysqlType) {
		return MYSQL_TO_TS.getOrDefault(mysqlType, DEFAULT_TS_TYPE);
	}

}
