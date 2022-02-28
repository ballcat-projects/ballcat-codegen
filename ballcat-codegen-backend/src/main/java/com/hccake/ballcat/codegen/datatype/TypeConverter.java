package com.hccake.ballcat.codegen.datatype;

/**
 * @author hccake
 */
public interface TypeConverter {

	/**
	 * 数据库的列类型转换为 java 类型
	 * @param dataType 类类型
	 * @return IColumnType
	 */
	IColumnType convert(String dataType);

}
