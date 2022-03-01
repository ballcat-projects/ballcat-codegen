package com.hccake.ballcat.codegen.database.sqlserver;

import com.baomidou.mybatisplus.annotation.DbType;
import com.hccake.ballcat.codegen.database.TableInfoMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author hccake
 */
@Mapper
public interface SqlServerTableInfoMapper extends TableInfoMapper {

	@Override
	default DbType dbType() {
		return DbType.SQL_SERVER;
	}

}
