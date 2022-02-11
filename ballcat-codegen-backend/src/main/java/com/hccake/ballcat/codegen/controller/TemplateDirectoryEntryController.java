package com.hccake.ballcat.codegen.controller;

import com.hccake.ballcat.codegen.converter.TemplateModelConverter;
import com.hccake.ballcat.codegen.model.dto.TemplateDirectoryCreateDTO;
import com.hccake.ballcat.codegen.model.dto.TemplateDirectoryUpdateDTO;
import com.hccake.ballcat.codegen.model.entity.TemplateDirectoryEntry;
import com.hccake.ballcat.codegen.model.vo.TemplateDirectoryEntryVO;
import com.hccake.ballcat.codegen.service.TemplateDirectoryEntryService;
import com.hccake.ballcat.common.model.result.BaseResultCode;
import com.hccake.ballcat.common.model.result.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 模板文件目录项
 *
 * @author hccake
 * @date 2020-06-19 19:11:41
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/gen/template/directory-entry")
@Tag(name = "模板文件目录项管理")
public class TemplateDirectoryEntryController {

	private final TemplateDirectoryEntryService templateDirectoryEntryService;

	/**
	 * 模板组的文件目录
	 * @param templateGroupId 模板组ID
	 * @return R
	 */
	@Operation(summary = "指定模板组的文件目录项")
	@GetMapping("/list/{templateGroupId}")
	// @PreAuthorize("@per.hasPermission('codegen:templatedirectoryentry:read')" )
	public R<List<TemplateDirectoryEntryVO>> getTemplateDirectoryEntryPage(@PathVariable Integer templateGroupId) {
		List<TemplateDirectoryEntry> entries = templateDirectoryEntryService.listByTemplateGroupId(templateGroupId);
		List<TemplateDirectoryEntryVO> vos = entries.stream().map(TemplateModelConverter.INSTANCE::entryPoToVo)
				.collect(Collectors.toList());
		return R.ok(vos);
	}

	/**
	 * 移动目录项
	 * @param entryId 被移动的目录项ID
	 * @param horizontalMove 是否移动到目标目录平级，否则移动到其内部
	 * @param targetEntryId 目标目录项ID
	 * @return R
	 */
	@Operation(summary = "移动目录项")
	@PatchMapping("/{entryId}/position")
	public R<?> move(@PathVariable Integer entryId, @RequestParam boolean horizontalMove,
			@RequestParam Integer targetEntryId) {
		return templateDirectoryEntryService.move(horizontalMove, entryId, targetEntryId) ? R.ok()
				: R.failed(BaseResultCode.UPDATE_DATABASE_ERROR, "移动目录项失败");
	}

	/**
	 * 新增模板目录项
	 * @param templateDirectoryCreateDTO 模板目录项
	 * @return R
	 */
	@Operation(summary = "新增模板目录项")
	// @CreateOperationLogging(msg = "新增模板文件目录项" )
	@PostMapping
	// @PreAuthorize("@per.hasPermission('codegen:templatedirectoryentry:add')" )
	public R<Integer> save(@RequestBody TemplateDirectoryCreateDTO templateDirectoryCreateDTO) {
		Integer entryId = templateDirectoryEntryService.createEntry(templateDirectoryCreateDTO);
		return entryId != null ? R.ok(entryId) : R.failed(BaseResultCode.UPDATE_DATABASE_ERROR, "新增模板目录项失败");
	}

	/**
	 * 修改目录项
	 * @param templateDirectoryUpdateDTO 模板目录项
	 * @return R
	 */
	@Operation(summary = "修改目录项")
	@PutMapping
	public R<Void> updateEntry(@RequestBody TemplateDirectoryUpdateDTO templateDirectoryUpdateDTO) {
		return templateDirectoryEntryService.updateEntry(templateDirectoryUpdateDTO) ? R.ok()
				: R.failed(BaseResultCode.UPDATE_DATABASE_ERROR, "重命名目录项");
	}

	/**
	 * 通过id删除模板文件目录项
	 * @param id id
	 * @param mode 删除模式， 1：只删除本身，将子节点上移 2. 删除自身及其所有子节点
	 * @return R
	 */
	@Operation(summary = "通过id删除模板文件目录项")
	// @DeleteOperationLogging(msg = "通过id删除模板文件目录项" )
	@DeleteMapping("/{id}")
	// @PreAuthorize("@per.hasPermission('codegen:templatedirectoryentry:del')" )
	public R<Void> removeById(@PathVariable Integer id, @RequestParam Integer mode) {
		return templateDirectoryEntryService.removeEntry(id, mode) ? R.ok()
				: R.failed(BaseResultCode.UPDATE_DATABASE_ERROR, "通过id删除模板文件目录项失败");
	}

}