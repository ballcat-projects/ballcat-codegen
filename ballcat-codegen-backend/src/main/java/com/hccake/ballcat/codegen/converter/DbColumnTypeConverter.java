package com.hccake.ballcat.codegen.converter;

import com.hccake.ballcat.codegen.model.entity.DbColumnType;
import com.hccake.ballcat.codegen.model.entity.FieldType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * software：IntelliJ IDEA 2022.1 interface name: DbColumnTypeConverter class description：
 *
 * @author MoBaiJun 2022/7/4 14:20
 */
@Mapper
public interface DbColumnTypeConverter {

	DbColumnTypeConverter INSTANCE = Mappers.getMapper(DbColumnTypeConverter.class);

	/**
	 * 数据库字段类型转转 DbColumnType
	 * @param ft 数据库字段类型
	 * @return DbColumnType
	 */
	@Mappings({ @Mapping(target = "type", source = "ft.columnValue"),
			@Mapping(target = "pkg", source = "ft.packageName") })
	DbColumnType toModel(FieldType ft);

}
