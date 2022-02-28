package com.hccake.ballcat.codegen.datatype;

import com.baomidou.mybatisplus.annotation.DbType;

import java.util.EnumMap;
import java.util.Map;

/**
 * @author hccake
 */
public final class TypeConverterManager {

	private TypeConverterManager() {
	}

	private static final Map<DbType, TypeConverter> TYPE_CONVERTER_MAP = new EnumMap<>(DbType.class);

	public static void register(DbType dbType, TypeConverter typeConverter) {
		TYPE_CONVERTER_MAP.put(dbType, typeConverter);
	}

	public static TypeConverter getTypeConverter(DbType dbType) {
		return TYPE_CONVERTER_MAP.get(dbType);
	}

}
