package com.hccake.ballcat.codegen.model.bo;

import lombok.Data;

/**
 * 模板生成的文件项
 *
 * @author hccake
 */
@Data
public class FileEntry {

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
	 * 类型 1：文件夹 2：文件
	 */
	private Integer type;

	/**
	 * 文件内容
	 */
	private String content;

}
