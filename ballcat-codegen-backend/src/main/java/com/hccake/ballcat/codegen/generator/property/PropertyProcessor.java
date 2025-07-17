package com.hccake.ballcat.codegen.generator.property;

import java.util.Map;

/**
 * 属性处理器接口 定义统一的属性处理规范
 *
 * @author hccake
 */
public interface PropertyProcessor {

	/**
	 * 处理属性并返回属性Map
	 * @param context 生成上下文
	 * @return 属性Map
	 */
	Map<String, Object> processProperties(PropertyProcessContext context);

	/**
	 * 获取处理器名称
	 * @return 处理器名称
	 */
	String getProcessorName();

	/**
	 * 获取处理优先级，数字越小优先级越高 系统属性(1) -> 表属性(2) -> 配置属性(3) -> 计算属性(4)
	 * @return 优先级
	 */
	int getPriority();

}