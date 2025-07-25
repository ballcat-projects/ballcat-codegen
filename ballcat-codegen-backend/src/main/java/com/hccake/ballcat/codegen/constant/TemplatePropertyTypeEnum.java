package com.hccake.ballcat.codegen.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 属性类型。
 *
 * @author hccake
 * @since 1.5.0
 */
@Getter
@RequiredArgsConstructor
public enum TemplatePropertyTypeEnum {

	/**
	 * 配置属性。
	 **/
	CONFIG(1, "配置属性"),

	/**
	 * 计算属性。
	 */
	COMPUTED(2, "计算属性，支持多种模板引擎");

	private final Integer value;

	private final String desc;

}
