package com.hccake.ballcat.codegen.generator.context;

import com.hccake.ballcat.codegen.model.bo.FileEntry;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 代码生成结果
 *
 * @author hccake
 */
@Data
@Builder
public class CodeGenerationResult {

	/**
	 * 生成的文件列表
	 */
	private List<FileEntry> fileEntries;

	/**
	 * 文件路径到文件实体的映射
	 */
	private Map<String, FileEntry> fileMap;

	/**
	 * 生成是否成功
	 */
	private boolean success;

	/**
	 * 错误信息（如果生成失败）
	 */
	private String errorMessage;

	/**
	 * 生成统计信息
	 */
	private GenerationStatistics statistics;

	@Data
	@Builder
	public static class GenerationStatistics {

		/**
		 * 生成的文件总数
		 */
		private int totalFiles;

		/**
		 * 模板文件数量
		 */
		private int templateFiles;

		/**
		 * 二进制文件数量
		 */
		private int binaryFiles;

		/**
		 * 文件夹数量
		 */
		private int folders;

		/**
		 * 生成耗时（毫秒）
		 */
		private long duration;

	}

}