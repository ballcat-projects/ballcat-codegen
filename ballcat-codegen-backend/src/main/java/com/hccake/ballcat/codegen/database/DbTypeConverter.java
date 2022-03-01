package com.hccake.ballcat.codegen.database;

/**
 * @author hccake
 */
public interface DbTypeConverter {

	/**
	 * 数据库的列类型转换为 java 类型
	 * @param dataType 类类型
	 * @return IColumnType
	 */
	IColumnType convert(String dataType);

}
