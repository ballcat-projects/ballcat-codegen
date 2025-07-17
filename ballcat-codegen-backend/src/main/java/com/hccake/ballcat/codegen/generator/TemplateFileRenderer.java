package com.hccake.ballcat.codegen.generator;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.StrUtil;
import com.hccake.ballcat.codegen.engine.TemplateEngineDelegator;
import com.hccake.ballcat.codegen.engine.TemplateEngineTypeEnum;
import com.hccake.ballcat.codegen.exception.TemplateRenderException;
import com.hccake.ballcat.codegen.model.bo.FileEntry;
import com.hccake.ballcat.codegen.model.bo.TemplateFile;
import com.hccake.ballcat.codegen.util.FilePathUtils;
import com.hccake.ballcat.common.core.exception.BusinessException;
import com.hccake.ballcat.common.model.result.SystemResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 文件处理器 负责处理模板文件的渲染和文件生成
 *
 * @author hccake
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TemplateFileRenderer {

	private final TemplateEngineDelegator templateEngineDelegator;

	/**
	 * 处理单个模板文件
	 */
	public FileEntry processTemplateFile(TemplateFile templateFile, Map<String, Object> context) {
		FileEntry fileEntry = new FileEntry();
		fileEntry.setId(templateFile.getId());
		fileEntry.setType(templateFile.getType());

		// 替换文件名中的占位符
		String filename = StrUtil.format(templateFile.getFilename(), context);
		fileEntry.setFilename(filename);

		String parentFilePath = evaluateRealPath(templateFile.getParentFilePath(), context);
		fileEntry.setParentFilePath(parentFilePath);

		switch (fileEntry.getType()) {
			case TEMPLATE_FILE:
				processTemplateFileContent(templateFile, fileEntry, context, parentFilePath, filename);
				break;
			case BINARY_FILE:
				processBinaryFile(fileEntry, templateFile, parentFilePath, filename);
				break;
			case FOLDER:
				processFolder(fileEntry, templateFile, context, parentFilePath);
				break;
			default:
				log.warn("未知的文件类型: {}", templateFile.getType());
		}

		return fileEntry;
	}

	/**
	 * 处理模板文件内容
	 */
	private void processTemplateFileContent(TemplateFile templateFile, FileEntry fileEntry, Map<String, Object> context,
			String parentFilePath, String filename) {
		String filePath = FilePathUtils.concatFilePath(parentFilePath, filename);
		fileEntry.setFilePath(filePath);

		TemplateEngineTypeEnum engineType = TemplateEngineTypeEnum.of(templateFile.getEngineType());
		String templateContent = StrUtil.str(templateFile.getFileContent(), StandardCharsets.UTF_8);

		try {
			String renderedContent = templateEngineDelegator.render(engineType, templateContent, context);
			fileEntry.setFileContent(renderedContent.getBytes(StandardCharsets.UTF_8));
		}
		catch (TemplateRenderException ex) {
			throw new BusinessException(SystemResultCode.SERVER_ERROR.getCode(), "模板渲染失败: " + ex.getMessage());
		}
	}

	/**
	 * 处理二进制文件
	 */
	private void processBinaryFile(FileEntry fileEntry, TemplateFile templateFile, String parentFilePath,
			String filename) {
		String filePath = FilePathUtils.concatFilePath(parentFilePath, filename);
		fileEntry.setFilePath(filePath);
		fileEntry.setFileContent(templateFile.getFileContent());
	}

	/**
	 * 处理文件夹
	 */
	private void processFolder(FileEntry fileEntry, TemplateFile templateFile, Map<String, Object> context,
			String parentFilePath) {
		String currentPath = evaluateRealPath(templateFile.getFilename(), context);
		fileEntry.setFilePath(FilePathUtils.concatFilePath(parentFilePath, currentPath));
	}

	/**
	 * 获取真实的文件全路径
	 */
	private String evaluateRealPath(String filePathMaker, Map<String, Object> map) {
		// 占位符替换
		String realFilePath = StrUtil.format(filePathMaker, map);
		if (CharSequenceUtil.isEmpty(realFilePath)) {
			return realFilePath;
		}
		// 用 . 标识文件夹合并，所以需要替换成 /
		realFilePath = realFilePath.replace(StrPool.DOT, File.separator);
		// 防止多写了 /
		realFilePath = realFilePath.replace(File.separator + File.separator, File.separator);

		return realFilePath;
	}

}