package com.hccake.ballcat.codegen.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.hccake.ballcat.codegen.constant.DirectoryEntryTypeEnum;
import com.hccake.ballcat.codegen.datatype.MysqlDataTypeConverter;
import com.hccake.ballcat.codegen.model.bo.ColumnProperties;
import com.hccake.ballcat.codegen.model.bo.GenerateProperties;
import com.hccake.ballcat.codegen.model.bo.TemplateFile;
import com.hccake.ballcat.codegen.model.vo.ColumnInfo;
import com.hccake.ballcat.codegen.model.vo.TableInfo;
import com.hccake.ballcat.codegen.model.vo.TemplateEntryTree;
import java.io.File;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

/**
 * 代码生成器 工具类
 *
 * @author hccake
 * @date 2018-07-30
 */
@Slf4j
@UtilityClass
public class GenUtils {

	static {
		// 设置velocity资源加载器, 保留文件加载loader
		Properties prop = new Properties();
		prop.put("file.resource.loader.class", ClasspathResourceLoader.class.getName());
		Velocity.init(prop);
	}

	/**
	 * 生成代码
	 */
	@SneakyThrows
	public void generatorCode(String tablePrefix, Map<String, String> customProperties, TableInfo tableInfo,
			List<ColumnInfo> columnInfos, ZipOutputStream zip, List<TemplateFile> templateFiles) {

		// 根据表信息和字段信息获取对应的配置属性
		GenerateProperties generateProperties = getGenerateProperties(tableInfo, columnInfos, tablePrefix);

		// 转换generateProperties为map，模板数据
		Map<String, Object> map = BeanUtil.beanToMap(generateProperties);
		// 追加用户自定义属性
		map.putAll(customProperties);
		// 模板渲染
		VelocityContext context = new VelocityContext(map);
		for (TemplateFile templateFile : templateFiles) {
			StringWriter sw = new StringWriter();
			Velocity.evaluate(context, sw, tableInfo.getTableName() + templateFile.getFilePath(),
					templateFile.getContent());
			// 替换路径中的占位符
			String realFilePath = getRealFilePath(templateFile.getFilePath(), templateFile.getFileName(), map);
			// 添加到zip
			zip.putNextEntry(new ZipEntry(realFilePath));
			IoUtil.write(zip, StandardCharsets.UTF_8, false, sw.toString());
			IoUtil.close(sw);
			zip.closeEntry();
		}
	}

	/**
	 * 预览代码
	 */
	@SneakyThrows
	public void previewCode(String tablePrefix, Map<String, String> customProperties, TableInfo tableInfo,
			List<ColumnInfo> columnInfos, List<TemplateEntryTree> templateEntryTreeList) {

		// 根据表信息和字段信息获取对应的配置属性
		GenerateProperties generateProperties = getGenerateProperties(tableInfo, columnInfos, tablePrefix);
		// 转换generateProperties为map，模板数据
		Map<String, Object> map = BeanUtil.beanToMap(generateProperties);
		// 追加用户自定义属性
		map.putAll(customProperties);
		// 模板渲染
		VelocityContext context = new VelocityContext(map);
		for (TemplateEntryTree templateEntryTree : templateEntryTreeList) {
			// 文件名处理
			templateEntryTree.setFileName(StrUtil.format(templateEntryTree.getFileName(), map));
			if (DirectoryEntryTypeEnum.FILE.getType().equals(templateEntryTree.getType())
					&& StrUtil.isNotEmpty(templateEntryTree.getContent())) {
				// 模板内容填充
				StringWriter sw = new StringWriter();
				Velocity.evaluate(context, sw, tableInfo.getTableName() + templateEntryTree.getFileName(),
						templateEntryTree.getContent());
				templateEntryTree.setContent(sw.toString());
			}
		}
	}

	/**
	 * 根据表信息和字段信息获取对应的配置属性
	 * @param tableInfo 表信息
	 * @param columnInfos 字段信息
	 * @param tablePrefix 表前缀
	 * @return GenerateProperties
	 */
	private GenerateProperties getGenerateProperties(TableInfo tableInfo, List<ColumnInfo> columnInfos,
			String tablePrefix) {
		// 表信息
		GenerateProperties generateProperties = new GenerateProperties();
		// 表名
		String tableName = tableInfo.getTableName();
		generateProperties.setTableName(tableName);
		// 去除前缀的表名
		String noPrefixTableName = tableName;
		if (StringUtils.isNotBlank(tablePrefix) && tableName.startsWith(tablePrefix)) {
			noPrefixTableName = tableName.substring(tablePrefix.length());
		}

		// 表备注
		generateProperties.setComments(tableInfo.getTableComment());
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
		for (ColumnInfo columnInfo : columnInfos) {
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
			columnProperties.setAttrType(MysqlDataTypeConverter.getJavaOfMysql(columnProperties.getDataType()));
			// 列的 ts数据类型
			columnProperties.setTsAttrType(MysqlDataTypeConverter.getTsOfMysql(columnProperties.getDataType()));

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
	 * 获取真实的文件全路径
	 * @param filePathMaker 文件路径模板
	 * @param map 模板属性
	 * @return filePath 文件路径
	 */
	private String getRealFilePath(String filePathMaker, String fileNameMaker, Map<String, Object> map) {
		// 占位符替换
		String realFileName = StrUtil.format(fileNameMaker, map);
		String realFilePath = StrUtil.format(filePathMaker, map);
		// 用 . 标识文件夹合并，所以需要替换成 /
		realFilePath = realFilePath.replace(StrUtil.DOT, File.separator);
		// 防止多写了 /
		realFilePath = realFilePath.replace(File.separator + File.separator, File.separator);
		return realFilePath.endsWith(File.separator) ? realFilePath + realFileName
				: realFilePath + File.separator + realFileName;
	}

	/**
	 * 根据类名生成表别名
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
		return WordUtils.capitalizeFully(underlineStr, new char[] { '_' }).replace("_", "");
	}

}
