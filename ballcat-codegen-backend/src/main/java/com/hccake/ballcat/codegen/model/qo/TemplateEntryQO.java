package com.hccake.ballcat.codegen.model.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springdoc.api.annotations.ParameterObject;

/**
 * 模板文件目录项 查询对象
 *
 * @author hccake
 * @date 2020-06-19 19:11:41
 */
@Data
@Schema(title = "模板文件目录项查询对象")
@ParameterObject
public class TemplateEntryQO {

	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	@Schema(title = "ID")
	private String id;

}