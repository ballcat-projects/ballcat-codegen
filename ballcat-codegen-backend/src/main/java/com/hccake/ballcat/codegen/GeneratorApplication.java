package com.hccake.ballcat.codegen;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

/**
 * @author Hccake 2019/9/12 16:21
 */
@Slf4j
@SpringBootApplication
public class GeneratorApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(GeneratorApplication.class, args);
		Environment bean = context.getBean(Environment.class);
		String property = bean.getProperty("server.port");
		log.info("http://localhost:{}", property + bean.getProperty("server.servlet.context-path"));
		log.info("http://localhost:{}", property + "/swagger-ui.html");
	}

}
