package com.hccake.ballcat.codegen.generator.property;

import cn.hutool.core.collection.CollUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 配置属性处理器 负责处理用户自定义的配置属性
 *
 * @author hccake
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ConfigPropertyProcessor implements PropertyProcessor {

	@Override
	public Map<String, Object> processProperties(PropertyProcessContext context) {
		Map<String, String> customProperties = context.getCustomProperties();

		if (CollUtil.isEmpty(customProperties)) {
			log.debug("无自定义配置属性");
			return new HashMap<>();
		}

		// 将 Map<String, String> 转换为 Map<String, Object>
		Map<String, Object> configProperties = new HashMap<>(customProperties);

		log.debug("配置属性处理完成，属性数量: {}", configProperties.size());
		return configProperties;
	}

	@Override
	public String getProcessorName() {
		return "ConfigPropertyProcessor";
	}

	@Override
	public int getPriority() {
		return 3; // 第三优先级
	}

}