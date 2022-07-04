package com.hccake.ballcat.codegen.util;

import com.baomidou.mybatisplus.annotation.DbType;

import javax.validation.constraints.NotNull;

/**
 * @author hccake
 */
public final class DbTypeUtils {

	private DbTypeUtils() {
	}

	/**
	 * 根据数据库连接地址推断数据库类型
	 *
	 * @param jdbcUrl url
	 * @return DbType
	 */
	public static DbType getDbType(@NotNull String jdbcUrl) {
		if (jdbcUrl.contains(":mysql:") || jdbcUrl.contains(":cobar:")) {
			return DbType.MYSQL;
		} else if (jdbcUrl.contains(":oracle:")) {
			return DbType.ORACLE;
		} else if (jdbcUrl.contains(":postgresql:")) {
			return DbType.POSTGRE_SQL;
		} else if (jdbcUrl.contains(":sqlserver:")) {
			return DbType.SQL_SERVER;
		} else if (jdbcUrl.contains(":db2:")) {
			return DbType.DB2;
		} else if (jdbcUrl.contains(":mariadb:")) {
			return DbType.MARIADB;
		} else if (jdbcUrl.contains(":sqlite:")) {
			return DbType.SQLITE;
		} else if (jdbcUrl.contains(":h2:")) {
			return DbType.H2;
		} else if (jdbcUrl.contains(":kingbase:") || jdbcUrl.contains(":kingbase8:")) {
			return DbType.KINGBASE_ES;
		} else if (jdbcUrl.contains(":dm:")) {
			return DbType.DM;
		} else if (jdbcUrl.contains(":zenith:")) {
			return DbType.GAUSS;
		} else if (jdbcUrl.contains(":oscar:")) {
			return DbType.OSCAR;
		} else if (jdbcUrl.contains(":firebird:")) {
			return DbType.FIREBIRD;
		} else if (jdbcUrl.contains(":xugu:")) {
			return DbType.XU_GU;
		} else if (jdbcUrl.contains(":clickhouse:")) {
			return DbType.CLICK_HOUSE;
		} else if (jdbcUrl.contains(":sybase:")) {
			return DbType.SYBASE;
		} else {
			return DbType.OTHER;
		}
	}


	/**
	 * 根据 dbType 返回对应的数据库类型
	 *
	 * @param dbType 数据库类型
	 * @return 数据库对应的值
	 */
	public static int getDbType(DbType dbType) {
		switch (dbType) {
			case MYSQL:
				return 1;
			case ORACLE:
				return 2;
			case POSTGRE_SQL:
				return 3;
			case SQL_SERVER:
				return 4;
			default:
				return 0;
		}
	}
}
