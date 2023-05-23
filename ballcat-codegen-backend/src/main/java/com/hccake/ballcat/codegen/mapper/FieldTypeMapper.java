package com.hccake.ballcat.codegen.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hccake.ballcat.codegen.converter.FieldTypeConverter;
import com.hccake.ballcat.codegen.model.entity.FieldType;
import com.hccake.ballcat.codegen.model.qo.FieldTypeQO;
import com.hccake.ballcat.codegen.model.vo.FieldTypeVO;
import com.hccake.ballcat.common.model.domain.PageParam;
import com.hccake.ballcat.common.model.domain.PageResult;
import com.hccake.extend.mybatis.plus.conditions.query.LambdaQueryWrapperX;
import com.hccake.extend.mybatis.plus.mapper.ExtendMapper;
import com.hccake.extend.mybatis.plus.toolkit.WrappersX;
import org.apache.ibatis.annotations.Mapper;

/**
 * software：IntelliJ IDEA 2022.2 interface name: FieldTypeMapper description：DB和后端数据类型 The
 * Mapper interface
 *
 * @author MoBaiJun 2022-07-01 14:27:10
 */
@Mapper
public interface FieldTypeMapper extends ExtendMapper<FieldType> {

	/**
	 * 分页查询
	 * @param pageParam pagination parameters
	 * @param qo Query conditions
	 * @return PageResult<FieldTypeVO> Paginated result set
	 */
	default PageResult<FieldTypeVO> queryPage(PageParam pageParam, FieldTypeQO qo) {
		IPage<FieldType> page = this.prodPage(pageParam);
		LambdaQueryWrapperX<FieldType> wrapper = WrappersX.lambdaQueryX(FieldType.class)
			.eqIfPresent(FieldType::getGroupKey, qo.getGroupKey())
			.eqIfPresent(FieldType::getDbType, qo.getDbType());
		this.selectPage(page, wrapper);
		IPage<FieldTypeVO> voPage = page.convert(FieldTypeConverter.INSTANCE::toVo);
		return new PageResult<>(voPage.getRecords(), voPage.getTotal());
	}

}