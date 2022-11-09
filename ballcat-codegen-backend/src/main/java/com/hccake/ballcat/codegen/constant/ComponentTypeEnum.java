package com.hccake.ballcat.codegen.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 组件类型
 *
 * @author hccake
 * @since 1.4.0
 */

@Getter
@RequiredArgsConstructor
public enum ComponentTypeEnum {

	/**
	 * 组件类型
	 **/
	INPUT("input", "输入框"), INPUT_NUMBER("input-number", "数字输入框"), SELECT("select", "选择器"), RADIO("radio", "单选框");

	private final String value;

	private final String desc;

}
