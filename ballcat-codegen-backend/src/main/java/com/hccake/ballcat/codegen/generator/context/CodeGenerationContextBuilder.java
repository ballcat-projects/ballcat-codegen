package com.hccake.ballcat.codegen.generator.context;

import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import com.hccake.ballcat.codegen.model.bo.TableDetails;
import com.hccake.ballcat.codegen.model.bo.TemplateFile;
import com.hccake.ballcat.codegen.model.dto.GeneratorOptionDTO;
import com.hccake.ballcat.codegen.model.entity.TemplateEntry;
import com.hccake.ballcat.codegen.model.entity.TemplateProperty;
import com.hccake.ballcat.codegen.service.TableInfoQuery;
import com.hccake.ballcat.codegen.service.TemplateEntryService;
import com.hccake.ballcat.codegen.service.TemplatePropertyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 代码生成上下文构建器 负责将DTO转换为生成上下文
 *
 * @author hccake
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CodeGenerationContextBuilder {

	private final TemplateEntryService templateEntryService;

	private final TemplatePropertyService templatePropertyService;

	private final TableInfoQuery tableInfoQuery;

	/**
	 * 从GeneratorOptionDTO构建代码生成上下文
	 * @param optionDTO 生成选项DTO
	 * @return 代码生成上下文
	 */
	public CodeGenerationContext buildFromDTO(GeneratorOptionDTO optionDTO) {
		// 获取模板文件
		List<TemplateEntry> templateEntries = templateEntryService.listByIds(optionDTO.getTemplateEntryIds());
		List<TemplateFile> templateFiles = templateEntryService.convertToTemplateFile(templateEntries);

		// 获取计算属性
		List<TemplateProperty> computedProperties = templatePropertyService
			.listComputedProperties(optionDTO.getTemplateGroupKey());

		// 获取表详情
		List<TableDetails> tableDetailsList = buildTableDetailsList(optionDTO.getTableNames());

		// 确定生成模式
		CodeGenerationContext.GenerationMode mode = CollUtil.isEmpty(tableDetailsList)
				? CodeGenerationContext.GenerationMode.TEMPLATE_ONLY : CodeGenerationContext.GenerationMode.TABLE_BASED;

		return CodeGenerationContext.builder()
			.templateGroupKey(optionDTO.getTemplateGroupKey())
			.templateFiles(templateFiles)
			.computedProperties(computedProperties)
			.customProperties(optionDTO.getGenProperties())
			.tableDetailsList(tableDetailsList)
			.tablePrefix(optionDTO.getTablePrefix())
			.generationMode(mode)
			.build();
	}

	/**
	 * 构建表详情列表
	 * @param tableNames 表名数组
	 * @return 表详情列表
	 */
	private List<TableDetails> buildTableDetailsList(String[] tableNames) {
		if (ArrayUtil.isEmpty(tableNames)) {
			return new ArrayList<>();
		}

		List<TableDetails> tableDetailsList = new ArrayList<>();
		for (String tableName : tableNames) {
			try {
				TableDetails tableDetails = tableInfoQuery.queryTableDetails(tableName);
				tableDetailsList.add(tableDetails);
			}
			catch (Exception e) {
				log.error("查询表详情失败，表名: {}", tableName, e);
				throw new RuntimeException("查询表详情失败: " + tableName, e);
			}
		}
		return tableDetailsList;
	}

}