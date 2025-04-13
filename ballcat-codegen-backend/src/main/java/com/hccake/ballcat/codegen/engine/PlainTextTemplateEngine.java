package com.hccake.ballcat.codegen.engine;

import java.util.Map;

import org.springframework.stereotype.Component;

/**
 * Plain Text 的模板引擎
 *
 * @author hccake
 */
@Component
public class PlainTextTemplateEngine implements TemplateEngine {

	@Override
	public TemplateEngineTypeEnum type() {
		return TemplateEngineTypeEnum.PLAIN_TEXT;
	}

	/**
	 * 纯文本无需渲染，直接原文返回。
	 * @param templateContent 模板字符串
	 * @param context 渲染使用的上下文
	 * @return 纯文本
	 */
	@Override
	public String render(String templateContent, Map<String, Object> context) {
		return templateContent;
	}

}
