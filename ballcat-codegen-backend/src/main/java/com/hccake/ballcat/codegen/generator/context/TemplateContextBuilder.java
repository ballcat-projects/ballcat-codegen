package com.hccake.ballcat.codegen.generator.context;

import com.hccake.ballcat.codegen.generator.property.PropertyProcessContext;
import com.hccake.ballcat.codegen.generator.property.PropertyProcessor;
import com.hccake.ballcat.codegen.model.bo.TableDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 模板上下文构建器 负责协调统一的属性处理器构建模板渲染所需的上下文数据
 *
 * 统一的属性处理架构： 1. 系统属性 - SystemPropertyProcessor (优先级1: currentTime等) 2. 表属性 -
 * TablePropertyProcessor (优先级2: 表相关信息) 3. 配置属性 - ConfigPropertyProcessor (优先级3: 用户自定义属性)
 * 4. 计算属性 - ComputedPropertyProcessor (优先级4: 基于前三种属性计算得出)
 *
 * @author hccake
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TemplateContextBuilder {

	private final List<PropertyProcessor> propertyProcessors;

	/**
	 * 构建模板渲染上下文
	 */
	public Map<String, Object> buildTemplateContext(CodeGenerationContext context, TableDetails tableDetails) {
		Map<String, Object> templateContext = new HashMap<>();

		// 构建属性处理上下文
		PropertyProcessContext processContext = PropertyProcessContext.builder()
			.codeGenerationContext(context)
			.tableDetails(tableDetails)
			.processedProperties(templateContext)
			.build();

		// 按优先级排序处理器
		List<PropertyProcessor> sortedProcessors = propertyProcessors.stream()
			.sorted(Comparator.comparingInt(PropertyProcessor::getPriority))
			.collect(Collectors.toList());

		// 依次执行各个属性处理器
		for (PropertyProcessor processor : sortedProcessors) {
			try {
				log.debug("开始执行属性处理器: {}", processor.getProcessorName());

				// 更新处理上下文中的已处理属性
				processContext.setProcessedProperties(templateContext);

				// 处理属性
				Map<String, Object> properties = processor.processProperties(processContext);

				// 合并到模板上下文中
				templateContext.putAll(properties);

				log.debug("属性处理器 {} 执行完成，新增属性数量: {}", processor.getProcessorName(), properties.size());

			}
			catch (Exception e) {
				log.error("属性处理器 {} 执行失败: {}", processor.getProcessorName(), e.getMessage(), e);
				// 继续执行其他处理器，不因为一个处理器失败而中断整个流程
			}
		}

		log.info("模板上下文构建完成，属性总数: {}, 处理器数量: {}", templateContext.size(), sortedProcessors.size());
		return templateContext;
	}

}