package com.hccake.ballcat.codegen.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 模板文件目录项
 *
 * @author hccake
 * @date 2020-06-19 19:11:41
 */
@Data
@Schema(title = "模板文件目录项")
public class TemplateEntryUpdateDTO {

	@Schema(title = "ID")
	private String id;

	/**
	 * 文件夹全路径/模板文件名称（支持占位符）
	 */
	@Schema(title = "文件夹路径/模板文件名称（支持占位符）")
	private String filename;

	/**
	 * 文件类型 1：文件夹 2：模板文件
	 */
	@Schema(title = "文件类型 1：文件夹 2：模板文件")
	private Integer type;

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
