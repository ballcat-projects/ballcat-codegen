package com.hccake.ballcat.codegen.service;

import com.hccake.ballcat.codegen.model.entity.TypeScriptType;
import com.hccake.ballcat.codegen.model.qo.TypeScriptTypeQO;
import com.hccake.ballcat.codegen.model.vo.TypeScriptTypeVO;
import com.hccake.ballcat.common.model.domain.PageParam;
import com.hccake.ballcat.common.model.domain.PageResult;
import com.hccake.extend.mybatis.plus.service.ExtendService;

/**
 * software：IntelliJ IDEA 2022.2
 * interface name: TypeScriptTypeService
 * description：前端和后端数据类型管理 business interface
 *
 * @author MoBaiJun 2022-07-01 14:27:10
 */
public interface TypeScriptTypeService extends ExtendService<TypeScriptType> {

	/**
	 * 分页查询
	 *
	 * @param pageParam 分页参数
	 * @param qo        查询条件
	 * @return 分页集合
	 */
	PageResult<TypeScriptTypeVO> queryPage(PageParam pageParam, TypeScriptTypeQO qo);
}