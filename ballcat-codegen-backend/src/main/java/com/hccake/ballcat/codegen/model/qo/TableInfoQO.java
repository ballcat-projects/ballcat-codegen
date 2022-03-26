package com.hccake.ballcat.codegen.model.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springdoc.api.annotations.ParameterObject;

/**
 * @author Hccake
 * @version 1.0
 * @date 2020/6/17 15:21
 */
@Data
@Schema(title = "表信息查询对象")
@ParameterObject
public class TableInfoQO {

	/**
	 * 表名
	 */
	private String tableName;

}
