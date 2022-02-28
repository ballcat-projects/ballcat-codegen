package com.hccake.ballcat.codegen.mapper;

import com.baomidou.mybatisplus.annotation.DbType;
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
