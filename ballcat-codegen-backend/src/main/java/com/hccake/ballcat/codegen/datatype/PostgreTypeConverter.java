package com.hccake.ballcat.codegen.datatype;

import com.baomidou.mybatisplus.annotation.DbType;

import java.util.HashMap;
import java.util.Map;

import static com.hccake.ballcat.codegen.datatype.DbColumnType.*;

/**
 * @author hccake
 */
public class PostgreTypeConverter implements TypeConverter {

	static final Map<String, IColumnType> TYPE_MAP = new HashMap<>();

	static {
		TYPE_MAP.put("smallint", INTEGER);
		TYPE_MAP.put("integer", INTEGER);
		TYPE_MAP.put("bigint", LONG);
		TYPE_MAP.put("decimal", BIG_DECIMAL);
		TYPE_MAP.put("numeric", BIG_DECIMAL);

		TYPE_MAP.put("mediumint", INTEGER);
		TYPE_MAP.put("int", INTEGER);
		TYPE_MAP.put("float", FLOAT);
		TYPE_MAP.put("double", DOUBLE);
		TYPE_MAP.put("bit", BOOLEAN);
		TYPE_MAP.put("boolean", BOOLEAN);

		TYPE_MAP.put("char", STRING);
		TYPE_MAP.put("varchar", STRING);
		TYPE_MAP.put("character varying", STRING);
		TYPE_MAP.put("character", STRING);
		TYPE_MAP.put("text", STRING);
		TYPE_MAP.put("json", STRING);

		TYPE_MAP.put("date", LOCAL_DATE_TIME);
		TYPE_MAP.put("time", LOCAL_DATE_TIME);
		TYPE_MAP.put("timestamp", LOCAL_DATE_TIME);

		// 注册
		TypeConverterManager.register(DbType.POSTGRE_SQL, new PostgreTypeConverter());
	}

	@Override
	public IColumnType convert(String mysqlType) {
		return TYPE_MAP.getOrDefault(mysqlType, STRING);
	}

}
