package com.hccake.ballcat.codegen.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 模板属性配置
 *
 * @author hccake 2020-06-22 15:46:39
 */
@Data
@Schema(title = "模板属性配置")
public class TemplatePropertyDTO {

	private static final long serialVersionUID = 1L;

	/**
	 * 标题
	 */
	@Schema(title = "标题")
	private String title;

	/**
	 * 属性键
	 */
	@Schema(title = "属性键")
	private String propKey;

	/**
	 * 默认值(可为空值)
	 */
	@Schema(title = "默认值")
	private String defaultValue;

	/**
	 * 必填，1：是，0：否
	 */
	@Schema(title = "必填，1：是，0：否")
	private Integer required;

	/**
	 * 备注信息
	 */
	@Schema(title = "备注信息")
	private String remarks;

}
