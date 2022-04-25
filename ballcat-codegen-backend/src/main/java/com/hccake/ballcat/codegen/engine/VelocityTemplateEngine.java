package com.hccake.ballcat.codegen.engine;

import com.hccake.ballcat.codegen.exception.TemplateRenderException;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.util.introspection.SecureUberspector;
import org.springframework.stereotype.Component;

import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;

/**
 * Velocity 的模板引擎
 *
 * @author hccake
 */
@Component
public class VelocityTemplateEngine implements TemplateEngine {

	static {
		// 设置velocity资源加载器, 保留文件加载loader
		Properties prop = new Properties();
		prop.put("file.resource.loader.class", ClasspathResourceLoader.class.getName());
		// 安全处理
		prop.put("runtime.introspector.uberspect", SecureUberspector.class.getName());
		Velocity.init(prop);
	}

	@Override
	public TemplateEngineTypeEnum type() {
		return TemplateEngineTypeEnum.VELOCITY;
	}

	@Override
	public String render(String templateContent, Map<String, Object> context) throws TemplateRenderException {
		VelocityContext velocityContext = new VelocityContext(context);
		try (StringWriter sw = new StringWriter()) {
			Velocity.evaluate(velocityContext, sw, "velocityTemplateEngine", templateContent);
			return sw.toString();
		}
		catch (Exception ex) {
			throw new TemplateRenderException(ex);
		}
	}

}
