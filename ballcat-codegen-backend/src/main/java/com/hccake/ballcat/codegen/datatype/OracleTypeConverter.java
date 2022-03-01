package com.hccake.ballcat.codegen.datatype;

import com.baomidou.mybatisplus.annotation.DbType;

import java.util.HashMap;
import java.util.Map;

import static com.hccake.ballcat.codegen.datatype.DbColumnType.*;

/**
 * @author Hccake
 * @version 1.0
 * @date 2020/6/22 17:53
 */
public class OracleTypeConverter implements TypeConverter {

	static final Map<String, IColumnType> TYPE_MAP = new HashMap<>();

	static {
		TYPE_MAP.put("INTEGER", INTEGER);
		TYPE_MAP.put("BINARY_FLOAT", FLOAT);
		TYPE_MAP.put("BINARY_DOUBLE", DOUBLE);
		TYPE_MAP.put("FLOAT", FLOAT);
		TYPE_MAP.put("number", DOUBLE);
		TYPE_MAP.put("bit", BOOLEAN);

		TYPE_MAP.put("char", STRING);
		TYPE_MAP.put("varchar", STRING);
		TYPE_MAP.put("varchar2", STRING);
		TYPE_MAP.put("nvarchar", STRING);
		TYPE_MAP.put("nvarchar2", STRING);

		TYPE_MAP.put("date", LOCAL_DATE);
		TYPE_MAP.put("timestamp", LOCAL_DATE_TIME);

		// 注册
		TypeConverterManager.register(DbType.ORACLE, new OracleTypeConverter());
	}

	@Override
	public IColumnType convert(String dataType) {
		return TYPE_MAP.getOrDefault(dataType, STRING);
	}

}
