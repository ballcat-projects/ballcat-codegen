package com.hccake.ballcat.codegen.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.hccake.ballcat.codegen.mapper.TemplateGroupMapper;
import com.hccake.ballcat.codegen.model.entity.TemplateGroup;
import com.hccake.ballcat.codegen.model.qo.TemplateGroupQO;
import com.hccake.ballcat.codegen.model.vo.TemplateGroupPageVO;
import com.hccake.ballcat.codegen.model.vo.TemplateGroupSelectDataAttributes;
import com.hccake.ballcat.codegen.service.TemplateEntryService;
import com.hccake.ballcat.codegen.service.TemplateGroupService;
import com.hccake.ballcat.codegen.service.TemplatePropertyService;
import com.hccake.ballcat.common.model.domain.PageParam;
import com.hccake.ballcat.common.model.domain.PageResult;
import com.hccake.ballcat.common.model.domain.SelectData;
import com.hccake.extend.mybatis.plus.service.impl.ExtendServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 模板组
 *
 * @author hccake 2020-06-19 19:11:41
 */
@Service
@RequiredArgsConstructor
public class TemplateGroupServiceImpl extends ExtendServiceImpl<TemplateGroupMapper, TemplateGroup>
		implements TemplateGroupService {

	private final TemplateEntryService templateEntryService;

	private final TemplatePropertyService templatePropertyService;

	/**
	 * 根据QueryObject查询分页数据
	 * @param pageParam 分页参数
	 * @param qo 查询参数对象
	 * @return 分页数据
	 */
	@Override
	public PageResult<TemplateGroupPageVO> queryPage(PageParam pageParam, TemplateGroupQO qo) {
		return baseMapper.queryPage(pageParam, qo);
	}

	/**
	 * 获取SelectData数据
	 * @return List<SelectData<?>>
	 */
	@Override
	public List<SelectData<TemplateGroupSelectDataAttributes>> listSelectData() {
		return baseMapper.listSelectData();
	}

	/**
	 * 复制模板组
	 * @param resourceGroupKey 原模板组标识
	 * @param templateGroup 模板组
	 * @return boolean 复制成功: true
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean copy(String resourceGroupKey, TemplateGroup templateGroup) {
		// 清空id
		templateGroup.setId(null);
		int insertFlag = baseMapper.insert(templateGroup);
		Assert.isTrue(SqlHelper.retBool(insertFlag), "复制模板组时，保存模板组失败：[{}]", templateGroup);
		// 获取落库成功后的自增ID
		String groupKey = templateGroup.getGroupKey();
		// 复制模板目录文件
		templateEntryService.copy(resourceGroupKey, groupKey);
		// 复制模板属性配置
		templatePropertyService.copy(resourceGroupKey, groupKey);

		return true;
	}

	/**
	 * 删除模板组
	 * @param groupKey 模板组标识
	 * @return 删除成功与否
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean removeByGroupKey(String groupKey) {
		// 删除模板组
		baseMapper.deleteByGroupKey(groupKey);
		// 删除关联文件
		templateEntryService.removeByGroupKey(groupKey);
		// 删除模板属性
		templatePropertyService.removeByGroupKey(groupKey);
		return true;
	}

}