package com.hccake.ballcat.codegen.service.impl;

import com.hccake.ballcat.codegen.mapper.TypeScriptTypeMapper;
import com.hccake.ballcat.codegen.model.entity.TypeScriptType;
import com.hccake.ballcat.codegen.model.qo.TypeScriptTypeQO;
import com.hccake.ballcat.codegen.model.vo.TypeScriptTypeVO;
import com.hccake.ballcat.codegen.service.TypeScriptTypeService;
import com.hccake.ballcat.common.model.domain.PageParam;
import com.hccake.ballcat.common.model.domain.PageResult;
import com.hccake.extend.mybatis.plus.service.impl.ExtendServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * software：IntelliJ IDEA 2022.2
 * class name: TypeScriptTypeServiceImpl
 * description：前端和后端数据类型管理 business implementation class
 *
 * @author MoBaiJun 2022-07-01 14:27:10
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TypeScriptTypeServiceImpl extends ExtendServiceImpl<TypeScriptTypeMapper, TypeScriptType> implements TypeScriptTypeService {

	private final TypeScriptTypeMapper typeScriptTypeMapper;

	@Override
	public PageResult<TypeScriptTypeVO> queryPage(PageParam pageParam, TypeScriptTypeQO qo) {
		return typeScriptTypeMapper.queryPage(pageParam, qo);
	}
}