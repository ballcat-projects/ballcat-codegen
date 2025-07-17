package com.hccake.ballcat.codegen.generator.property;

import cn.hutool.core.date.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 系统属性处理器 负责处理系统级别的属性，如当前时间、版本信息等
 *
 * @author hccake
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SystemPropertyProcessor implements PropertyProcessor {

	@Override
	public Map<String, Object> processProperties(PropertyProcessContext context) {
		Map<String, Object> systemProperties = new HashMap<>();

		// 当前时间
		systemProperties.put("currentTime", DateUtil.now());
		// 当前时间戳
		systemProperties.put("timestamp", System.currentTimeMillis());

		log.debug("系统属性处理完成，属性数量: {}", systemProperties.size());
		return systemProperties;
	}

	@Override
	public String getProcessorName() {
		return "SystemPropertyProcessor";
	}

	@Override
	public int getPriority() {
		return 1; // 最高优先级
	}

}