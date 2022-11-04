package com.hccake.ballcat.codegen.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author hccake
 */
@Data
@Schema(title = "模板分组选择数据属性")
public class TemplateGroupSelectDataAttributes {

	/**
	 * 图标（Base64）
	 */
	@Schema(title = "图标（Base64）")
	private String icon;

	/**
	 * 备注
	 */
	@Schema(title = "备注")
	private String remarks;

}
