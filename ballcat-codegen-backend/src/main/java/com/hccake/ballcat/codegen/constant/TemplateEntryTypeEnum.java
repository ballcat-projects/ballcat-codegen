package com.hccake.ballcat.codegen.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Hccake
 * @version 1.0 2020/6/23 14:36 模板目录项类型
 */
@Getter
@RequiredArgsConstructor
public enum TemplateEntryTypeEnum {

	/**
	 * 文件夹
	 */
	FOLDER(1),
	/**
	 * 模板文件
	 */
	TEMPLATE_FILE(2),
	/**
	 * 二进制文件
	 */
	BINARY_FILE(3);

	private final Integer type;

}
