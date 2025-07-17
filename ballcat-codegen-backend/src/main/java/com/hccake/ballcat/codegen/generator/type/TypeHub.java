package com.hccake.ballcat.codegen.generator.type;

import java.util.Map;

import com.baomidou.mybatisplus.annotation.DbType;
import com.hccake.ballcat.codegen.model.entity.DbColumnType;
import lombok.Builder;
import lombok.Data;

/**
 * 类型中心 集中管理和提供各种类型转换功能
 *
 * @author hccake
 */
@Data
@Builder
public class TypeHub {

	private DbType dbType;

	private String templateGroupKey;

	private Map<String, DbColumnType> dbTypeMapping;

	private Map<String, String> tsTypeMapping;

	/**
	 * 获取Java类型
	 * @param dbColumnType 数据库列类型
	 * @return Java类型信息
	 */
	public DbColumnType toJavaType(String dbColumnType) {
		return dbTypeMapping.getOrDefault(dbColumnType.toLowerCase(),
				new DbColumnType().setPkg("java.lang.String").setType("String"));
	}

	/**
	 * 获取TypeScript类型
	 * @param javaType Java类型
	 * @return TypeScript类型
	 */
	public String toTypeScriptType(String javaType) {
		return tsTypeMapping.getOrDefault(javaType, "any");
	}

	/**
	 * 一步获取TypeScript类型（从数据库列类型）
	 * @param dbColumnType 数据库列类型
	 * @return TypeScript类型
	 */
	public String dbToTypeScriptType(String dbColumnType) {
		DbColumnType javaType = toJavaType(dbColumnType);
		return toTypeScriptType(javaType.getType());
	}

}