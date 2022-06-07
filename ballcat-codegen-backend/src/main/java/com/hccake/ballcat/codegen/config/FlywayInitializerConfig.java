package com.hccake.ballcat.codegen.config;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.api.Location;
import org.flywaydb.core.api.configuration.ClassicConfiguration;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;

/**
 * software：IntelliJ IDEA 2022.1 class
 * name: FlywayConfig
 * class description： 配置 Flyway 优先加载
 *
 * @author MoBaiJun 2022/6/6 17:32
 */
@Slf4j
@Configuration
public class FlywayInitializerConfig {

	@Resource
	private DataSource dataSource;

	@PostConstruct
	public void migrate() {
		// 配置 flyway
		ClassicConfiguration configuration = new ClassicConfiguration();
		configuration.setDataSource(dataSource);
		// 设置flyway扫描sql升级脚本、java升级脚本的目录路径或包路径（表示是src/main/resources/flyway下面，前缀默认为src/main/resources，因为这个路径默认在classpath下面）
		configuration.setLocations(new Location("db/migration"));
		// 设置sql脚本文件的编码
		configuration.setEncoding(StandardCharsets.UTF_8);
		// 取消占位符检测
		configuration.setPlaceholderReplacement(false);
		// 脚本前缀
		configuration.setSqlMigrationPrefix("V");
		Flyway flyway = new Flyway(configuration);
		try {
			flyway.migrate();
		} catch (FlywayException e) {
			log.error("Flyway配置第一次加载出错", e);
			try {
				flyway.repair();
				log.info("Flyway配置修复成功");
				flyway.migrate();
				log.info("Flyway配置重新加载成功");
			} catch (Exception exception) {
				log.error("Flyway配置第二次加载出错", exception);
				throw exception;
			}
		}
	}
}
