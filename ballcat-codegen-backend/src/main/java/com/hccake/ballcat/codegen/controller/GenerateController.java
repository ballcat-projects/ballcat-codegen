package com.hccake.ballcat.codegen.controller;

import cn.hutool.core.io.IoUtil;
import com.hccake.ballcat.codegen.model.dto.GeneratorOptionDTO;
import com.hccake.ballcat.codegen.model.qo.TableInfoQO;
import com.hccake.ballcat.codegen.model.vo.TableInfo;
import com.hccake.ballcat.codegen.model.vo.TemplateEntryTree;
import com.hccake.ballcat.codegen.service.GeneratorService;
import com.hccake.ballcat.codegen.service.TableInfoService;
import com.hccake.ballcat.common.model.domain.PageParam;
import com.hccake.ballcat.common.model.domain.PageResult;
import com.hccake.ballcat.common.model.result.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 代码生成器
 *
 * @author hccake
 * @date 2018-07-30
 */
@CrossOrigin
@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "代码生成")
public class GenerateController {

	private final GeneratorService generatorService;

	private final TableInfoService tableInfoService;

	/**
	 * 表信息分页查询
	 * @param pageParam 分页参数
	 * @param tableInfoQO 表信息查询对象
	 * @return R
	 */
	@Operation(summary = "表信息分页查询")
	@GetMapping("/table-info/page")
	public R<PageResult<TableInfo>> getDataSourceConfigPage(PageParam pageParam, TableInfoQO tableInfoQO) {
		return R.ok(tableInfoService.queryPage(pageParam, tableInfoQO));
	}

	/**
	 * 生成代码
	 */
	@Operation(summary = "生成代码")
	@PostMapping("/generate")
	public void generateCode(@RequestBody GeneratorOptionDTO generatorOptionDTO, HttpServletResponse response)
			throws IOException {
		byte[] data = generatorService.generatorCode(generatorOptionDTO);
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
	public R<List<TemplateEntryTree>> previewCode(@RequestBody GeneratorOptionDTO preGenerateOptionDTO) {
		return R.ok(generatorService.previewCode(preGenerateOptionDTO));
	}

}
