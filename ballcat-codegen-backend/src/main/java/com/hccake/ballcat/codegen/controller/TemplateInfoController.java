package com.hccake.ballcat.codegen.controller;

import com.hccake.ballcat.codegen.model.dto.TemplateFileContentDTO;
import com.hccake.ballcat.codegen.model.entity.TemplateInfo;
import com.hccake.ballcat.codegen.service.TemplateInfoService;
import com.hccake.ballcat.common.model.result.BaseResultCode;
import com.hccake.ballcat.common.model.result.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 模板信息
 *
 * @author hccake
 * @date 2020-06-18 18:32:51
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/gen/template/info")
@Tag(name = "模板信息管理")
public class TemplateInfoController {

	private final TemplateInfoService templateInfoService;

	/**
	 * 指定模板组的文件列表
	 * @param templateGroupId 模板组ID
	 * @return R
	 */
	@Operation(summary = "指定模板组的文件列表")
	@GetMapping("/list/{templateGroupId}")
	public R<List<TemplateInfo>> listTemplateInfo(@PathVariable Integer templateGroupId) {
		return R.ok(templateInfoService.listByTemplateGroupId(templateGroupId));
	}

	/**
	 * 通过id查询模板信息
	 * @param directoryEntryId directoryEntryId
	 * @return R
	 */
	@Operation(summary = "通过id查询")
	@GetMapping("/{directoryEntryId}")
	// @PreAuthorize("@per.hasPermission('gen:template:read')" )
	public R<TemplateInfo> getById(@PathVariable("directoryEntryId") Integer directoryEntryId) {
		return R.ok(templateInfoService.getById(directoryEntryId));
	}

	/**
	 * 修改模板信息
	 * @param genTemplateInfo 模板信息
	 * @return R
	 */
	@Operation(summary = "修改模板信息")
	// @UpdateOperationLogging(msg = "修改模板信息" )
	@PutMapping
	// @PreAuthorize("@per.hasPermission('gen:template:edit')" )
	public R<Void> updateById(@RequestBody TemplateInfo genTemplateInfo) {
		return templateInfoService.updateById(genTemplateInfo) ? R.ok()
				: R.failed(BaseResultCode.UPDATE_DATABASE_ERROR, "修改模板信息失败");
	}

	@Operation(summary = "修改模板内容")
	// @UpdateOperationLogging(msg = "修改模板信息" )
	@PatchMapping("/content")
	// @PreAuthorize("@per.hasPermission('gen:template:edit')" )
	public R<Void> updateContent(@RequestBody TemplateFileContentDTO templateFileContentDTO) {
		return templateInfoService.updateContent(templateFileContentDTO) ? R.ok()
				: R.failed(BaseResultCode.UPDATE_DATABASE_ERROR, "修改模板内容失败");
	}

}
