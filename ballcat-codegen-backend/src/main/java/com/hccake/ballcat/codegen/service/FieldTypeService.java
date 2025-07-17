package com.hccake.ballcat.codegen.service;

import com.baomidou.mybatisplus.annotation.DbType;
import com.hccake.ballcat.codegen.constant.TemplateEntryConstants;
import com.hccake.ballcat.codegen.mapper.FieldTypeMapper;
import com.hccake.ballcat.codegen.model.entity.FieldType;
import com.hccake.ballcat.codegen.model.qo.FieldTypeQO;
import com.hccake.ballcat.codegen.model.vo.FieldTypeVO;
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
 * software：IntelliJ IDEA 2022.2 class name: FieldTypeService description：DB和后端数据类型
 * business implementation class
 *
 * @author MoBaiJun 2022-07-01 14:27:10
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FieldTypeService extends ExtendServiceImpl<FieldTypeMapper, FieldType> {

	private final FieldTypeMapper fieldTypeMapper;

	/**
	 * 根据QueryObject查询分页数据
	 * @param pageParam 分页参数
	 * @param qo 查询参数对象
	 * @return 分页数据
	 */
	public PageResult<FieldTypeVO> queryPage(PageParam pageParam, FieldTypeQO qo) {
		return fieldTypeMapper.queryPage(pageParam, qo);
	}

	/**
	 * 查询数据库对应数据类型, 没有配置的数据类型配置会使用默认配置
	 * @param dbType 数据库类型
	 * @param templateGroupKey 模板组标识
	 * @return 字段集合
	 */
	public List<FieldType> selectFieldTypesWithDefault(DbType dbType, String templateGroupKey) {
		LambdaQueryWrapperX<FieldType> wrapper = WrappersX.lambdaQueryX(FieldType.class)
			.eqIfPresent(FieldType::getDbType, DbTypeUtils.getDbType(dbType))
			.in(FieldType::getGroupKey, TemplateEntryConstants.TREE_ROOT_ID, templateGroupKey);
		return fieldTypeMapper.selectList(wrapper);
	}

}
