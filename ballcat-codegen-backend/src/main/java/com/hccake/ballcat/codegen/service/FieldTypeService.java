package com.hccake.ballcat.codegen.service;

import com.baomidou.mybatisplus.annotation.DbType;
import com.hccake.ballcat.codegen.model.entity.FieldType;
import com.hccake.ballcat.codegen.model.qo.FieldTypeQO;
import com.hccake.ballcat.codegen.model.vo.FieldTypeVO;
import com.hccake.ballcat.common.model.domain.PageParam;
import com.hccake.ballcat.common.model.domain.PageResult;
import com.hccake.extend.mybatis.plus.service.ExtendService;

import java.util.List;

/**
 * software：IntelliJ IDEA 2022.2 interface name: FieldTypeService description：DB和后端数据类型
 * business interface
 *
 * @author MoBaiJun 2022-07-01 14:27:10
 */
public interface FieldTypeService extends ExtendService<FieldType> {

	/**
	 * 根据QueryObject查询分页数据
	 * @param pageParam 分页参数
	 * @param qo 查询参数对象
	 * @return 分页数据
	 */
	PageResult<FieldTypeVO> queryPage(PageParam pageParam, FieldTypeQO qo);

	/**
	 * 查询数据库对应数据类型, 没有配置的数据类型配置会使用默认配置
	 * @param dbType 数据库类型
	 * @param templateGroupKey 模板组标识
	 * @return 字段集合
	 */
	List<FieldType> selectFieldTypesWithDefault(DbType dbType, String templateGroupKey);

}
