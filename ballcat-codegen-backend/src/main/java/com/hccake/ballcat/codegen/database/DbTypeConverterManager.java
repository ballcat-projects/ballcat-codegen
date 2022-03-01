package com.hccake.ballcat.codegen.database;

import com.baomidou.mybatisplus.annotation.DbType;

import java.util.EnumMap;
import java.util.Map;

/**
 * @author hccake
 */
public final class DbTypeConverterManager {

	private DbTypeConverterManager() {
	}

	private static final Map<DbType, DbTypeConverter> TYPE_CONVERTER_MAP = new EnumMap<>(DbType.class);

	public static void register(DbType dbType, DbTypeConverter dbTypeConverter) {
		TYPE_CONVERTER_MAP.put(dbType, dbTypeConverter);
	}

	public static DbTypeConverter getTypeConverter(DbType dbType) {
		return TYPE_CONVERTER_MAP.get(dbType);
	}

}
