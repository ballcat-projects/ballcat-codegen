package com.hccake.ballcat.codegen.service.impl;

import com.baomidou.mybatisplus.annotation.DbType;
import com.hccake.ballcat.codegen.constant.TemplateEntryConstants;
import com.hccake.ballcat.codegen.mapper.FieldTypeMapper;
import com.hccake.ballcat.codegen.model.entity.FieldType;
import com.hccake.ballcat.codegen.model.qo.FieldTypeQO;
import com.hccake.ballcat.codegen.model.vo.FieldTypeVO;
import com.hccake.ballcat.codegen.service.FieldTypeService;
import com.hccake.ballcat.codegen.util.DbTypeUtils;
import com.hccake.ballcat.common.model.domain.PageParam;
import com.hccake.ballcat.common.model.domain.PageResult;
import com.hccake.extend.mybatis.plus.conditions.query.LambdaQueryWrapperX;
import com.hccake.extend.mybatis.plus.service.impl.ExtendServiceImpl;
import com.hccake.extend.mybatis.plus.toolkit.WrappersX;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * software：IntelliJ IDEA 2022.2 class name: FieldTypeServiceImpl description：DB和后端数据类型
 * business implementation class
 *
 * @author MoBaiJun 2022-07-01 14:27:10
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FieldTypeServiceImpl extends ExtendServiceImpl<FieldTypeMapper, FieldType> implements FieldTypeService {

	private final FieldTypeMapper fieldTypeMapper;

	@Override
	public PageResult<FieldTypeVO> queryPage(PageParam pageParam, FieldTypeQO qo) {
		return fieldTypeMapper.queryPage(pageParam, qo);
	}

	@Override
	public List<FieldType> selectFieldTypesWithDefault(DbType dbType, String templateGroupKey) {
		LambdaQueryWrapperX<FieldType> wrapper = WrappersX.lambdaQueryX(FieldType.class)
			.eqIfPresent(FieldType::getDbType, DbTypeUtils.getDbType(dbType))
			.in(FieldType::getGroupKey, TemplateEntryConstants.TREE_ROOT_ID, templateGroupKey);
		return fieldTypeMapper.selectList(wrapper);
	}

}
