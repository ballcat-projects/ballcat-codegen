package com.hccake.ballcat.codegen.controller;

import com.hccake.ballcat.codegen.model.dto.DataSourceConfigDTO;
import com.hccake.ballcat.codegen.model.entity.DataSourceConfig;
import com.hccake.ballcat.codegen.model.qo.DataSourceConfigQO;
import com.hccake.ballcat.codegen.model.vo.DataSourceConfigPageVO;
import com.hccake.ballcat.codegen.service.DataSourceConfigService;
import com.hccake.ballcat.common.model.domain.PageParam;
import com.hccake.ballcat.common.model.domain.PageResult;
import com.hccake.ballcat.common.model.domain.SelectData;
import com.hccake.ballcat.common.model.result.BaseResultCode;
import com.hccake.ballcat.common.model.result.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
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

/**
 * 数据源
 *
 * @author hccake 2020-06-17 10:24:47
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/gen/datasource-config")
@Tag(name = "数据源管理")
public class DataSourceConfigController {

	private final DataSourceConfigService dataSourceConfigService;

	/**
	 * 分页查询
	 * @param pageParam 分页对象
	 * @param dataSourceConfigQO 数据源
	 * @return R
	 */
	@Operation(summary = "分页查询")
	@GetMapping("/page")
	public R<PageResult<DataSourceConfigPageVO>> getDataSourceConfigPage(@Validated PageParam pageParam,
			DataSourceConfigQO dataSourceConfigQO) {
		return R.ok(dataSourceConfigService.queryPage(pageParam, dataSourceConfigQO));
	}

	/**
	 * 通过id查询数据源
	 * @param id id
	 * @return R
	 */
	@Operation(summary = "通过id查询")
	@GetMapping("/{id}")
	public R<DataSourceConfig> getById(@PathVariable("id") Integer id) {
		return R.ok(dataSourceConfigService.getById(id));
	}

	/**
	 * 新增数据源
	 * @param dataSourceConfigDTO 数据源
	 * @return R
	 */
	@Operation(summary = "新增数据源")
	@PostMapping
	public R<Void> save(@RequestBody DataSourceConfigDTO dataSourceConfigDTO) {
		return dataSourceConfigService.save(dataSourceConfigDTO) ? R.ok()
				: R.failed(BaseResultCode.UPDATE_DATABASE_ERROR, "新增数据源失败");
	}

	/**
	 * 修改数据源
	 * @param dataSourceConfigDTO 数据源
	 * @return R
	 */
	@Operation(summary = "修改数据源")
	@PutMapping
	public R<Void> updateById(@RequestBody DataSourceConfigDTO dataSourceConfigDTO) {
		return dataSourceConfigService.update(dataSourceConfigDTO) ? R.ok()
				: R.failed(BaseResultCode.UPDATE_DATABASE_ERROR, "修改数据源失败");
	}

	/**
	 * 通过id删除数据源
	 * @param id id
	 * @return R
	 */
	@Operation(summary = "通过id删除数据源")
	@DeleteMapping("/{id}")
	public R<Void> removeById(@PathVariable Integer id) {
		return dataSourceConfigService.removeById(id) ? R.ok()
				: R.failed(BaseResultCode.UPDATE_DATABASE_ERROR, "通过id删除数据源失败");
	}

	/**
	 * 获取selectData数据
	 * @return R
	 */
	@Operation(summary = "获取selectData数据")
	@GetMapping("/select")
	public R<List<SelectData<Void>>> listSelectData() {
		List<SelectData<Void>> selectDataList = dataSourceConfigService.listSelectData();
		return R.ok(selectDataList);
	}

}
