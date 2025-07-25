package com.hccake.ballcat.codegen.generator.property;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.hccake.ballcat.codegen.engine.TemplateEngineDelegator;
import com.hccake.ballcat.codegen.engine.TemplateEngineTypeEnum;
import com.hccake.ballcat.codegen.model.entity.TemplateProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 计算属性处理器 负责处理模板中的计算属性，使用安全的模板引擎替代SpEL
 *
 * @author hccake
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ComputedPropertyProcessor implements PropertyProcessor {

	private final TemplateEngineDelegator templateEngineDelegator;

	@Override
	public Map<String, Object> processProperties(PropertyProcessContext context) {
		List<TemplateProperty> computedProperties = context.getComputedProperties();
		Map<String, Object> processedProperties = context.getProcessedProperties();

		if (CollUtil.isEmpty(computedProperties)) {
			log.debug("无计算属性需要处理");
			return new HashMap<>();
		}

		Map<String, Object> computedPropertiesMap = new HashMap<>();

		for (TemplateProperty computedProperty : computedProperties) {
			try {
				String expression = computedProperty.getExpression();
				if (StrUtil.isBlank(expression)) {
					continue;
				}

				// 获取模板引擎类型，如果未指定则使用Velocity作为默认值
				Integer engineTypeValue = computedProperty.getEngineType();
				TemplateEngineTypeEnum engineType = engineTypeValue != null ? TemplateEngineTypeEnum.of(engineTypeValue)
						: TemplateEngineTypeEnum.VELOCITY;

				// 如果转换失败，使用默认的Velocity引擎
				if (engineType == null) {
					engineType = TemplateEngineTypeEnum.VELOCITY;
				}

				String renderedValue = templateEngineDelegator.render(engineType, expression, processedProperties);

				// 尝试转换为合适的数据类型
				Object value = convertToAppropriateType(renderedValue);
				computedPropertiesMap.put(computedProperty.getPropKey(), value);

				log.debug("计算属性处理成功: {} = {}", computedProperty.getPropKey(), value);
			}
			catch (Exception e) {
				log.warn("计算属性处理失败: {}, 表达式: {}, 错误: {}", computedProperty.getPropKey(),
						computedProperty.getExpression(), e.getMessage());
				// 处理失败时设置为空字符串，避免模板渲染出错
				computedPropertiesMap.put(computedProperty.getPropKey(), "");
			}
		}

		log.debug("计算属性处理完成，属性数量: {}", computedPropertiesMap.size());
		return computedPropertiesMap;
	}

	@Override
	public String getProcessorName() {
		return "ComputedPropertyProcessor";
	}

	@Override
	public int getPriority() {
		return 4; // 最低优先级，需要基于前面的属性进行计算
	}

	/**
	 * 将字符串转换为合适的数据类型
	 */
	private Object convertToAppropriateType(String value) {
		if (StrUtil.isBlank(value)) {
			return "";
		}

		String trimmedValue = value.trim();

		// 尝试转换为数字
		if (trimmedValue.matches("^-?\\d+$")) {
			try {
				return Long.parseLong(trimmedValue);
			}
			catch (NumberFormatException e) {
				// 忽略转换失败，返回原字符串
			}
		}

		// 尝试转换为小数
		if (trimmedValue.matches("^-?\\d+\\.\\d+$")) {
			try {
				return Double.parseDouble(trimmedValue);
			}
			catch (NumberFormatException e) {
				// 忽略转换失败，返回原字符串
			}
		}

		// 尝试转换为布尔值
		if ("true".equalsIgnoreCase(trimmedValue) || "false".equalsIgnoreCase(trimmedValue)) {
			return Boolean.parseBoolean(trimmedValue);
		}

		// 默认返回字符串
		return trimmedValue;
	}

}