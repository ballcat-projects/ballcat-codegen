package com.hccake.ballcat.codegen.datatype;

import java.util.HashMap;
import java.util.Map;

/**
 * TypeScript 的数据类型转换
 * 
 * @author hccake
 */
public class TypeScriptTypeConverter {

	public static final String DEFAULT_TS_TYPE = "any";

	static final Map<String, String> JAVA_TO_TS = new HashMap<>();

	static {
		JAVA_TO_TS.put("Short", "number");
		JAVA_TO_TS.put("Integer", "number");
		JAVA_TO_TS.put("Long", "number");

		JAVA_TO_TS.put("Float", "string");
		JAVA_TO_TS.put("Double", "string");
		JAVA_TO_TS.put("BigDecimal", "string");

		JAVA_TO_TS.put("Boolean", "boolean");

		JAVA_TO_TS.put("Char", "string");
		JAVA_TO_TS.put("String", "string");

		JAVA_TO_TS.put("LocalDate", "string");
		JAVA_TO_TS.put("LocalDateTime", "string");
	}

	public static String javaToTs(String javaType) {
		return JAVA_TO_TS.getOrDefault(javaType, DEFAULT_TS_TYPE);
	}

}
