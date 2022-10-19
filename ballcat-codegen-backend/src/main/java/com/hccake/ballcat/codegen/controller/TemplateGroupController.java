package com.hccake.ballcat.codegen.controller;

import com.hccake.ballcat.codegen.model.entity.TemplateGroup;
import com.hccake.ballcat.codegen.model.qo.TemplateGroupQO;
import com.hccake.ballcat.codegen.model.vo.TemplateGroupPageVO;
import com.hccake.ballcat.codegen.service.TemplateGroupService;
import com.hccake.ballcat.common.model.domain.PageParam;
import com.hccake.ballcat.common.model.domain.PageResult;
import com.hccake.ballcat.common.model.domain.SelectData;
import com.hccake.ballcat.common.model.result.BaseResultCode;
import com.hccake.ballcat.common.model.result.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 模板组
 *
 * @author hccake
 * @date 2020-06-19 19:11:41
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/gen/template/group")
@Tag(name = "模板组管理")
public class TemplateGroupController {

	private final TemplateGroupService templateGroupService;

	/**
	 * 分页查询
	 * @param pageParam 分页参数
	 * @param templateGroupQO 模板组查询对象
	 * @return R
	 */
	@Operation(summary = "分页查询")
	@GetMapping("/page")
	// @PreAuthorize("@per.hasPermission('codegen:templategroup:read')" )
	public R<PageResult<TemplateGroupPageVO>> getTemplateGroupPage(@Validated PageParam pageParam,
			TemplateGroupQO templateGroupQO) {
		return R.ok(templateGroupService.queryPage(pageParam, templateGroupQO));
	}

	/**
	 * 通过id查询模板组
	 * @param id id
	 * @return R
	 */
	@Operation(summary = "通过id查询")
	@GetMapping("/{id}")
	// @PreAuthorize("@per.hasPermission('codegen:templategroup:read')" )
	public R<TemplateGroup> getById(@PathVariable("id") Integer id) {
		return R.ok(templateGroupService.getById(id));
	}

	/**
	 * 新增模板组
	 * @param templateGroup 模板组
	 * @return R
	 */
	@Operation(summary = "新增模板组")
	// @CreateOperationLogging(msg = "新增模板组" )
	@PostMapping
	// @PreAuthorize("@per.hasPermission('codegen:templategroup:add')" )
	public R<Void> save(@RequestBody TemplateGroup templateGroup) {
		return templateGroupService.save(templateGroup) ? R.ok()
				: R.failed(BaseResultCode.UPDATE_DATABASE_ERROR, "新增模板组失败");
	}

	/**
	 * 复制模板组
	 * @param resourceGroupKey 原模板组标识
	 * @param templateGroup 新模板组实体
	 * @return R
	 */
	@PostMapping("/{resourceGroupKey}")
	@Operation(summary = "复制模板组")
	public R<Void> copy(@PathVariable String resourceGroupKey, @RequestBody TemplateGroup templateGroup) {
		return templateGroupService.copy(resourceGroupKey, templateGroup) ? R.ok()
				: R.failed(BaseResultCode.UPDATE_DATABASE_ERROR, "复制模板组失败");
	}

	/**
	 * 修改模板组
	 * @param templateGroup 模板组
	 * @return R
	 */
	@Operation(summary = "修改模板组")
	// @UpdateOperationLogging(msg = "修改模板组" )
	@PutMapping
	// @PreAuthorize("@per.hasPermission('codegen:templategroup:edit')" )
	public R<Void> updateById(@RequestBody TemplateGroup templateGroup) {
		return templateGroupService.updateById(templateGroup) ? R.ok()
				: R.failed(BaseResultCode.UPDATE_DATABASE_ERROR, "修改模板组失败");
	}

	/**
	 * 通过id删除模板组
	 * @param groupKey groupKey
	 * @return R
	 */
	@Operation(summary = "通过 groupKey 删除模板组")
	// @DeleteOperationLogging(msg = "通过id删除模板组" )
	@DeleteMapping("/{groupKey}")
	// @PreAuthorize("@per.hasPermission('codegen:templategroup:del')" )
	public R<Void> removeById(@PathVariable String groupKey) {
		return templateGroupService.removeByGroupKey(groupKey) ? R.ok()
				: R.failed(BaseResultCode.UPDATE_DATABASE_ERROR, "通过id删除模板组失败");
	}

	/**
	 * 获取模板组选择框数据
	 * @return R
	 */
	@Operation(summary = "获取模板组选择框")
	// @DeleteOperationLogging(msg = "通过id删除模板组" )
	@GetMapping("/select")
	// @PreAuthorize("@per.hasPermission('codegen:templategroup:del')" )
	public R<List<SelectData<Void>>> listSelectData() {
		return R.ok(templateGroupService.listSelectData());
	}

}