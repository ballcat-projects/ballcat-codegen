package com.hccake.ballcat.codegen.database.oracle;

import com.baomidou.mybatisplus.annotation.DbType;
import com.hccake.ballcat.codegen.database.TableInfoMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author hccake
 */
@Mapper
public interface OracleTableInfoMapper extends TableInfoMapper {

	@Override
	default DbType dbType() {
		return DbType.ORACLE;
	}

}
