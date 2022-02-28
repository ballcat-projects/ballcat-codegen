package com.hccake.ballcat.codegen.datasource;

import com.baomidou.dynamic.datasource.creator.DefaultDataSourceCreator;
import com.baomidou.dynamic.datasource.provider.DynamicDataSourceProvider;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.hccake.ballcat.codegen.constant.DataSourceConstants;
import com.hccake.ballcat.codegen.util.DbTypeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 默认只提供主数据源，生成使用数据源，启动后再加载，避免影响项目启动
 *
 * @see DynamicJdbcDataSourceLoader
 * @author Hccake
 * @version 1.0
 * @date 2020/6/15 17:46
 */
@Slf4j
public class MasterDataSourceProvider implements DynamicDataSourceProvider {

	/**
	 * JDBC url 地址
	 */
	private final String url;

	/**
	 * JDBC 用户名
	 */
	private final String username;

	/**
	 * JDBC 密码
	 */
	private final String password;

	/**
	 * 数据源创建器
	 */
	private final DefaultDataSourceCreator defaultDataSourceCreator;

	public MasterDataSourceProvider(DataSourceProperties dataSourceProperties,
			DefaultDataSourceCreator dataSourceCreator) {
		this.url = dataSourceProperties.getUrl();
		this.username = dataSourceProperties.getUsername();
		this.password = dataSourceProperties.getPassword();
		this.defaultDataSourceCreator = dataSourceCreator;
	}

	/**
	 * 加载所有数据源
	 * @return 所有数据源，key为数据源名称
	 */
	@Override
	public Map<String, DataSource> loadDataSources() {
		// 添加主数据源
		Map<String, DataSource> dataSourceMap = new HashMap<>(8);

		DataSourceProperty masterDataSourceProperty = new DataSourceProperty();
		masterDataSourceProperty.setUsername(username);
		masterDataSourceProperty.setPassword(password);
		masterDataSourceProperty.setUrl(url);
		String dsName = DataSourceConstants.DEFAULT_DS_NAME;
		String poolName = masterDataSourceProperty.getPoolName();
		if (poolName == null || "".equals(poolName)) {
			poolName = dsName;
		}
		masterDataSourceProperty.setPoolName(poolName);
		DataSource dataSource = defaultDataSourceCreator.createDataSource(masterDataSourceProperty);
		// 包装一层
		WrappedDataSource wrappedDataSource = new WrappedDataSource(dsName, DbTypeUtils.getDbType(url), dataSource);
		dataSourceMap.put(dsName, wrappedDataSource);

		return dataSourceMap;
	}

}
