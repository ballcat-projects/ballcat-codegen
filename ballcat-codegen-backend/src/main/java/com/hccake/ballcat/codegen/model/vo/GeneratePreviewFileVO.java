package com.hccake.ballcat.codegen.model.vo;

import com.hccake.ballcat.codegen.constant.TemplateEntryTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 代码生成预览使用的的文件项
 *
 * @author hccake
 */
@Data
public class GeneratePreviewFileVO {

	/**
	 * ID
	 */
	@Schema(title = "ID")
	private String id;

	/**
	 * 文件名
	 */
	private String filename;

	/**
	 * 完全文件路径
	 */
	private String filePath;

	/**
	 * 父级的完全文件路径
	 */
	private String parentFilePath;

	/**
	 * 类型 1：文件夹 2：模板文件 3. 二进制文件
	 * @see TemplateEntryTypeEnum
	 */
	private Integer type;

	/**
	 * 模板文件内容
	 */
	@Schema(title = "模板文件内容")
	private String templateContent;

}
