package com.hccake.ballcat.codegen.database.mysql;

import com.baomidou.mybatisplus.annotation.DbType;
import com.hccake.ballcat.codegen.database.TableInfoMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author hccake
 */
@Mapper
public interface MysqlTableInfoMapper extends TableInfoMapper {

	@Override
	default DbType dbType() {
		return DbType.MYSQL;
	}

}
