package com.hccake.ballcat.codegen.model.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 数据源 查询对象
 *
 * @author hccake
 * @date 2020-06-17 10:24:47
 */
@Data
@Schema(title = "数据源查询对象")
public class DataSourceConfigQO {

	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@Schema(title = "ID")
	private Integer id;

}
