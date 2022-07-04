package com.hccake.ballcat.codegen.typescript;

import java.util.HashMap;
import java.util.Map;

/**
 * TypeScript 的数据类型转换
 *
 * @author hccake
 */
public class TypeScriptTypeConverter {

	public static final String DEFAULT_TS_TYPE = "any";

	public static final Map<String, String> JAVA_TO_TS = new HashMap<>();

	public static String javaToTs(String javaType) {
		return JAVA_TO_TS.getOrDefault(javaType, DEFAULT_TS_TYPE);
	}

}
