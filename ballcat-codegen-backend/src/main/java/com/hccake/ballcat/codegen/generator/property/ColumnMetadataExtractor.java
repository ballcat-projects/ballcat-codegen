package com.hccake.ballcat.codegen.generator.property;

import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.lang.Assert;
import com.hccake.ballcat.codegen.generator.type.TypeHubCreator;
import com.hccake.ballcat.codegen.generator.type.TypeHub;
import com.hccake.ballcat.codegen.model.bo.ColumnInfo;
import com.hccake.ballcat.codegen.model.bo.ColumnProperties;
import com.hccake.ballcat.codegen.model.bo.TableDetails;
import com.hccake.ballcat.codegen.model.entity.DbColumnType;
import com.hccake.ballcat.codegen.util.NamingUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

/**
 * 列属性构建器 负责构建数据库列相关的属性信息
 *
 * @author hccake
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ColumnMetadataExtractor {

	private final TypeHubCreator typeHubCreator;

	/**
	 * 构建列属性列表
	 */
	public List<ColumnProperties> buildColumnProperties(TableDetails tableDetails, String templateGroupKey) {
		// 创建类型转换中心
		TypeHub typeHub = typeHubCreator.create(tableDetails.getDbType(), templateGroupKey);

		return tableDetails.getColumnInfos()
			.stream()
			.map(columnInfo -> buildColumnProperty(columnInfo, typeHub))
			.collect(Collectors.toList());
	}

	/**
	 * 构建单个列属性
	 */
	private ColumnProperties buildColumnProperty(ColumnInfo columnInfo, TypeHub typeHub) {
		ColumnProperties columnProperties = new ColumnProperties();

		// 设置基本信息
		columnProperties.setColumnName(columnInfo.getColumnName());
		columnProperties.setDataType(columnInfo.getDataType());
		columnProperties.setComments(columnInfo.getColumnComment());
		columnProperties.setExtra(columnInfo.getExtra());
		columnProperties.setColumnType(columnInfo.getColumnType());

		// 设置Java属性名
		String capitalizedAttrName = NamingUtils.underlineToCamel(columnInfo.getColumnName());
		columnProperties.setCapitalizedAttrName(capitalizedAttrName);
		columnProperties.setAttrName(NamingUtils.uncapitalize(capitalizedAttrName));

		// 使用类型中心进行类型转换
		DbColumnType javaType = typeHub.toJavaType(columnInfo.getDataType());
		Assert.notNull(javaType, "未找到对应的数据类型配置，请添加对应配置：{}", columnInfo.getDataType());

		columnProperties.setAttrType(javaType.getType());
		columnProperties.setTsAttrType(typeHub.toTypeScriptType(javaType.getType()));

		return columnProperties;
	}

}