package com.hccake.ballcat.codegen.model.vo;

import com.hccake.ballcat.codegen.constant.TemplateEntryTypeEnum;
import com.hccake.ballcat.common.util.tree.TreeNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author Hccake 2020/6/21 17:59
 */
@Data
public class TemplateEntryTree implements TreeNode<String> {

	private String id;

	private String parentId;

	private List<TemplateEntryTree> children;

	/**
	 * 模板组标识
	 */
	@Schema(title = "模板组标识")
	private String groupKey;

	/**
	 * 文件夹全路径/模板文件名称（支持占位符）
	 */
	@Schema(title = "文件夹全路径/模板文件名称（支持占位符）")
	private String filename;

	/**
	 * 文件类型 1：文件夹 2：模板文件
	 */
	@Schema(title = "文件类型 1：文件夹 2：模板文件")
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

	@Override
	public String getKey() {
		return this.id;
	}

	@Override
	public String getParentKey() {
		return this.parentId;
	}

	@Override
	public <T extends TreeNode<String>> void setChildren(List<T> list) {
		this.children = (List<TemplateEntryTree>) list;
	}

}
