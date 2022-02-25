package com.hccake.ballcat.codegen.config;

import com.hccake.ballcat.codegen.engine.TemplateEngine;
import com.hccake.ballcat.codegen.engine.TemplateEngineDelegator;
import com.hccake.ballcat.codegen.engine.TemplateEngineTypeEnum;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 代码生成的配置类
 *
 * @author hccake
 */
@Configuration(proxyBeanMethods = false)
public class GenerateConfiguration {

	/**
	 * 模板引擎委托器
	 * @return TemplateEngineDelegator
	 */
	@Bean
	public TemplateEngineDelegator templateEngineDelegator(List<TemplateEngine> templateEngineList) {
		Map<TemplateEngineTypeEnum, TemplateEngine> templateEngineMap = new HashMap<>(templateEngineList.size());
		for (TemplateEngine templateEngine : templateEngineList) {
			templateEngineMap.put(templateEngine.type(), templateEngine);
		}
		return new TemplateEngineDelegator(templateEngineMap);
	}

}
