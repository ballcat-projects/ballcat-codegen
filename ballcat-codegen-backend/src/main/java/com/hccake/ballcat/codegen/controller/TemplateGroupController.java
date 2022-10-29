package com.hccake.ballcat.codegen.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hccake.ballcat.codegen.constant.TemplateEntryTypeEnum;
import com.hccake.ballcat.codegen.converter.TemplatePropertyConverter;
import com.hccake.ballcat.codegen.model.bo.FileEntry;
import com.hccake.ballcat.codegen.model.bo.TemplateFile;
import com.hccake.ballcat.codegen.model.dto.TemplatePropertyDTO;
import com.hccake.ballcat.codegen.model.entity.TemplateEntry;
import com.hccake.ballcat.codegen.model.entity.TemplateGroup;
import com.hccake.ballcat.codegen.model.entity.TemplateProperty;
import com.hccake.ballcat.codegen.model.qo.TemplateGroupQO;
import com.hccake.ballcat.codegen.model.vo.TemplateGroupPageVO;
import com.hccake.ballcat.codegen.service.TemplateEntryService;
import com.hccake.ballcat.codegen.service.TemplateGroupService;
import com.hccake.ballcat.codegen.service.TemplatePropertyService;
import com.hccake.ballcat.codegen.util.GenerateUtils;
import com.hccake.ballcat.common.model.domain.PageParam;
import com.hccake.ballcat.common.model.domain.PageResult;
import com.hccake.ballcat.common.model.domain.SelectData;
import com.hccake.ballcat.common.model.result.BaseResultCode;
import com.hccake.ballcat.common.model.result.R;
import com.hccake.ballcat.common.model.result.SystemResultCode;
import com.hccake.ballcat.common.util.JsonUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 模板组
 *
 * @author hccake 2020-06-19 19:11:41
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/gen/template/group")
@Tag(name = "模板组管理")
public class TemplateGroupController {

	private final TemplateGroupService templateGroupService;

	private final TemplateEntryService templateEntryService;

	private final TemplatePropertyService templatePropertyService;

	private final ObjectMapper objectMapper;

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
	 * 导入模板组
	 * @param file 模板组压缩包
	 * @return R
	 */
	@Operation(summary = "导入模板")
	@PostMapping("/import/entry")
	public R<Void> importTemplate(@RequestPart("file") MultipartFile file) {
		return R.ok();
	}

	/**
	 * 导出模板组文件
	 * @param groupKey 模板组标识
	 */
	@Operation(summary = "导出模板")
	@GetMapping("/export/entry")
	public void exportTemplate(@RequestParam("groupKey") String groupKey, HttpServletResponse response) throws IOException {
		response.setContentType("application/octet-stream");
		response.setHeader("Content-disposition", "attachment;filename=" + groupKey + "-templates.zip");

		List<TemplateEntry> templateEntries = templateEntryService.listByGroupKey(groupKey);
		if (CollUtil.isEmpty(templateEntries)) {
			return;
		}

		List<TemplateFile> templateFiles = templateEntryService.convertToTemplateFile(templateEntries);

		ServletOutputStream responseOutputStream = response.getOutputStream();
		try (ZipOutputStream zip = new ZipOutputStream(responseOutputStream)) {
			for (TemplateFile templateFile : templateFiles) {
				String filePath = GenerateUtils.concatFilePath(templateFile.getParentFilePath(), templateFile.getFilename());
				Integer type = templateFile.getType();
				// 文件夹必须尾缀 “/”
				if (TemplateEntryTypeEnum.FOLDER.getType().equals(type)) {
					filePath = filePath + "/";
				}
				ZipEntry zipEntry = new ZipEntry(filePath);
				zip.putNextEntry(zipEntry);
				// 文件需要额外写入内容
				if (TemplateEntryTypeEnum.FILE.getType().equals(type)) {
					IoUtil.write(zip, StandardCharsets.UTF_8, false, templateFile.getContent());
				}
				zip.closeEntry();
			}

			// 手动结束 zip，防止文件末端未被写入
			zip.finish();
		}
	}

	/**
	 * 导入模板组属性
	 * @param groupKey 模板组标识
	 * @param file 模板组属性文件
	 */
	@Operation(summary = "导入模板组属性")
	@PostMapping("/import/property")
	public R<String> importProperty(@RequestParam("groupKey") String groupKey, @RequestPart("file") MultipartFile file)
			throws IOException {
		List<TemplatePropertyDTO> dtoList = objectMapper.readValue(file.getInputStream(),
				new TypeReference<List<TemplatePropertyDTO>>() {
				});
		if (CollUtil.isEmpty(dtoList)) {
			return R.ok();
		}

		List<TemplateProperty> list = dtoList.stream().map(x -> TemplatePropertyConverter.INSTANCE.dtoToPo(groupKey, x))
				.collect(Collectors.toList());

		try {
			templatePropertyService.saveBatch(list);
			return R.ok();
		}
		catch (DuplicateKeyException ex) {
			Throwable rootCause = ex.getRootCause();
			String message = "模板组属性重复：" + (rootCause == null ? ex.getMessage() : rootCause.getMessage());
			return R.failed(SystemResultCode.BAD_REQUEST, message);
		}

	}

	/**
	 * 导出模板组属性
	 * @param groupKey 模板组标识
	 */
	@Operation(summary = "导出模板组属性")
	@GetMapping("/export/property")
	public void exportProperty(@RequestParam("groupKey") String groupKey, HttpServletResponse response)
			throws IOException {
		response.setContentType("application/octet-stream");
		response.setHeader("Content-disposition", "attachment;filename=" + groupKey + "-properties.json");

		List<TemplateProperty> templateProperties = templatePropertyService.listByGroupKey(groupKey);
		if (CollUtil.isEmpty(templateProperties)) {
			return;
		}

		// 剔除无用属性
		List<TemplatePropertyDTO> list = templateProperties.stream().map(TemplatePropertyConverter.INSTANCE::poToDto)
				.collect(Collectors.toList());
		// 美化 json
		String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(list);
		// 输出内容
		response.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));
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