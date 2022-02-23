package com.hccake.ballcat.codegen.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				ZipOutputStream zip = new ZipOutputStream(outputStream)) {

			// 根据tableName 查询最新的表单配置
			List<TemplateDirectoryEntry> templateEntryList = templateDirectoryEntryService
					.listByIds(generatorOptionDTO.getTemplateEntryIds());
			List<TemplateFile> templateFiles = templateDirectoryEntryService.convertToTemplateFile(templateEntryList);

			// 过滤仅获取文件
			List<TemplateFile> files = templateFiles.stream()
					.filter(x -> x.getType().equals(DirectoryEntryTypeEnum.FILE.getType()))
					.collect(Collectors.toList());
			Assert.notEmpty(files, "模板组中模板文件为空！");

			for (String tableName : generatorOptionDTO.getTableNames()) {
				// 查询表信息
				TableInfo tableInfo = tableInfoService.queryTableInfo(tableName);
				// 查询列信息
				List<ColumnInfo> columnInfoList = tableInfoService.listColumnInfo(tableName);
				// 生成代码
				GenUtils.generatorCode(generatorOptionDTO.getTablePrefix(), generatorOptionDTO.getGenProperties(),
						tableInfo, columnInfoList, zip, files);
			}
			// 手动结束 zip，防止文件末端未被写入
			zip.finish();
			return outputStream.toByteArray();
		}
	}

	@Override
	public List<FileEntry> previewCode(GeneratorOptionDTO generateOptionDTO) {
		List<TemplateDirectoryEntry> templateEntryList = templateDirectoryEntryService
				.listByIds(generateOptionDTO.getTemplateEntryIds());

		List<TemplateFile> templateFiles = templateDirectoryEntryService.convertToTemplateFile(templateEntryList);

		Map<String, FileEntry> map = new HashMap<>(templateFiles.size());

		for (String tableName : generateOptionDTO.getTableNames()) {
			// 查询表信息
			TableInfo tableInfo = tableInfoService.queryTableInfo(tableName);
			// 查询列信息
			List<ColumnInfo> columnInfoList = tableInfoService.listColumnInfo(tableName);
			// 生成代码
			Map<String, FileEntry> fileEntryMap = GenUtils.previewCode(generateOptionDTO.getTablePrefix(),
					generateOptionDTO.getGenProperties(), tableInfo, columnInfoList, templateFiles);
			map.putAll(fileEntryMap);
		}

		return CollectionUtil.sort(map.values(),
				Comparator.comparing(FileEntry::getFilename, String.CASE_INSENSITIVE_ORDER));
	}

}
