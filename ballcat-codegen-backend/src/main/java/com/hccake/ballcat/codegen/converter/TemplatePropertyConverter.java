package com.hccake.ballcat.codegen.converter;

import com.hccake.ballcat.codegen.model.dto.TemplatePropertyDTO;
import com.hccake.ballcat.codegen.model.entity.TemplateProperty;
import com.hccake.ballcat.codegen.model.vo.TemplatePropertyPageVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.lang.annotation.Target;

/**
 * @author Hccake 2020/6/26
 * @version 1.0
 */
@Mapper
public interface TemplatePropertyConverter {

	TemplatePropertyConverter INSTANCE = Mappers.getMapper(TemplatePropertyConverter.class);

	/**
	 * 模板属性配置 PO 转换为 PageVO
	 * @param templateProperty 模板属性配置实体
	 * @return TemplatePropertyPageVO 模板属性配置分页VO
	 */
	TemplatePropertyPageVO poToPageVo(TemplateProperty templateProperty);

	/**
	 * 模板属性配置 PO 转换为 DTO
	 * @param templateProperty 模板属性配置实体
	 * @return TemplatePropertyDTO 模板属性传输对象
	 */
	TemplatePropertyDTO poToDto(TemplateProperty templateProperty);

	/**
	 * 模板属性配置 DTO 转换为 PO
	 * @param groupKey 模板组标识
	 * @param templateProperty 模板属性配置实体
	 * @return TemplatePropertyDTO 模板属性传输对象
	 */
	TemplateProperty dtoToPo(String groupKey, TemplatePropertyDTO templateProperty);

}
