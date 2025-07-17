package com.hccake.ballcat.codegen.generator.context;

import com.hccake.ballcat.codegen.model.bo.TableDetails;
import com.hccake.ballcat.codegen.model.bo.TemplateFile;
import com.hccake.ballcat.codegen.model.entity.TemplateProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 代码生成上下文 包含代码生成所需的所有信息
 *
 * @author hccake
 */
@Data
@Builder
public class CodeGenerationContext {

	/**
	 * 模板组标识
	 */
	private String templateGroupKey;

	/**
	 * 模板文件列表
	 */
	private List<TemplateFile> templateFiles;

	/**
	 * 计算属性列表
	 */
	private List<TemplateProperty> computedProperties;

	/**
	 * 用户自定义属性
	 */
	private Map<String, String> customProperties;

	/**
	 * 表详情列表（可能为空，用于无表生成场景）
	 */
	private List<TableDetails> tableDetailsList;

	/**
	 * 表前缀
	 */
	private String tablePrefix;

	/**
	 * 生成模式
	 */
	private GenerationMode generationMode;

	/**
	 * 生成模式枚举
	 */
	public enum GenerationMode {

		/**
		 * 基于数据表生成
		 */
		TABLE_BASED,
		/**
		 * 无表生成（仅基于自定义属性）
		 */
		TEMPLATE_ONLY

	}

}