package com.hccake.ballcat.codegen.generator.type;

import com.baomidou.mybatisplus.annotation.DbType;
import com.hccake.ballcat.codegen.constant.TemplateEntryConstants;
import com.hccake.ballcat.codegen.converter.DbColumnTypeConverter;
import com.hccake.ballcat.codegen.model.entity.DbColumnType;
import com.hccake.ballcat.codegen.model.entity.FieldType;
import com.hccake.ballcat.codegen.model.entity.TypeScriptType;
import com.hccake.ballcat.codegen.service.FieldTypeService;
import com.hccake.ballcat.codegen.service.TypeScriptTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 类型创建器 负责创建和初始化类型中心
 *
 * @author hccake
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TypeHubCreator {

	private final FieldTypeService fieldTypeService;

	private final TypeScriptTypeService tsTypeService;

	/**
	 * 创建类型中心
	 * @param dbType 数据库类型
	 * @param templateGroupKey 模板组键
	 * @return 类型中心实例
	 */
	public TypeHub create(DbType dbType, String templateGroupKey) {
		// 构建数据库类型映射
		Map<String, DbColumnType> dbTypeMapping = buildDbTypeMapping(dbType, templateGroupKey);

		// 构建TypeScript类型映射
		Map<String, String> tsTypeMapping = buildTsTypeMapping();

		log.debug("创建类型中心 - 数据库类型: {}, 模板组: {}, DB类型映射数量: {}, TS类型映射数量: {}", dbType, templateGroupKey,
				dbTypeMapping.size(), tsTypeMapping.size());

		return TypeHub.builder()
			.dbType(dbType)
			.templateGroupKey(templateGroupKey)
			.dbTypeMapping(dbTypeMapping)
			.tsTypeMapping(tsTypeMapping)
			.build();
	}

	/**
	 * 构建数据库类型映射
	 */
	private Map<String, DbColumnType> buildDbTypeMapping(DbType dbType, String templateGroupKey) {
		List<FieldType> fieldTypes = fieldTypeService.selectFieldTypesWithDefault(dbType, templateGroupKey);

		// 处理模板组优先级：指定模板组的配置覆盖默认配置
		Map<String, FieldType> fieldTypeMap = new HashMap<>(fieldTypes.size());
		for (FieldType fieldType : fieldTypes) {
			String columnKey = fieldType.getColumnKey();
			String groupKey = fieldType.getGroupKey();

			// 如果不存在直接填充，或者存在但是指定模板组的配置，就进行覆盖
			if (!fieldTypeMap.containsKey(columnKey)
					|| !Objects.equals(TemplateEntryConstants.TREE_ROOT_ID, groupKey)) {
				fieldTypeMap.put(columnKey, fieldType);
			}
		}

		// 转换为DbColumnType映射
		return fieldTypeMap.values()
			.stream()
			.collect(Collectors.toMap(type -> type.getColumnKey().toLowerCase(),
					DbColumnTypeConverter.INSTANCE::toModel, (existing, replacement) -> existing));
	}

	/**
	 * 构建TypeScript类型映射
	 */
	private Map<String, String> buildTsTypeMapping() {
		List<TypeScriptType> types = tsTypeService.list();
		return types.stream()
			.collect(Collectors.toMap(TypeScriptType::getCodeKey, TypeScriptType::getCodeValue,
					(existing, replacement) -> existing));
	}

}