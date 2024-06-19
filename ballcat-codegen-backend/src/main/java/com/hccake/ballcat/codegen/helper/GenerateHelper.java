package com.hccake.ballcat.codegen.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.annotation.DbType;
import com.hccake.ballcat.codegen.database.DbTypeConverterManager;
import com.hccake.ballcat.codegen.model.bo.ColumnInfo;
import com.hccake.ballcat.codegen.model.bo.ColumnProperties;
import com.hccake.ballcat.codegen.model.bo.GenerateProperties;
import com.hccake.ballcat.codegen.model.bo.TableDetails;
import com.hccake.ballcat.codegen.model.entity.DbColumnType;
import com.hccake.ballcat.codegen.model.entity.FieldType;
import com.hccake.ballcat.codegen.model.entity.TypeScriptType;
import com.hccake.ballcat.codegen.service.TypeScriptTypeService;
import com.hccake.ballcat.codegen.typescript.TypeScriptTypeConverter;
import com.hccake.ballcat.codegen.util.GenerateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

/**
 * 代码生成器 工具类
 *
 * @author hccake 2018-07-30
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GenerateHelper {

	private final DbTypeConverterManager dbTypeConverterManager;

	private final TypeScriptTypeService typeScriptTypeService;

	public Map<String, Object> getContext(TableDetails tableDetails, String tablePrefix, String templateGroupKey,
			Map<String, String> customProperties) {
		Map<String, Object> context;

		GenerateProperties generateProperties;
		if (tableDetails != null) {
			// 根据表信息和字段信息获取对应的配置属性
			generateProperties = genGeneratePropertiesFormTable(tableDetails, tablePrefix, templateGroupKey);
		}
		else {
			generateProperties = new GenerateProperties();
			generateProperties.setCurrentTime(DateUtil.now());
		}

		// 转换generateProperties为map，模板数据
		context = BeanUtil.beanToMap(generateProperties);
		// 追加用户自定义属性
		context.putAll(customProperties);
		return context;
	}

	/**
	 * 根据表信息和字段信息获取对应的配置属性
	 * @param tableDetails 表详情
	 * @param tablePrefix 表前缀
	 * @return GenerateProperties
	 */
	private GenerateProperties genGeneratePropertiesFormTable(TableDetails tableDetails, String tablePrefix,
			String templateGroupKey) {
		// 表信息
		GenerateProperties generateProperties = new GenerateProperties();
		// 表名
		String tableName = tableDetails.getTableName();
		generateProperties.setTableName(tableName);
		// 去除前缀的表名
		String noPrefixTableName = tableName;
		if (StringUtils.isNotBlank(tablePrefix) && tableName.startsWith(tablePrefix)) {
			noPrefixTableName = tableName.substring(tablePrefix.length());
		}

		// 表备注
		generateProperties.setComments(tableDetails.getTableComment());
		// 大驼峰类名
		String className = GenerateUtils.underlineToCamel(noPrefixTableName);
		generateProperties.setClassName(className);
		// 表别名
		generateProperties.setTableAlias(GenerateUtils.prodAlias(className));
		// 小驼峰类名
		String classname = StringUtils.uncapitalize(className);
		generateProperties.setClassname(classname);
		// 请求路径
		generateProperties.setPath(noPrefixTableName.replace('_', '-'));
		generateProperties.setPathName(classname.toLowerCase());

		// 列信息
		List<ColumnProperties> columnList = new ArrayList<>();

		// 类型转换器
		DbType dbType = tableDetails.getDbType();

		// 类型集合
		List<FieldType> typeList = dbTypeConverterManager.getDbTypeList(dbType, templateGroupKey);
		Assert.notNull(typeList, "未找到对应的数据库类型转换器集合：{}", dbType);

		for (ColumnInfo columnInfo : tableDetails.getColumnInfos()) {
			String columnName = columnInfo.getColumnName();
			ColumnProperties columnProperties = new ColumnProperties();
			columnProperties.setColumnName(columnName);
			columnProperties.setDataType(columnInfo.getDataType());
			columnProperties.setComments(columnInfo.getColumnComment());
			columnProperties.setExtra(columnInfo.getExtra());
			columnProperties.setColumnType(columnInfo.getColumnType());

			// 列名转换成Java属性名
			String capitalizedAttrName = GenerateUtils.underlineToCamel(columnName);
			columnProperties.setCapitalizedAttrName(capitalizedAttrName);
			columnProperties.setAttrName(StringUtils.uncapitalize(capitalizedAttrName));

			// 列的数据类型，转换成Java类型
			DbColumnType columnType = dbTypeConverterManager.getTypeConverter(typeList, columnProperties.getDataType());
			String columnJavaType = columnType.getType();
			columnProperties.setAttrType(columnJavaType);

			// 列的 ts数据类型
			List<TypeScriptType> list = typeScriptTypeService.list();
			columnProperties.setTsAttrType(new TypeScriptTypeConverter(list).javaToTs(columnJavaType));

			// 是否主键
			if ("PRI".equalsIgnoreCase(columnInfo.getColumnKey()) && generateProperties.getPk() == null) {
				generateProperties.setPk(columnProperties);
			}

			columnList.add(columnProperties);
		}
		generateProperties.setColumns(columnList);

		// 没主键，则第一个字段为主键
		if (generateProperties.getPk() == null) {
			generateProperties.setPk(generateProperties.getColumns().get(0));
		}
		// 当前时间
		generateProperties.setCurrentTime(DateUtil.now());
		return generateProperties;
	}

}
