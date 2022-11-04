package com.hccake.ballcat.codegen.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author hccake
 */
@Data
public class TemplateGroupSelectDataAttributes {

	/**
	 * 图标（Base64）
	 */
	@Schema(title = "图标（Base64）")
	private String icon;

}
