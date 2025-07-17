package com.hccake.ballcat.codegen.generator.property;

import com.hccake.ballcat.codegen.generator.context.CodeGenerationContext;
import com.hccake.ballcat.codegen.model.bo.TableDetails;
import com.hccake.ballcat.codegen.model.entity.TemplateProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 属性处理上下文 包含属性处理所需的所有信息
 *
 * @author hccake
 */
@Data
@Builder
public class PropertyProcessContext {

	/**
	 * 代码生成上下文
	 */
	private CodeGenerationContext codeGenerationContext;

	/**
	 * 表详情（可能为null）
	 */
	private TableDetails tableDetails;

	/**
	 * 已处理的属性（用于计算属性处理）
	 */
	private Map<String, Object> processedProperties;

	/**
	 * 便捷方法：获取模板组标识
	 */
	public String getTemplateGroupKey() {
		return codeGenerationContext != null ? codeGenerationContext.getTemplateGroupKey() : null;
	}

	/**
	 * 便捷方法：获取表前缀
	 */
	public String getTablePrefix() {
		return codeGenerationContext != null ? codeGenerationContext.getTablePrefix() : null;
	}

	/**
	 * 便捷方法：获取自定义属性
	 */
	public Map<String, String> getCustomProperties() {
		return codeGenerationContext != null ? codeGenerationContext.getCustomProperties() : null;
	}

	/**
	 * 便捷方法：获取计算属性
	 */
	public List<TemplateProperty> getComputedProperties() {
		return codeGenerationContext != null ? codeGenerationContext.getComputedProperties() : null;
	}

}