package com.hccake.ballcat.codegen.converter;

import cn.hutool.core.util.StrUtil;
import com.hccake.ballcat.codegen.constant.TemplateEntryTypeEnum;
import com.hccake.ballcat.codegen.model.bo.FileEntry;
import com.hccake.ballcat.codegen.model.bo.TemplateEntryFileTree;
import com.hccake.ballcat.codegen.model.dto.TemplateEntryCreateDTO;
import com.hccake.ballcat.codegen.model.dto.TemplateEntryUpdateDTO;
import com.hccake.ballcat.codegen.model.entity.TemplateEntry;
import com.hccake.ballcat.codegen.model.entity.TemplateGroup;
import com.hccake.ballcat.codegen.model.vo.GeneratePreviewFileVO;
import com.hccake.ballcat.codegen.model.vo.TemplateEntryTree;
import com.hccake.ballcat.codegen.model.vo.TemplateEntryVO;
import com.hccake.ballcat.codegen.model.vo.TemplateGroupPageVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.nio.charset.StandardCharsets;

/**
 * @author Hccake
 * @version 1.0
 * @date 2020/6/19 20:01
 */
@Mapper
public interface TemplateModelConverter {

	TemplateModelConverter INSTANCE = Mappers.getMapper(TemplateModelConverter.class);

	/**
	 * 文件内容转模板文件内容，byte[] to string
	 * @param templateEntry 模板目录下
	 * @return 字符串
	 */
	default String mapTemplateContent(TemplateEntry templateEntry) {
		if (TemplateEntryTypeEnum.TEMPLATE_FILE.getType().equals(templateEntry.getType())) {
			return StrUtil.str(templateEntry.getFileContent(), StandardCharsets.UTF_8);
		}
		return null;
	}

	/**
	 * 文件内容转模板文件内容，byte[] to string
	 * @param fileContent 模板文件内容
	 * @return 字符串
	 */
	default String mapTemplateContent(byte[] fileContent) {
		return StrUtil.str(fileContent, StandardCharsets.UTF_8);
	}

	/**
	 * 数值类型转枚举
	 * @param type 文件类型
	 * @return TemplateEntryTypeEnum
	 */
	default TemplateEntryTypeEnum mapType(Integer type) {
		switch (type) {
			case 1:
				return TemplateEntryTypeEnum.FOLDER;
			case 2:
				return TemplateEntryTypeEnum.TEMPLATE_FILE;
			case 3:
				return TemplateEntryTypeEnum.BINARY_FILE;
			default:
				throw new RuntimeException("模板文件的类型转换异常，未知的类型：" + type);
		}
	}

	/**
	 * 转枚举数值类型
	 * @param type 文件类型
	 * @return Integer
	 */
	default Integer mapType(TemplateEntryTypeEnum type) {
		return type.getType();
	}

	/**
	 * 模板组 PO 转换为 PageVO
	 * @param templateGroup 模板组实体
	 * @return TemplateGroupPageVO 模板组分页VO
	 */
	TemplateGroupPageVO groupPoToPageVo(TemplateGroup templateGroup);

	/**
	 * 转换 TemplateEntry 为 TemplateEntryVO
	 * @param templateEntry templateEntry
	 * @return VO
	 */
	@Mapping(target = "templateContent", source = "templateEntry")
	TemplateEntryVO entryPoToVo(TemplateEntry templateEntry);

	/**
	 * 转换 TemplateEntryCreateDTO to TemplateEntry
	 * @param entryCreateDTO entryCreateDTO
	 * @return TemplateDirectoryEntry 持久对象
	 */
	TemplateEntry entryCreateDtoToPo(TemplateEntryCreateDTO entryCreateDTO);

	/**
	 * 转换 TemplateEntryUpdateDTO to TemplateEntry
	 * @param entryUpdateDTO entryUpdateDTO
	 * @return TemplateEntry 持久对象
	 */
	TemplateEntry entryUpdateDtoToPo(TemplateEntryUpdateDTO entryUpdateDTO);

	/**
	 * 转换为目录树
	 * @param templateEntry templateDirectoryEntry
	 * @return TemplateDirectoryTree
	 */
	@Mapping(target = "type", source = "type")
	TemplateEntryTree entryPoToTree(TemplateEntry templateEntry);

	/**
	 * 树转换为 po
	 * @param templateEntryFileTree TemplateEntryTree
	 * @return TemplateEntry
	 */
	TemplateEntry entryFileTreeToPo(TemplateEntryFileTree templateEntryFileTree);

	/**
	 * 文件项转预览文件VO
	 * @param fileEntry 文件项
	 * @return GeneratePreviewFileVO
	 */
	@Mapping(target = "templateContent", source = "fileContent")

	@Mapping(target = "type", source = "type")
	GeneratePreviewFileVO fileEntryToPreviewVo(FileEntry fileEntry);

}
