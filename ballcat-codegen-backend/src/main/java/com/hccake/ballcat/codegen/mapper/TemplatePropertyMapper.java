package com.hccake.ballcat.codegen.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hccake.ballcat.codegen.converter.TemplatePropertyConverter;
import com.hccake.ballcat.codegen.model.entity.TemplateProperty;
import com.hccake.ballcat.codegen.model.qo.TemplatePropertyQO;
import com.hccake.ballcat.codegen.model.vo.TemplatePropertyPageVO;
import com.hccake.ballcat.common.model.domain.PageParam;
import com.hccake.ballcat.common.model.domain.PageResult;
import com.hccake.extend.mybatis.plus.conditions.query.LambdaQueryWrapperX;
import com.hccake.extend.mybatis.plus.mapper.ExtendMapper;
import com.hccake.extend.mybatis.plus.toolkit.WrappersX;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 模板属性配置
 *
 * @author hccake 2020-06-22 15:46:39
 */
@Mapper
public interface TemplatePropertyMapper extends ExtendMapper<TemplateProperty> {

	/**
	 * 分页查询
	 * @param pageParam 分页参数
	 * @param qo 查询参数
	 * @return PageResult<TemplatePropertyVO> 分页数据
	 */
	default PageResult<TemplatePropertyPageVO> queryPage(PageParam pageParam, TemplatePropertyQO qo) {
		IPage<TemplateProperty> page = this.prodPage(pageParam);
		LambdaQueryWrapperX<TemplateProperty> wrapperX = WrappersX.lambdaQueryX(TemplateProperty.class)
			.eqIfPresent(TemplateProperty::getId, qo.getId())
			.eqIfPresent(TemplateProperty::getGroupKey, qo.getGroupKey());
		this.selectPage(page, wrapperX);
		IPage<TemplatePropertyPageVO> voPage = page.convert(TemplatePropertyConverter.INSTANCE::poToPageVo);
		return new PageResult<>(voPage.getRecords(), voPage.getTotal());
	}

	/**
	 * 根据模板组标识获取模板组的所有配置
	 * @param templateGroupKey 模板组标识
	 * @return List<TemplateProperty> 配置列表
	 */
	default List<TemplateProperty> listByTemplateGroupKey(String templateGroupKey) {
		return this.selectList(Wrappers.<TemplateProperty>lambdaQuery()
			.eq(TemplateProperty::getGroupKey, templateGroupKey)
			.orderByAsc(TemplateProperty::getOrderValue));

	}

	/**
	 * 根据模板组标识 删除模板属性
	 * @param templateGroupKey 模板组标识
	 */
	default void removeByGroupKey(String templateGroupKey) {
		this.delete(Wrappers.lambdaQuery(TemplateProperty.class).eq(TemplateProperty::getGroupKey, templateGroupKey));
	}

}