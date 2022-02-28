package com.hccake.ballcat.codegen.engine;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;

import java.util.Map;

/**
 * 模板引擎的委托者
 * 
 * @author hccake
 */
public class TemplateEngineDelegator {

	public final Map<TemplateEngineTypeEnum, TemplateEngine> templateEngineMap;

	public TemplateEngineDelegator(Map<TemplateEngineTypeEnum, TemplateEngine> templateEngineMap) {
		this.templateEngineMap = templateEngineMap;
	}

	/**
	 * 渲染模板字符串
	 * @param templateContent 模板字符串
	 * @param context 渲染使用的上下文
	 * @return 渲染完成后的字符串
	 */
	public String render(TemplateEngineTypeEnum engineType, String templateContent, Map<String, Object> context) {
		if (StrUtil.isEmpty(templateContent)) {
			return StrUtil.EMPTY;
		}
		TemplateEngine templateEngine = templateEngineMap.get(engineType);
		Assert.notNull(templateEngine, "未找到对应的模板引擎：{}", engineType);
		return templateEngine.render(templateContent, context);
	}

}
