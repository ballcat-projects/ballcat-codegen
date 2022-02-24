package com.hccake.ballcat.codegen.model.vo;

import com.hccake.ballcat.common.util.tree.SimpleTreeNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Hccake
 * @version 1.0
 * @date 2020/6/21 17:59
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TemplateEntryTree extends SimpleTreeNode<Integer> {

	/**
	 * 模板组Id
	 */
	@Schema(title = "模板组Id")
	private Integer groupId;

	/**
	 * 文件夹全路径/模板文件名称（支持占位符）
	 */
	@Schema(title = "文件夹全路径/模板文件名称（支持占位符）")
	private String fileName;

	/**
	 * 文件类型 1：文件夹 2：模板文件
	 */
	@Schema(title = "文件类型 1：文件夹 2：模板文件")
	private Integer type;

	/**
	 * 文件内容
	 */
	@Schema(title = "文件内容")
	private String content;

	/**
	 * 模板引擎类型 1：velocity
	 */
	@Schema(title = "模板引擎类型 1：velocity")
	private Integer engineType;

}
