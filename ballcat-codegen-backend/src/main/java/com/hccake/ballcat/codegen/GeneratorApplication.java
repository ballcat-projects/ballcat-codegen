package com.hccake.ballcat.codegen;

import cn.hutool.core.lang.ClassScanner;
import com.hccake.ballcat.codegen.database.DbTypeConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.context.annotation.DependsOn;

import java.util.Set;

/**
 * @author Hccake
 * @version 1.0
 * @date 2019/9/12 16:21
 */
@Slf4j
@DependsOn(value = "flywayInitializerConfig")
@SpringBootApplication(exclude = {FlywayAutoConfiguration.class})
public class GeneratorApplication {

	/**
	 * 包路径
	 */
	public static final String PACKAGE = "com.hccake.ballcat.codegen.database";

	public static void main(String[] args) throws ClassNotFoundException {
		loadTypeConverter();
		SpringApplication.run(GeneratorApplication.class);
	}

	/**
	 * 加载所有的类型转换器
	 */
	private static void loadTypeConverter() throws ClassNotFoundException {
		// 包扫描，以便注册所有的 TypeConverter
		Set<Class<?>> classes = ClassScanner.scanPackage(PACKAGE,
				DbTypeConverter.class::isAssignableFrom);
		for (Class<?> aClass : classes) {
			Class.forName(aClass.getName());
			log.info("TypeConverter 加载成功：" + aClass);
		}
	}
}
