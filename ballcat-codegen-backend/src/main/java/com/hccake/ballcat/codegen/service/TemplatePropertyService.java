package com.hccake.ballcat.codegen.service;

import com.hccake.ballcat.codegen.model.entity.TemplateProperty;
import com.hccake.ballcat.codegen.model.qo.TemplatePropertyQO;
import com.hccake.ballcat.codegen.model.vo.TemplatePropertyPageVO;
import com.hccake.ballcat.common.model.domain.PageParam;
import com.hccake.ballcat.common.model.domain.PageResult;
import com.hccake.extend.mybatis.plus.service.ExtendService;

import java.util.List;

/**
 * 模板属性配置
 *
 * @author hccake
 * @since 2020-06-22 15:46:39
 */
public interface TemplatePropertyService extends ExtendService<TemplateProperty> {

	/**
	 * 根据QueryObject查询分页数据
	 * @param pageParam 分页参数
	 * @param qo 查询参数对象
	 * @return 分页数据
	 */
	PageResult<TemplatePropertyPageVO> queryPage(PageParam pageParam, TemplatePropertyQO qo);

	/**
	 * 获取模板组的所有配置
	 * @param groupKey 模板组标识
	 * @return List<TemplatePropertyVO> 配置列表
	 */
	List<TemplateProperty> listByGroupKey(String groupKey);

	/**
	 * 复制模板属性配置
	 * @param resourceGroupKey 原模板组标识
	 * @param targetGroupKey 模板模板组标识
	 */
	void copy(String resourceGroupKey, String targetGroupKey);

	/**
	 * 根据模板组标识 删除模板属性
	 * @param groupKey 模板组标识
	 */
	void removeByGroupKey(String groupKey);

	/**
	 * 获取所有计算属性。
	 * @param groupKey 模板组标识
	 * @return 计算属性列表
	 */
	List<TemplateProperty> listComputedProperties(String groupKey);

}