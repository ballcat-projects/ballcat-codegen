package com.hccake.ballcat.codegen.engine;

import java.util.Map;

/**
 * 模板引擎
 *
 * @author hccake
 */
public interface TemplateEngine {

	/**
	 * 当前模板引擎对应的类型枚举
	 * @return TemplateEngineTypeEnum
	 */
	TemplateEngineTypeEnum type();

	/**
	 * 渲染模板字符串
	 * @param templateContent 模板字符串
	 * @param context 渲染使用的上下文
	 * @return 渲染完成后的字符串
	 */
	String render(String templateContent, Map<String, Object> context);

}
