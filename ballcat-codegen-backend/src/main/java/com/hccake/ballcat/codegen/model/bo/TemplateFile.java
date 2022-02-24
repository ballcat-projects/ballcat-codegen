package com.hccake.ballcat.codegen.model.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 模板信息
 *
 * @author hccake
 * @date 2020-06-19 18:09:08
 */
@Accessors(chain = true)
@Data
@Schema(title = "模板文件")
public class TemplateFile {

	private static final long serialVersionUID = 1L;

	/**
	 * 文件名称
	 */
	@Schema(title = "文件名称")
	private String fileName;

	/**
	 * 父级文件路径
	 */
	@Schema(title = "父级文件路径")
	private String parentFilePath;

	/**
	 * 类型 1：文件夹 2：文件
	 */
	private Integer type;

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

}
