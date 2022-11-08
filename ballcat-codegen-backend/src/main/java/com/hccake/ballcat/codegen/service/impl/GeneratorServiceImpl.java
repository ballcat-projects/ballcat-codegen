package com.hccake.ballcat.codegen.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import com.hccake.ballcat.codegen.constant.TemplateEntryTypeEnum;
import com.hccake.ballcat.codegen.engine.TemplateEngineDelegator;
import com.hccake.ballcat.codegen.engine.TemplateEngineTypeEnum;
import com.hccake.ballcat.codegen.exception.TemplateRenderException;
import com.hccake.ballcat.codegen.helper.GenerateHelper;
import com.hccake.ballcat.codegen.model.bo.FileEntry;
import com.hccake.ballcat.codegen.model.bo.TableDetails;
import com.hccake.ballcat.codegen.model.bo.TemplateFile;
import com.hccake.ballcat.codegen.model.dto.GeneratorOptionDTO;
import com.hccake.ballcat.codegen.model.entity.TemplateEntry;
import com.hccake.ballcat.codegen.service.GeneratorService;
import com.hccake.ballcat.codegen.service.TableInfoQuery;
import com.hccake.ballcat.codegen.service.TemplateEntryService;
import com.hccake.ballcat.codegen.util.GenerateUtils;
import com.hccake.ballcat.common.core.exception.BusinessException;
import com.hccake.ballcat.common.model.result.SystemResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Hccake
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GeneratorServiceImpl implements GeneratorService {

	private final TableInfoQuery tableInfoQuery;

	private final TemplateEntryService templateEntryService;

	private final TemplateEngineDelegator templateEngineDelegator;

	private final GenerateHelper generateHelper;

	/**
	 * 生成代码
	 * @param generatorOptionDTO 代码生成的一些配置信息
	 * @return 已生成的代码数据
	 */
	@Override
	public byte[] generatorCode(GeneratorOptionDTO generatorOptionDTO) throws IOException {
		// 获取生成后的文件项 map
		Map<String, FileEntry> map = getStringFileEntryMap(generatorOptionDTO);

		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				ZipOutputStream zip = new ZipOutputStream(outputStream)) {
			// 循环写入数据
			for (Map.Entry<String, FileEntry> entry : map.entrySet()) {
				FileEntry fileEntry = entry.getValue();
				// 只处理文件
				if (!TemplateEntryTypeEnum.FOLDER.equals(fileEntry.getType())) {
					// 添加到zip
					String filePath = entry.getKey();
					zip.putNextEntry(new ZipEntry(filePath));
					zip.write(fileEntry.getFileContent());
					zip.closeEntry();
				}
			}
			// 手动结束 zip，防止文件末端未被写入
			zip.finish();
			return outputStream.toByteArray();
		}
	}

	@Override
	public List<FileEntry> previewCode(GeneratorOptionDTO generateOptionDTO) {
		// 获取生成后的文件项 map
		Map<String, FileEntry> map = getStringFileEntryMap(generateOptionDTO);
		// 忽略大小写的排序
		return CollUtil.sort(map.values(), Comparator.comparing(FileEntry::getFilename, String.CASE_INSENSITIVE_ORDER));
	}

	/**
	 * 获得生成后的 代码地址：代码文件 的 map
	 * @param generateOptionDTO 生成参数
	 * @return Map<String, FileEntry>
	 */
	private Map<String, FileEntry> getStringFileEntryMap(GeneratorOptionDTO generateOptionDTO) {
		// 获取模板文件信息
		List<TemplateEntry> templateEntryList = templateEntryService.listByIds(generateOptionDTO.getTemplateEntryIds());
		List<TemplateFile> templateFiles = templateEntryService.convertToTemplateFile(templateEntryList);

		return getStringFileEntryMap(generateOptionDTO, templateFiles);
	}

	/**
	 * 获得生成后的 代码地址：代码文件 的 map
	 * @param generateOptionDTO 生成参数
	 * @param templateFiles 模板文件
	 * @return Map<String, FileEntry>
	 */
	private Map<String, FileEntry> getStringFileEntryMap(GeneratorOptionDTO generateOptionDTO,
			List<TemplateFile> templateFiles) {

		String[] tableNames = generateOptionDTO.getTableNames();
		Map<String, String> genProperties = generateOptionDTO.getGenProperties();
		String templateGroupKey = generateOptionDTO.getTemplateGroupKey();

		// 没有表数据则直接进行代码生成
		if (tableNames == null || tableNames.length == 0) {
			return generatorCode(genProperties, templateGroupKey, templateFiles);
		}

		// 有表数据，则根据表数据进行循环生成
		Map<String, FileEntry> map = new HashMap<>(templateFiles.size());
		for (String tableName : tableNames) {
			// 查询表详情
			TableDetails tableDetails = tableInfoQuery.queryTableDetails(tableName);
			// 生成代码
			Map<String, FileEntry> fileEntryMap = generatorCode(tableDetails, generateOptionDTO.getTablePrefix(),
					genProperties, templateGroupKey, templateFiles);
			map.putAll(fileEntryMap);
		}
		return map;
	}

	private Map<String, FileEntry> generatorCode(Map<String, String> genProperties, String templateGroupKey,
			List<TemplateFile> templateFiles) {
		return generatorCode(null, null, genProperties, templateGroupKey, templateFiles);
	}

	/**
	 * 代码生成
	 * @return Map<String, FileEntry>
	 */
	public Map<String, FileEntry> generatorCode(TableDetails tableDetails, String tablePrefix,
			Map<String, String> customProperties, String groupKey, List<TemplateFile> templateFiles) {

		Map<String, FileEntry> map = new HashMap<>(templateFiles.size());

		// 模板渲染
		Map<String, Object> context = generateHelper.getContext(tableDetails, tablePrefix, groupKey, customProperties);

		for (TemplateFile templateFile : templateFiles) {
			FileEntry fileEntry = new FileEntry();
			fileEntry.setId(templateFile.getId());
			fileEntry.setType(templateFile.getType());

			// 替换路径中的占位符
			String templateFilename = templateFile.getFilename();
			String filename = StrUtil.format(templateFilename, context);
			fileEntry.setFilename(filename);

			String parentFilePath = GenerateUtils.evaluateRealPath(templateFile.getParentFilePath(), context);
			fileEntry.setParentFilePath(parentFilePath);

			switch (fileEntry.getType()) {
				case TEMPLATE_FILE:
					String filePath = GenerateUtils.concatFilePath(parentFilePath, filename);
					fileEntry.setFilePath(filePath);
					// 文件内容渲染
					TemplateEngineTypeEnum engineTypeEnum = TemplateEngineTypeEnum.of(templateFile.getEngineType());

					try {
						String templateContent = StrUtil.str(templateFile.getFileContent(), StandardCharsets.UTF_8);
						String content = templateEngineDelegator.render(engineTypeEnum, templateContent, context);
						fileEntry.setFileContent(content.getBytes(StandardCharsets.UTF_8));
					}
					catch (TemplateRenderException ex) {
						String errorMessage = CharSequenceUtil.format("模板渲染异常，模板文件名：【{}】，错误详情：{}", templateFilename,
								ex.getMessage());
						throw new BusinessException(SystemResultCode.SERVER_ERROR.getCode(), errorMessage);
					}
					break;
				case BINARY_FILE:
					String binaryFilePath = GenerateUtils.concatFilePath(parentFilePath, filename);
					fileEntry.setFilePath(binaryFilePath);
					fileEntry.setFileContent(templateFile.getFileContent());
					break;
				case FOLDER:
					String currentPath = GenerateUtils.evaluateRealPath(templateFilename, context);
					fileEntry.setFilePath(GenerateUtils.concatFilePath(parentFilePath, currentPath));
					break;
				default:
					log.warn("错误的文件类型: {}", templateFile);
			}

			map.put(fileEntry.getFilePath(), fileEntry);
		}
		return map;
	}

}
