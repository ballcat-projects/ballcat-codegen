package com.hccake.ballcat.codegen.engine;

import com.hccake.ballcat.codegen.exception.TemplateRenderException;
import freemarker.core.TemplateClassResolver;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Freemarker 的模板引擎
 *
 * @author hccake
 */
@Component
public class FreemarkerTemplateEngine implements TemplateEngine {

	private final Configuration configuration;

	public FreemarkerTemplateEngine() {
		this.configuration = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
		configuration.setDefaultEncoding(StandardCharsets.UTF_8.name());

		// 安全处理 https://ackcent.com/blog/in-depth-freemarker-template-injection/
		configuration.setNewBuiltinClassResolver(TemplateClassResolver.SAFER_RESOLVER);
	}

	@Override
	public TemplateEngineTypeEnum type() {
		return TemplateEngineTypeEnum.FREEMARKER;
	}

	@SneakyThrows(TemplateRenderException.class)
	@Override
	public String render(String templateContent, Map<String, Object> context) {
		try {
			Template template = new Template("templateName", templateContent, configuration);
			try (StringWriter sw = new StringWriter()) {
				template.process(context, sw);
				return sw.toString();
			}
		}
		catch (Exception ex) {
			throw new TemplateRenderException(ex);
		}
	}

}
