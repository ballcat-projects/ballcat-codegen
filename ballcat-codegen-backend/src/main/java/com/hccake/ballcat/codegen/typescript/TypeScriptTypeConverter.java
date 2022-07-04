package com.hccake.ballcat.codegen.typescript;

import cn.hutool.core.collection.CollUtil;
import com.hccake.ballcat.codegen.model.entity.TypeScriptType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TypeScript 的数据类型转换
 *
 * @author hccake
 */
public class TypeScriptTypeConverter {

	public static final String DEFAULT_TS_TYPE = "any";

	public final Map<String, String> typeMapping = new HashMap<>();

	public TypeScriptTypeConverter(List<TypeScriptType> typeScriptTypes) {
		if (CollUtil.isNotEmpty(typeScriptTypes)) {
			for (TypeScriptType typeScriptType : typeScriptTypes) {
				typeMapping.put(typeScriptType.getCodeKey(), typeScriptType.getCodeValue());
			}
		}
	}

	public String javaToTs(String javaType) {
		return typeMapping.getOrDefault(javaType, DEFAULT_TS_TYPE);
	}

}
