package com.hccake.ballcat.codegen.model.bo;

import com.hccake.ballcat.codegen.constant.TemplateEntryTypeEnum;
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
	 * ID
	 */
	@Schema(title = "ID")
	private String id;

	/**
	 * 文件名称
	 */
	@Schema(title = "文件名称")
	private String filename;

	/**
	 * 父级文件路径
	 */
	@Schema(title = "父级文件路径")
	private String parentFilePath;

	/**
	 * 类型
	 */
	private TemplateEntryTypeEnum type;

	/**
	 * 文件内容
	 */
	@Schema(title = "文件内容")
	private byte[] fileContent;

	/**
	 * 模板引擎类型 1：velocity
	 */
	@Schema(title = "模板引擎类型 1：velocity")
	private Integer engineType;

}
