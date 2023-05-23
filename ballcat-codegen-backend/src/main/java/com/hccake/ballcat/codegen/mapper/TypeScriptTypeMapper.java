package com.hccake.ballcat.codegen.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hccake.ballcat.codegen.converter.TypeScriptTypeConverter;
import com.hccake.ballcat.codegen.model.entity.TypeScriptType;
import com.hccake.ballcat.codegen.model.qo.TypeScriptTypeQO;
import com.hccake.ballcat.codegen.model.vo.TypeScriptTypeVO;
import com.hccake.ballcat.common.model.domain.PageParam;
import com.hccake.ballcat.common.model.domain.PageResult;
import com.hccake.extend.mybatis.plus.conditions.query.LambdaQueryWrapperX;
import com.hccake.extend.mybatis.plus.mapper.ExtendMapper;
import com.hccake.extend.mybatis.plus.toolkit.WrappersX;
import org.apache.ibatis.annotations.Mapper;

/**
 * software：IntelliJ IDEA 2022.2 interface name: TypeScriptTypeMapper
 * description：前端和后端数据类型管理 The Mapper interface
 *
 * @author MoBaiJun 2022-07-01 14:27:10
 */
@Mapper
public interface TypeScriptTypeMapper extends ExtendMapper<TypeScriptType> {

	/**
	 * 分页查询
	 * @param pageParam pagination parameters
	 * @param qo Query conditions
	 * @return PageResult<TypeScriptTypeVO> Paginated result set
	 */
	default PageResult<TypeScriptTypeVO> queryPage(PageParam pageParam, TypeScriptTypeQO qo) {
		IPage<TypeScriptType> page = this.prodPage(pageParam);
		LambdaQueryWrapperX<TypeScriptType> wrapper = WrappersX.lambdaQueryX(TypeScriptType.class)
			.eqIfPresent(TypeScriptType::getGroupKey, qo.getGroupKey());
		this.selectPage(page, wrapper);
		IPage<TypeScriptTypeVO> voPage = page.convert(TypeScriptTypeConverter.INSTANCE::toVo);
		return new PageResult<>(voPage.getRecords(), voPage.getTotal());
	}

}