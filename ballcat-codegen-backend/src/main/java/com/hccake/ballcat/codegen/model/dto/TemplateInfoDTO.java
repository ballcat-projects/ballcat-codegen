package com.hccake.ballcat.codegen.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 模板信息
 *
 * @author hccake
 * @date 2020-06-19 18:09:08
 */
@Data
@Schema(title = "模板信息")
public class TemplateInfoDTO {

	private static final long serialVersionUID = 1L;

	/**
	 * 模板名称
	 */
	@Schema(title = "模板标题")
	private String title;

	/**
	 * 模板内容
	 */
	@Schema(title = "模板内容")
	private String content;

	/**
	 * 模板引擎类型 1：velocity
	 */
	@Schema(title = "模板引擎类型 1：velocity")
	private Integer engineType;

	/**
	 * 备注
	 */
	@Schema(title = "备注")
	private String remarks;

}
