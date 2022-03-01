package com.hccake.ballcat.codegen.database.postgre;

import com.baomidou.mybatisplus.annotation.DbType;
import com.hccake.ballcat.codegen.database.TableInfoMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author hccake
 */
@Mapper
public interface PostgreTableInfoMapper extends TableInfoMapper {

	@Override
	default DbType dbType() {
		return DbType.POSTGRE_SQL;
	}

}
