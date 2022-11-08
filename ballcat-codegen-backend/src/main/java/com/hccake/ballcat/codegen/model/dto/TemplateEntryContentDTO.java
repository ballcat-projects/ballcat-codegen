package com.hccake.ballcat.codegen.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Hccake 2021/3/24
 * @version 1.0
 */
@Data
@Schema(title = "模板文件内容DTO")
public class TemplateEntryContentDTO {

	@Schema(title = "目录项ID")
	private String id;

	/**
	 * 模板内容
	 */
	@Schema(title = "模板内容")
	private String templateContent;

}
