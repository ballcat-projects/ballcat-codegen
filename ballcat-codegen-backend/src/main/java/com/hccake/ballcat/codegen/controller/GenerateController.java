package com.hccake.ballcat.codegen.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import com.hccake.ballcat.codegen.constant.TemplateEntryTypeEnum;
import com.hccake.ballcat.codegen.converter.TemplateModelConverter;
import com.hccake.ballcat.codegen.model.bo.FileEntry;
import com.hccake.ballcat.codegen.model.bo.TableInfo;
import com.hccake.ballcat.codegen.model.dto.GeneratorOptionDTO;
import com.hccake.ballcat.codegen.model.qo.TableInfoQO;
import com.hccake.ballcat.codegen.model.vo.GeneratePreviewFileVO;
import com.hccake.ballcat.codegen.service.GeneratorService;
import com.hccake.ballcat.codegen.service.TableInfoQuery;
import com.hccake.ballcat.common.model.domain.PageParam;
import com.hccake.ballcat.common.model.domain.PageResult;
import com.hccake.ballcat.common.model.result.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 代码生成器
 *
 * @author hccake 2018-07-30
 */
@CrossOrigin
@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "代码生成")
public class GenerateController {

	private final GeneratorService generatorService;

	private final TableInfoQuery tableInfoQuery;

	/**
	 * 表信息分页查询
	 * @param pageParam 分页参数
	 * @param tableInfoQO 表信息查询对象
	 * @return R
	 */
	@Operation(summary = "表信息分页查询")
	@GetMapping("/table-info/page")
	public R<PageResult<TableInfo>> getDataSourceConfigPage(@Validated PageParam pageParam, TableInfoQO tableInfoQO) {
		return R.ok(tableInfoQuery.queryPage(pageParam, tableInfoQO));
	}

	/**
	 * 生成代码
	 */
	@Operation(summary = "生成代码")
	@PostMapping("/generate")
	public void generateCode(@RequestBody GeneratorOptionDTO generatorOptionDTO, HttpServletResponse response)
			throws IOException {
		// 使用统一的生成方法
		List<FileEntry> fileEntries = generatorService.generateFileEntries(generatorOptionDTO);

		// 打包成ZIP文件
		byte[] data = packagesToZip(fileEntries);

		response.reset();
		response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"ballcat.zip\"");
		response.addHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(data.length));
		response.setContentType("application/octet-stream; charset=UTF-8");

		IoUtil.write(response.getOutputStream(), Boolean.TRUE, data);
	}

	/**
	 * 生成预览代码
	 * @param preGenerateOptionDTO 预览
	 * @return R<List<TemplateDirectory>>
	 */
	@Operation(summary = "生成预览代码")
	@PostMapping("/preview")
	public R<List<GeneratePreviewFileVO>> previewCode(@RequestBody GeneratorOptionDTO preGenerateOptionDTO) {
		// 使用统一的生成方法
		List<FileEntry> fileEntries = generatorService.generateFileEntries(preGenerateOptionDTO);

		if (CollUtil.isEmpty(fileEntries)) {
			return R.ok();
		}

		// 转换为 VO，并按文件名排序（忽略大小写）
		List<GeneratePreviewFileVO> list = fileEntries.stream()
			.sorted(Comparator.comparing(FileEntry::getFilename, String.CASE_INSENSITIVE_ORDER))
			.map(TemplateModelConverter.INSTANCE::fileEntryToPreviewVo)
			.collect(Collectors.toList());
		return R.ok(list);
	}

	/**
	 * 将文件列表打包成ZIP
	 * @param fileEntries 文件列表
	 * @return ZIP字节数组
	 * @throws IOException IO异常
	 */
	private byte[] packagesToZip(List<FileEntry> fileEntries) throws IOException {
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				ZipOutputStream zip = new ZipOutputStream(outputStream)) {

			for (FileEntry fileEntry : fileEntries) {
				// 只处理文件，跳过文件夹
				if (!TemplateEntryTypeEnum.FOLDER.equals(fileEntry.getType())) {
					zip.putNextEntry(new ZipEntry(fileEntry.getFilePath()));
					zip.write(fileEntry.getFileContent());
					zip.closeEntry();
				}
			}

			zip.finish();
			return outputStream.toByteArray();
		}
	}

}
