package com.hccake.ballcat.codegen.model.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springdoc.api.annotations.ParameterObject;

/**
 * 模板组 查询对象
 *
 * @author hccake 2020-06-19 19:11:41
 */
@Data
@Schema(title = "模板组查询对象")
@ParameterObject
public class TemplateGroupQO {

	private static final long serialVersionUID = 1L;

	/**
	 * 模板组名称
	 */
	@Schema(title = "模板组名称")
	private String name;

}