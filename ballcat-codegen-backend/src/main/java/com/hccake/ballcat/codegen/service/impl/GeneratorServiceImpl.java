package com.hccake.ballcat.codegen.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.IoUtil;
import com.hccake.ballcat.codegen.constant.DirectoryEntryTypeEnum;
import com.hccake.ballcat.codegen.model.bo.FileEntry;
import com.hccake.ballcat.codegen.model.bo.TemplateFile;
import com.hccake.ballcat.codegen.model.dto.GeneratorOptionDTO;
import com.hccake.ballcat.codegen.model.entity.TemplateDirectoryEntry;
import com.hccake.ballcat.codegen.model.vo.ColumnInfo;
import com.hccake.ballcat.codegen.model.vo.TableInfo;
import com.hccake.ballcat.codegen.service.GeneratorService;
import com.hccake.ballcat.codegen.service.TableInfoService;
import com.hccake.ballcat.codegen.service.TemplateDirectoryEntryService;
import com.hccake.ballcat.codegen.service.TemplateInfoService;
import com.hccake.ballcat.codegen.util.GenUtils;
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

	private final TableInfoService tableInfoService;

	private final TemplateDirectoryEntryService templateDirectoryEntryService;

	private final TemplateInfoService templateInfoService;

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
				if (DirectoryEntryTypeEnum.FILE.getType().equals(fileEntry.getType())) {
					// 添加到zip
					String filePath = entry.getKey();
					zip.putNextEntry(new ZipEntry(filePath));
					IoUtil.write(zip, StandardCharsets.UTF_8, false, fileEntry.getContent());
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
		return CollectionUtil.sort(map.values(),
				Comparator.comparing(FileEntry::getFilename, String.CASE_INSENSITIVE_ORDER));
	}

	/**
	 * 获得生成后的 代码地址：代码文件 的 map
	 * @param generateOptionDTO 生成参数
	 * @return Map<String, FileEntry>
	 */
	private Map<String, FileEntry> getStringFileEntryMap(GeneratorOptionDTO generateOptionDTO) {
		// 获取模板文件信息
		List<TemplateDirectoryEntry> templateEntryList = templateDirectoryEntryService
				.listByIds(generateOptionDTO.getTemplateEntryIds());
		List<TemplateFile> templateFiles = templateDirectoryEntryService.convertToTemplateFile(templateEntryList);

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
		Map<String, FileEntry> map = new HashMap<>(templateFiles.size());

		for (String tableName : generateOptionDTO.getTableNames()) {
			// 查询表信息
			TableInfo tableInfo = tableInfoService.queryTableInfo(tableName);
			// 查询列信息
			List<ColumnInfo> columnInfoList = tableInfoService.listColumnInfo(tableName);
			// 生成代码
			Map<String, FileEntry> fileEntryMap = GenUtils.generatorCode(generateOptionDTO.getTablePrefix(),
					generateOptionDTO.getGenProperties(), tableInfo, columnInfoList, templateFiles);
			map.putAll(fileEntryMap);
		}
		return map;
	}

}
