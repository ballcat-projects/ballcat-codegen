package com.hccake.ballcat.codegen.service;

import com.hccake.ballcat.codegen.model.entity.TemplateGroup;
import com.hccake.ballcat.codegen.model.qo.TemplateGroupQO;
import com.hccake.ballcat.codegen.model.vo.TemplateGroupPageVO;
import com.hccake.ballcat.common.model.domain.PageParam;
import com.hccake.ballcat.common.model.domain.PageResult;
import com.hccake.ballcat.common.model.domain.SelectData;
import com.hccake.extend.mybatis.plus.service.ExtendService;

import java.util.List;

/**
 * 模板组
 *
 * @author hccake 2020-06-19 19:11:41
 */
public interface TemplateGroupService extends ExtendService<TemplateGroup> {

	/**
	 * 根据QueryObject查询分页数据
	 * @param pageParam 分页参数
	 * @param qo 查询参数对象
	 * @return 分页数据
	 */
	PageResult<TemplateGroupPageVO> queryPage(PageParam pageParam, TemplateGroupQO qo);

	/**
	 * 获取SelectData数据
	 * @return List<SelectData<Void>>
	 */
	List<SelectData<Void>> listSelectData();

	/**
	 * 复制模板组
	 * @param resourceGroupKey 原模板组标识
	 * @param templateGroup 目标模板组
	 * @return boolean 复制成功: true
	 */
	boolean copy(String resourceGroupKey, TemplateGroup templateGroup);

	/**
	 * 删除模板组
	 * @param groupKey 模板组标识
	 * @return 删除成功与否
	 */
	boolean removeByGroupKey(String groupKey);

}