package com.hccake.ballcat.codegen.controller;

import com.hccake.ballcat.codegen.converter.TypeScriptTypeConverter;
import com.hccake.ballcat.codegen.model.dto.TypeScriptTypeDTO;
import com.hccake.ballcat.codegen.model.qo.TypeScriptTypeQO;
import com.hccake.ballcat.codegen.model.vo.TypeScriptTypeVO;
import com.hccake.ballcat.codegen.service.TypeScriptTypeService;
import com.hccake.ballcat.common.model.domain.PageParam;
import com.hccake.ballcat.common.model.domain.PageResult;
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

import java.util.Arrays;

/**
 * software：IntelliJ IDEA 2022.2
 * class name: TypeScriptTypeController
 * description：前端和后端数据类型管理 the controller
 *
 * @author MoBaiJun 2022-07-01 14:27:10
 */
@Tag(name = "前端和后端数据类型管理管理")
@RestController
@RequiredArgsConstructor
@RequestMapping("/codegen/type-script-type")
public class TypeScriptTypeController {

	private final TypeScriptTypeService typeScriptTypeService;

	@Operation(summary = "分页查询")
	@GetMapping("/page")
	public R<PageResult<TypeScriptTypeVO>> getDataSourceConfigPage(@Validated PageParam pageParam, TypeScriptTypeQO qo) {
		return R.ok(typeScriptTypeService.queryPage(pageParam, qo));
	}

	@Operation(summary = "通过id查询")
	@GetMapping("/{id}")
	public R<TypeScriptTypeVO> getById(@PathVariable("id") Long id) {
		return R.ok(TypeScriptTypeConverter.INSTANCE.toVo(typeScriptTypeService.getById(id)));
	}

	@Operation(summary = "新增数据类型")
	@PostMapping
	public R<Void> save(@RequestBody TypeScriptTypeDTO dto) {
		return typeScriptTypeService.save(TypeScriptTypeConverter.INSTANCE.toDto(dto)) ? R.ok()
				: R.failed(BaseResultCode.UPDATE_DATABASE_ERROR, "新增数据类型失败");
	}

	@Operation(summary = "修改数据类型")
	@PutMapping
	public R<Void> updateById(@RequestBody TypeScriptTypeDTO dto) {
		return typeScriptTypeService.updateById(TypeScriptTypeConverter.INSTANCE.toDto(dto)) ? R.ok()
				: R.failed(BaseResultCode.UPDATE_DATABASE_ERROR, "修改数据源失败");
	}

	@Operation(summary = "通过id批量删除数据类型")
	@DeleteMapping("/{ids}")
	public R<Void> bachRemoveById(@PathVariable Long[] ids) {
		return typeScriptTypeService.removeBatchByIds(Arrays.asList(ids)) ? R.ok()
				: R.failed(BaseResultCode.UPDATE_DATABASE_ERROR, "通过id删除数据类型失败");
	}
}