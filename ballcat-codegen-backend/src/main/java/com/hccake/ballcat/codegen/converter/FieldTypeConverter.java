package com.hccake.ballcat.codegen.converter;

import com.hccake.ballcat.codegen.model.dto.FieldTypeDTO;
import com.hccake.ballcat.codegen.model.entity.FieldType;
import com.hccake.ballcat.codegen.model.vo.FieldTypeVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * software：IntelliJ IDEA 2022.2 interface name: FieldTypeConverterMapper
 * description：DB和后端数据类型 model converter
 *
 * @author MoBaiJun 2022-07-01 14:27:10
 */
@Mapper
public interface FieldTypeConverter {

	FieldTypeConverter INSTANCE = Mappers.getMapper(FieldTypeConverter.class);

	/**
	 * entity to VO
	 * @param fieldType DB和后端数据类型 object
	 * @return FieldTypeVO DB和后端数据类型VO
	 */
	FieldTypeVO toVo(FieldType fieldType);

	/**
	 * DTO to entity
	 * @param fieldTypeDTO DB和后端数据类型DTO
	 * @return FieldType DB和后端数据类型 object
	 */
	FieldType toDto(FieldTypeDTO fieldTypeDTO);

}