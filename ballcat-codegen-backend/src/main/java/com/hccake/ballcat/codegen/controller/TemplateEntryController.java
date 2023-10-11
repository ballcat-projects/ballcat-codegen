package com.hccake.ballcat.codegen.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import com.hccake.ballcat.codegen.constant.TemplateEntryConstants;
import com.hccake.ballcat.codegen.constant.TemplateEntryTypeEnum;
import com.hccake.ballcat.codegen.converter.TemplateModelConverter;
import com.hccake.ballcat.codegen.engine.TemplateEngineTypeEnum;
import com.hccake.ballcat.codegen.model.bo.TemplateEntryFileTree;
import com.hccake.ballcat.codegen.model.bo.TemplateFile;
import com.hccake.ballcat.codegen.model.dto.TemplateEntryCreateDTO;
import com.hccake.ballcat.codegen.model.dto.TemplateEntryUpdateDTO;
import com.hccake.ballcat.codegen.model.entity.TemplateEntry;
import com.hccake.ballcat.codegen.model.vo.TemplateEntryVO;
import com.hccake.ballcat.codegen.service.TemplateEntryService;
import com.hccake.ballcat.codegen.util.GenerateUtils;
import com.hccake.ballcat.common.model.result.BaseResultCode;
import com.hccake.ballcat.common.model.result.R;
import com.hccake.ballcat.common.util.tree.TreeUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * 模板文件目录项
 *
 * @author hccake 2020-06-19 19:11:41
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/gen/template-entry")
@Tag(name = "模板文件目录项管理")
public class TemplateEntryController {

	private final TemplateEntryService templateEntryService;

	/**
	 * 模板组的文件目录
	 * @param groupKey 模板组标识
	 * @return R
	 */
	@Operation(summary = "指定模板组的文件目录项")
	@GetMapping("/list/{groupKey}")
	public R<List<TemplateEntryVO>> getTemplateDirectoryEntryPage(@PathVariable String groupKey) {
		List<TemplateEntry> entries = templateEntryService.listByGroupKey(groupKey);
		List<TemplateEntryVO> vos = entries.stream()
			.map(TemplateModelConverter.INSTANCE::entryPoToVo)
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
	public R<Void> move(@PathVariable String entryId, @RequestParam boolean horizontalMove,
			@RequestParam String targetEntryId) {
		return templateEntryService.move(horizontalMove, entryId, targetEntryId) ? R.ok()
				: R.failed(BaseResultCode.UPDATE_DATABASE_ERROR, "移动目录项失败");
	}

	/**
	 * 新增模板目录项
	 * @param templateEntryCreateDTO 模板目录项
	 * @return R
	 */
	@Operation(summary = "新增模板目录项")
	@PostMapping
	public R<String> save(@RequestPart("templateEntry") TemplateEntryCreateDTO templateEntryCreateDTO,
			@RequestPart(value = "file", required = false) MultipartFile file) throws IOException {
		String entryId = templateEntryService.createEntry(templateEntryCreateDTO, file);
		return entryId != null ? R.ok(entryId) : R.failed(BaseResultCode.UPDATE_DATABASE_ERROR, "新增模板目录项失败");
	}

	/**
	 * 修改目录项
	 * @param templateEntryUpdateDTO 模板目录项
	 * @return R
	 */
	@Operation(summary = "修改目录项")
	@PutMapping
	public R<Void> updateEntry(@RequestPart("templateEntry") TemplateEntryUpdateDTO templateEntryUpdateDTO,
			@RequestPart(value = "file", required = false) MultipartFile file) throws IOException {
		return templateEntryService.updateEntry(templateEntryUpdateDTO, file) ? R.ok()
				: R.failed(BaseResultCode.UPDATE_DATABASE_ERROR, "修改目录项失败");
	}

	/**
	 * 通过id删除模板文件目录项
	 * @param id id
	 * @param mode 删除模式， 1：只删除本身，将子节点上移 2. 删除自身及其所有子节点
	 * @return R
	 */
	@Operation(summary = "通过id删除模板文件目录项")
	@DeleteMapping("/{id}")
	public R<Void> removeById(@PathVariable String id, @RequestParam Integer mode) {
		return templateEntryService.removeEntry(id, mode) ? R.ok()
				: R.failed(BaseResultCode.UPDATE_DATABASE_ERROR, "通过id删除模板文件目录项失败");
	}

	/**
	 * 修改模板目录项内容
	 * @param id 模板项id
	 * @param content 模板内容
	 * @return R
	 */
	@Operation(summary = "修改模板目录项内容")
	@PatchMapping("/content")
	public R<Void> updateContent(@RequestParam("id") String id, @RequestParam("content") String content) {
		return templateEntryService.updateContent(id, content) ? R.ok()
				: R.failed(BaseResultCode.UPDATE_DATABASE_ERROR, "修改模板目录项内容失败");
	}

	/**
	 * 下载二进制文件
	 */
	@Operation(summary = "下载二进制文件")
	@GetMapping("/download/{id}")
	public void generateCode(@PathVariable String id, HttpServletResponse response) throws IOException {
		TemplateEntry templateEntry = templateEntryService.getById(id);
		byte[] fileContent = templateEntry.getFileContent();
		response.reset();
		response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + templateEntry.getFilename());
		response.addHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileContent.length));
		response.setContentType("application/octet-stream; charset=UTF-8");

		IoUtil.write(response.getOutputStream(), Boolean.TRUE, fileContent);
	}

	/**
	 * 导入模板组
	 * @param file 模板组压缩包
	 * @return R
	 */
	@Operation(summary = "导入模板")
	@PostMapping("/import")
	public R<Void> importTemplate(@RequestParam("groupKey") String groupKey, @RequestPart("file") MultipartFile file)
			throws IOException {
		ZipInputStream zis = new ZipInputStream(file.getInputStream());

		Map<String, TemplateEntryFileTree> map = new HashMap<>(32);

		ZipEntry ze;
		while ((ze = zis.getNextEntry()) != null) {
			String zipEntryName = ze.getName();
			Path path = Paths.get(zipEntryName);
			boolean isDirectory = ze.isDirectory();
			while (path != null) {
				String pathStr = path.toString();
				boolean finalIsDirectory = isDirectory;
				map.computeIfAbsent(pathStr, key -> createEntry(groupKey, zis, finalIsDirectory, pathStr));
				path = path.getParent();
				isDirectory = true;
			}
		}

		List<TemplateEntryFileTree> list = new ArrayList<>(map.values());
		List<TemplateEntryFileTree> treeNodeList = TreeUtils.buildTree(list, File.separator);
		List<TemplateEntry> templateEntries = new ArrayList<>();
		TreeUtils.forEachDFS(treeNodeList, null, (treeNode, parentTreeNode) -> {
			TemplateEntry templateEntry = TemplateModelConverter.INSTANCE.entryFileTreeToPo(treeNode);
			String parentId = parentTreeNode != null ? parentTreeNode.getId() : TemplateEntryConstants.TREE_ROOT_ID;
			templateEntry.setParentId(parentId);
			templateEntries.add(templateEntry);
		});
		templateEntryService.saveBatch(templateEntries);

		return R.ok();
	}

	private static TemplateEntryFileTree createEntry(String groupKey, ZipInputStream zis, boolean isDirectory,
			String pathStr) {
		int lastIndexOf = pathStr.lastIndexOf(File.separator);
		String filename = pathStr.substring(lastIndexOf + 1);
		String parentPathStr = lastIndexOf > 0 ? pathStr.substring(0, lastIndexOf) : File.separator;

		TemplateEntryFileTree entryTree = new TemplateEntryFileTree();
		entryTree.setGroupKey(groupKey);
		entryTree.setFilename(filename);
		entryTree.setPath(pathStr);
		entryTree.setParentPath(parentPathStr);
		if (isDirectory) {
			entryTree.setType(TemplateEntryTypeEnum.FOLDER.getType());
		}
		else {
			entryTree.setType(TemplateEntryTypeEnum.TEMPLATE_FILE.getType());
			// TODO 考虑文件上传时如何传递文件模板引擎类型的字段
			entryTree.setEngineType(TemplateEngineTypeEnum.VELOCITY.getType());
			entryTree.setFileContent(IoUtil.readBytes(zis, false));
		}
		// 生成一个 id
		entryTree.setId(IdUtil.getSnowflakeNextIdStr());
		return entryTree;
	}

	/**
	 * 导出模板组文件
	 * @param groupKey 模板组标识
	 */
	@Operation(summary = "导出模板")
	@GetMapping("/export")
	public void exportTemplate(@RequestParam("groupKey") String groupKey, HttpServletResponse response)
			throws IOException {
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
				String filePath = GenerateUtils.concatFilePath(templateFile.getParentFilePath(),
						templateFile.getFilename());
				TemplateEntryTypeEnum type = templateFile.getType();
				// 文件夹必须尾缀 “/”
				if (TemplateEntryTypeEnum.FOLDER.equals(type)) {
					filePath = filePath + "/";
				}
				ZipEntry zipEntry = new ZipEntry(filePath);
				zip.putNextEntry(zipEntry);
				// 文件需要额外写入内容
				if (TemplateEntryTypeEnum.TEMPLATE_FILE.equals(type)) {
					zip.write(templateFile.getFileContent());
				}
				zip.closeEntry();
			}

			// 手动结束 zip，防止文件末端未被写入
			zip.finish();
		}
	}

}