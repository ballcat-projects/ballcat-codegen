package com.hccake.ballcat.codegen.service;

import cn.hutool.core.collection.CollUtil;
import com.hccake.ballcat.codegen.mapper.TemplatePropertyMapper;
import com.hccake.ballcat.codegen.model.entity.TemplateProperty;
import com.hccake.ballcat.codegen.model.qo.TemplatePropertyQO;
import com.hccake.ballcat.codegen.model.vo.TemplatePropertyPageVO;
import com.hccake.ballcat.common.model.domain.PageParam;
import com.hccake.ballcat.common.model.domain.PageResult;
import com.hccake.extend.mybatis.plus.service.impl.ExtendServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 模板属性配置
 *
 * @author hccake 2020-06-22 15:46:39
 */
@Service
public class TemplatePropertyService extends ExtendServiceImpl<TemplatePropertyMapper, TemplateProperty> {

	/**
	 * 根据QueryObject查询分页数据
	 * @param pageParam 分页参数
	 * @param qo 查询参数对象
	 * @return 分页数据
	 */
	public PageResult<TemplatePropertyPageVO> queryPage(PageParam pageParam, TemplatePropertyQO qo) {
		return baseMapper.queryPage(pageParam, qo);
	}

	/**
	 * 根据模板组标识获取模板组的所有配置
	 * @param groupKey 模板组标识
	 * @return List<TemplateProperty> 配置列表
	 */
	public List<TemplateProperty> listByGroupKey(String groupKey) {
		return baseMapper.listByTemplateGroupKey(groupKey);
	}

	/**
	 * 获取模板组的所有指定类型的配置。
	 * @param groupKey 模板组标识
	 * @param propType 属性类型
	 * @return List<TemplatePropertyVO> 配置列表
	 */
	public List<TemplateProperty> listByGroupKeyAndPropType(String groupKey, Integer propType) {
		return baseMapper.listByGroupKeyAndPropType(groupKey, propType);
	}

	/**
	 * 复制模板属性配置
	 * @param resourceGroupKey 原模板组标识
	 * @param targetGroupKey 模板模板组标识
	 */
	public void copy(String resourceGroupKey, String targetGroupKey) {
		List<TemplateProperty> templateProperties = baseMapper.listByTemplateGroupKey(resourceGroupKey);
		if (CollUtil.isNotEmpty(templateProperties)) {
			List<TemplateProperty> list = new ArrayList<>();
			for (TemplateProperty x : templateProperties) {
				x.setId(null);
				x.setCreateTime(null);
				x.setUpdateTime(null);
				x.setGroupKey(targetGroupKey);
				list.add(x);
			}
			baseMapper.insertBatchSomeColumn(list);
		}
	}

	/**
	 * 根据模板组标识 删除模板属性
	 * @param groupKey 模板组标识
	 */
	public void removeByGroupKey(String groupKey) {
		baseMapper.removeByGroupKey(groupKey);
	}

	/**
	 * 获取所有计算属性。
	 * @param groupKey 模板组标识
	 * @return 计算属性列表
	 */
	public List<TemplateProperty> listComputedProperties(String groupKey) {
		return baseMapper.listComputedProperties(groupKey);
	}

}