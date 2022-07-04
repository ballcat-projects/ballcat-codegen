package com.hccake.ballcat.codegen.converter;

import com.hccake.ballcat.codegen.model.dto.TypeScriptTypeDTO;
import com.hccake.ballcat.codegen.model.entity.TypeScriptType;
import com.hccake.ballcat.codegen.model.vo.TypeScriptTypeVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * software：IntelliJ IDEA 2022.2 interface name: TypeScriptTypeConverterMapper
 * description：前端和后端数据类型管理 model converter
 *
 * @author MoBaiJun 2022-07-01 14:27:10
 */
@Mapper
public interface TypeScriptTypeConverter {

	TypeScriptTypeConverter INSTANCE = Mappers.getMapper(TypeScriptTypeConverter.class);

	/**
	 * entity to VO
	 * @param typeScriptType 前端和后端数据类型管理 object
	 * @return TypeScriptTypeVO 前端和后端数据类型管理VO
	 */
	@Mappings({})
	TypeScriptTypeVO toVo(TypeScriptType typeScriptType);

	/**
	 * DTO to entity
	 * @param typeScriptTypeDTO 前端和后端数据类型管理DTO
	 * @return TypeScriptType 前端和后端数据类型管理 object
	 */
	@Mappings({})
	TypeScriptType toDto(TypeScriptTypeDTO typeScriptTypeDTO);

}