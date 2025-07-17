package com.hccake.ballcat.codegen.generator;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import com.hccake.ballcat.codegen.generator.context.CodeGenerationContext;
import com.hccake.ballcat.codegen.generator.context.CodeGenerationResult;
import com.hccake.ballcat.codegen.generator.context.TemplateContextBuilder;
import com.hccake.ballcat.codegen.model.bo.FileEntry;
import com.hccake.ballcat.codegen.model.bo.TableDetails;
import com.hccake.ballcat.codegen.model.bo.TemplateFile;
import com.hccake.ballcat.common.core.exception.BusinessException;
import com.hccake.ballcat.common.model.result.SystemResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 代码生成器 负责协调整个代码生成流程
 *
 * @author hccake
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CodeGenerator {

	private final TemplateContextBuilder templateContextBuilder;

	private final TemplateFileRenderer templateFileRenderer;

	/**
	 * 生成代码
	 * @param context 生成上下文
	 * @return 生成结果
	 */
	public CodeGenerationResult generate(CodeGenerationContext context) {
		long startTime = System.currentTimeMillis();

		// 记录生成开始
		logGenerationStart(context);

		try {
			// 验证上下文
			if (!isValidContext(context)) {
				log.warn("代码生成失败 - 无效的生成上下文: {}", context);
				return CodeGenerationResult.builder().success(false).errorMessage("无效的生成上下文").build();
			}

			// 执行生成
			Map<String, FileEntry> fileMap = doGenerate(context);

			// 构建结果
			List<FileEntry> fileEntries = CollUtil.sort(fileMap.values(),
					Comparator.comparing(FileEntry::getFilename, String.CASE_INSENSITIVE_ORDER));

			long duration = System.currentTimeMillis() - startTime;

			CodeGenerationResult result = CodeGenerationResult.builder()
				.success(true)
				.fileEntries(fileEntries)
				.fileMap(fileMap)
				.statistics(buildStatistics(fileEntries, duration))
				.build();

			// 记录生成完成
			logGenerationComplete(context, result);
			return result;

		}
		catch (Exception e) {
			log.error("代码生成异常 - 模板组: {}, 异常信息: {}", context.getTemplateGroupKey(), e.getMessage(), e);
			return CodeGenerationResult.builder().success(false).errorMessage(e.getMessage()).build();
		}
	}

	/**
	 * 执行具体的代码生成逻辑
	 */
	private Map<String, FileEntry> doGenerate(CodeGenerationContext context) {
		Map<String, FileEntry> allFiles = new HashMap<>();

		// 判断是否有表数据
		if (CollUtil.isEmpty(context.getTableDetailsList())) {
			// 无表生成：仅基于自定义属性和模板
			Map<String, FileEntry> files = generateForTable(context, null);
			allFiles.putAll(files);
		}
		else {
			// 有表生成：为每个表生成代码
			for (TableDetails tableDetails : context.getTableDetailsList()) {
				Map<String, FileEntry> tableFiles = generateForTable(context, tableDetails);
				allFiles.putAll(tableFiles);
			}
		}

		return allFiles;
	}

	/**
	 * 为单个表（或无表场景）生成代码
	 */
	private Map<String, FileEntry> generateForTable(CodeGenerationContext context, TableDetails tableDetails) {
		Map<String, FileEntry> fileMap = new HashMap<>();

		// 构建模板渲染上下文
		Map<String, Object> templateContext = templateContextBuilder.buildTemplateContext(context, tableDetails);

		// 处理每个模板文件
		for (TemplateFile templateFile : context.getTemplateFiles()) {
			try {
				FileEntry fileEntry = templateFileRenderer.processTemplateFile(templateFile, templateContext);
				fileMap.put(fileEntry.getFilePath(), fileEntry);
			}
			catch (Exception e) {
				String errorMessage = CharSequenceUtil.format("处理模板文件失败，文件名：【{}】，错误详情：{}", templateFile.getFilename(),
						e.getMessage());
				throw new BusinessException(SystemResultCode.SERVER_ERROR.getCode(), errorMessage);
			}
		}

		return fileMap;
	}

	/**
	 * 构建生成统计信息
	 */
	private CodeGenerationResult.GenerationStatistics buildStatistics(List<FileEntry> fileEntries, long duration) {
		int totalFiles = fileEntries.size();
		int templateFiles = 0;
		int binaryFiles = 0;
		int folders = 0;

		for (FileEntry fileEntry : fileEntries) {
			switch (fileEntry.getType()) {
				case TEMPLATE_FILE:
					templateFiles++;
					break;
				case BINARY_FILE:
					binaryFiles++;
					break;
				case FOLDER:
					folders++;
					break;
			}
		}

		return CodeGenerationResult.GenerationStatistics.builder()
			.totalFiles(totalFiles)
			.templateFiles(templateFiles)
			.binaryFiles(binaryFiles)
			.folders(folders)
			.duration(duration)
			.build();
	}

	/**
	 * 检查生成上下文是否有效
	 */
	private boolean isValidContext(CodeGenerationContext context) {
		return context != null && StrUtil.isNotBlank(context.getTemplateGroupKey())
				&& CollUtil.isNotEmpty(context.getTemplateFiles());
	}

	// ==================== 监控日志方法 ====================

	/**
	 * 记录生成开始
	 */
	private void logGenerationStart(CodeGenerationContext context) {
		log.info("开始代码生成 - 模板组: {}, 生成模式: {}, 模板文件数: {}", context.getTemplateGroupKey(), context.getGenerationMode(),
				context.getTemplateFiles() != null ? context.getTemplateFiles().size() : 0);

		if (context.getTableDetailsList() != null && !context.getTableDetailsList().isEmpty()) {
			log.info("涉及数据表: {}", context.getTableDetailsList().stream().map(TableDetails::getTableName).toArray());
		}
	}

	/**
	 * 记录生成完成
	 */
	private void logGenerationComplete(CodeGenerationContext context, CodeGenerationResult result) {
		if (result.isSuccess()) {
			CodeGenerationResult.GenerationStatistics stats = result.getStatistics();
			log.info("代码生成完成 - 模板组: {}, 总文件数: {}, 耗时: {}ms", context.getTemplateGroupKey(), stats.getTotalFiles(),
					stats.getDuration());

			log.debug("生成统计 - 模板文件: {}, 二进制文件: {}, 文件夹: {}", stats.getTemplateFiles(), stats.getBinaryFiles(),
					stats.getFolders());
		}
		else {
			log.error("代码生成失败 - 模板组: {}, 错误信息: {}", context.getTemplateGroupKey(), result.getErrorMessage());
		}
	}

}