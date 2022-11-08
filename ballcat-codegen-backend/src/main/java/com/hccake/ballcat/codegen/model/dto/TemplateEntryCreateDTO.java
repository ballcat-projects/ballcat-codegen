package com.hccake.ballcat.codegen.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 模板文件目录项
 *
 * @author hccake 2020-06-19 19:11:41
 */
@Data
@Schema(title = "模板文件目录项")
public class TemplateEntryCreateDTO {

	private static final long serialVersionUID = 1L;

	/**
	 * 模板组标识
	 */
	@NotNull(message = "模板组标识不允许为空")
	@Schema(title = "模板组标识")
	private String groupKey;

	/**
	 * 文件夹全路径/模板文件名称（支持占位符）
	 */
	@NotNull(message = "文件名不允许为空")
	@Schema(title = "文件夹路径/模板文件名称（支持占位符）")
	private String filename;

	/**
	 * 文件类型
	 * @see com.hccake.ballcat.codegen.constant.TemplateEntryTypeEnum
	 */
	@NotNull(message = "文件类型不允许为空")
	@Schema(title = "文件类型")
	private Integer type;

	/**
	 * 父级Id
	 */
	@Schema(title = "父级Id")
	private String parentId;

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
