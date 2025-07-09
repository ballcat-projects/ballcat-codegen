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
	 * 普通属性
	 **/
	NORMAL(1, "普通属性"),

	/**
	 * 计算属性。
	 */
	COMPUTED_SPEL(2, "计算属性，使用SPEL表达式");

	private final Integer value;

	private final String desc;

}
