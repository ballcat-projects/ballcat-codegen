package com.hccake.ballcat.codegen.model.bo;

import com.hccake.ballcat.common.util.tree.TreeNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author Hccake 2020/6/21 17:59
 */
@Data
public class TemplateEntryFileTree implements TreeNode<String> {

	@Schema(title = "文件全路径")
	private String path;

	@Schema(title = "父级文件路径")
	private String parentPath;

	@Schema(title = "子文件列表")
	private List<TemplateEntryFileTree> children;

	@Schema(title = "ID")
	private String id;

	/**
	 * 父级Id
	 */
	@Schema(title = "父级Id")
	private String parentId;

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
	private Integer type;

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
		return this.path;
	}

	@Override
	public String getParentKey() {
		return this.parentPath;
	}

	@Override
	public <T extends TreeNode<String>> void setChildren(List<T> children) {
		this.children = (List<TemplateEntryFileTree>) children;
	}

}
