package com.hccake.ballcat.codegen.database.sqlserver;

import com.baomidou.mybatisplus.annotation.DbType;
import com.hccake.ballcat.codegen.database.DbTypeConverter;
import com.hccake.ballcat.codegen.database.DbTypeConverterManager;
import com.hccake.ballcat.codegen.database.IColumnType;

import java.util.HashMap;
import java.util.Map;

import static com.hccake.ballcat.codegen.database.DbColumnType.*;

/**
 * @author hccake
 */
public class SqlServerDbTypeConverter implements DbTypeConverter {

	static final Map<String, IColumnType> TYPE_MAP = new HashMap<>();

	static {
		TYPE_MAP.put("bit", BOOLEAN);
		TYPE_MAP.put("tinyint", INTEGER);
		TYPE_MAP.put("smallint", INTEGER);
		TYPE_MAP.put("int", INTEGER);
		TYPE_MAP.put("bigint", LONG);
		TYPE_MAP.put("decimal", BIG_DECIMAL);
		TYPE_MAP.put("numeric", BIG_DECIMAL);
		TYPE_MAP.put("smallmoney", BIG_DECIMAL);
		TYPE_MAP.put("money", BIG_DECIMAL);
		TYPE_MAP.put("float", FLOAT);
		TYPE_MAP.put("real", DOUBLE);

		TYPE_MAP.put("char", STRING);
		TYPE_MAP.put("varchar", STRING);
		TYPE_MAP.put("text", STRING);
		TYPE_MAP.put("nchar", STRING);
		TYPE_MAP.put("nvarchar", STRING);
		TYPE_MAP.put("ntext", STRING);
		TYPE_MAP.put("binary", STRING);

		TYPE_MAP.put("date", LOCAL_DATE);
		TYPE_MAP.put("time", LOCAL_TIME);
		TYPE_MAP.put("datetime", LOCAL_DATE_TIME);
		TYPE_MAP.put("timestamp", LOCAL_DATE_TIME);

		// 注册
		DbTypeConverterManager.register(DbType.SQL_SERVER, new SqlServerDbTypeConverter());
	}

	@Override
	public IColumnType convert(String dataType) {
		return TYPE_MAP.getOrDefault(dataType, STRING);
	}

}
