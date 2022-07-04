package com.hccake.ballcat.codegen.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.DbType;
import com.hccake.ballcat.codegen.database.DbTypeConverterManager;
import com.hccake.ballcat.codegen.model.bo.ColumnInfo;
import com.hccake.ballcat.codegen.model.bo.ColumnProperties;
import com.hccake.ballcat.codegen.model.bo.GenerateProperties;
import com.hccake.ballcat.codegen.model.bo.TableDetails;
import com.hccake.ballcat.codegen.model.entity.DbColumnType;
import com.hccake.ballcat.codegen.model.entity.FieldType;
import com.hccake.ballcat.codegen.typescript.TypeScriptTypeConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 代码生成器 工具类
 *
 * @author hccake
 * @date 2018-07-30
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GenUtils {

	private final DbTypeConverterManager dbTypeConverterManager;

	public Map<String, Object> getContext(TableDetails tableDetails, String tablePrefix,
										  Integer templateGroupId, Map<String, String> customProperties) {
		// 根据表信息和字段信息获取对应的配置属性
		GenerateProperties generateProperties = getGenerateProperties(tableDetails, tablePrefix, templateGroupId);
		// 转换generateProperties为map，模板数据
		Map<String, Object> context = BeanUtil.beanToMap(generateProperties);
		// 追加用户自定义属性
		context.putAll(customProperties);
		return context;
	}

	/**
	 * 根据表信息和字段信息获取对应的配置属性
	 *
	 * @param tableDetails 表详情
	 * @param tablePrefix  表前缀
	 * @return GenerateProperties
	 */
	private GenerateProperties getGenerateProperties(TableDetails tableDetails, String tablePrefix, Integer templateGroupId) {
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
		String className = underlineToCamel(noPrefixTableName);
		generateProperties.setClassName(className);
		// 表别名
		generateProperties.setTableAlias(prodAlias(className));
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
		List<FieldType> typeList = dbTypeConverterManager.getDbTypeList(dbType, templateGroupId);
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
			String capitalizedAttrName = underlineToCamel(columnName);
			columnProperties.setCapitalizedAttrName(capitalizedAttrName);
			columnProperties.setAttrName(StringUtils.uncapitalize(capitalizedAttrName));

			// 列的数据类型，转换成Java类型
			DbColumnType columnType = dbTypeConverterManager.getTypeConverter(typeList, columnProperties.getDataType());
			String columnJavaType = columnType.getType();
			columnProperties.setAttrType(columnJavaType);
			// 列的 ts数据类型
			columnProperties.setTsAttrType(TypeScriptTypeConverter.javaToTs(columnJavaType));

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

	/**
	 * 路径拼接
	 *
	 * @param parentPath 父级路径
	 * @param subPath    子路径
	 * @return 完整路径
	 */
	public String concatFilePath(String parentPath, String subPath) {
		if (StrUtil.isEmpty(parentPath)) {
			return subPath;
		}
		return parentPath.endsWith(File.separator) ? parentPath + subPath : parentPath + File.separator + subPath;
	}

	/**
	 * 获取真实的文件全路径
	 *
	 * @param filePathMaker 文件路径模板
	 * @param map           模板属性
	 * @return filePath 文件路径
	 */
	public String evaluateRealPath(String filePathMaker, Map<String, Object> map) {
		// 占位符替换
		String realFilePath = StrUtil.format(filePathMaker, map);
		if (StrUtil.isEmpty(realFilePath)) {
			return realFilePath;
		}
		// 用 . 标识文件夹合并，所以需要替换成 /
		realFilePath = realFilePath.replace(StrUtil.DOT, File.separator);
		// 防止多写了 /
		realFilePath = realFilePath.replace(File.separator + File.separator, File.separator);

		return realFilePath;
	}

	/**
	 * 根据类名生成表别名
	 *
	 * @param className 类名
	 * @return 表别名
	 */
	private String prodAlias(String className) {
		StringBuilder sb = new StringBuilder();
		for (char c : className.toCharArray()) {
			if (c >= 'A' && c <= 'Z') {
				sb.append(Character.toLowerCase(c));
			}
		}
		return sb.toString();
	}

	/**
	 * 列名转换成Java属性名
	 */
	public String underlineToCamel(String underlineStr) {
		return WordUtils.capitalizeFully(underlineStr, new char[]{'_'}).replace("_", "");
	}
}
