package com.hccake.ballcat.codegen.generator.property;

import java.util.List;

import com.hccake.ballcat.codegen.model.bo.ColumnProperties;
import com.hccake.ballcat.codegen.model.bo.GenerateProperties;
import com.hccake.ballcat.codegen.model.bo.TableDetails;
import com.hccake.ballcat.codegen.util.NamingUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

/**
 * 表属性构建器 负责构建表相关的生成属性
 *
 * @author hccake
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TableMetadataExtractor {

	private final ColumnMetadataExtractor columnMetadataExtractor;

	/**
	 * 构建表相关的生成属性 只处理基于表的生成场景
	 */
	public GenerateProperties buildTableProperties(TableDetails tableDetails, String tablePrefix,
			String templateGroupKey) {
		if (tableDetails == null) {
			throw new IllegalArgumentException("TableDetails 不能为空，TablePropertyBuilder 只处理基于表的生成场景");
		}

		return buildTableBasedProperties(tableDetails, tablePrefix, templateGroupKey);
	}

	/**
	 * 基于表信息构建生成属性
	 */
	private GenerateProperties buildTableBasedProperties(TableDetails tableDetails, String tablePrefix,
			String templateGroupKey) {
		GenerateProperties generateProperties = new GenerateProperties();

		// 处理表基本信息
		buildBasicTableInfo(generateProperties, tableDetails, tablePrefix);

		// 处理列信息
		List<ColumnProperties> columnList = columnMetadataExtractor.buildColumnProperties(tableDetails,
				templateGroupKey);
		generateProperties.setColumns(columnList);

		// 设置主键
		setPrimaryKey(generateProperties, tableDetails);

		return generateProperties;
	}

	/**
	 * 构建表基本信息
	 */
	private void buildBasicTableInfo(GenerateProperties generateProperties, TableDetails tableDetails,
			String tablePrefix) {
		String tableName = tableDetails.getTableName();
		generateProperties.setTableName(tableName);

		// 处理表前缀
		String noPrefixTableName = NamingUtils.removeTablePrefix(tableName, tablePrefix);

		// 设置基本属性
		generateProperties.setComments(tableDetails.getTableComment());
		String className = NamingUtils.underlineToCamel(noPrefixTableName);
		generateProperties.setClassName(className);
		generateProperties.setTableAlias(NamingUtils.prodAlias(className));
		generateProperties.setClassname(NamingUtils.uncapitalize(className));
		generateProperties.setPath(noPrefixTableName.replace('_', '-'));
		generateProperties.setPathName(generateProperties.getClassname().toLowerCase());
	}

	/**
	 * 设置主键信息
	 */
	private void setPrimaryKey(GenerateProperties generateProperties, TableDetails tableDetails) {
		// 查找主键
		for (int i = 0; i < tableDetails.getColumnInfos().size(); i++) {
			if ("PRI".equalsIgnoreCase(tableDetails.getColumnInfos().get(i).getColumnKey())) {
				generateProperties.setPk(generateProperties.getColumns().get(i));
				return;
			}
		}

		// 如果没有主键，使用第一个字段作为主键
		if (!generateProperties.getColumns().isEmpty()) {
			generateProperties.setPk(generateProperties.getColumns().get(0));
		}
	}

}