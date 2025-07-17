package com.hccake.ballcat.codegen.generator.property;

import cn.hutool.core.bean.BeanUtil;
import com.hccake.ballcat.codegen.model.bo.GenerateProperties;
import com.hccake.ballcat.codegen.model.bo.TableDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 表属性处理器 负责处理表相关的属性信息
 *
 * @author hccake
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TablePropertyProcessor implements PropertyProcessor {

	private final TableMetadataExtractor tablePropertyBuilder;

	@Override
	public Map<String, Object> processProperties(PropertyProcessContext context) {
		TableDetails tableDetails = context.getTableDetails();

		// 如果没有表数据，返回空Map
		if (tableDetails == null) {
			log.debug("无表数据，跳过表属性处理");
			return new HashMap<>();
		}

		// 构建表属性
		GenerateProperties tableProperties = tablePropertyBuilder.buildTableProperties(tableDetails,
				context.getTablePrefix(), context.getTemplateGroupKey());

		// 转换为Map
		Map<String, Object> propertiesMap = BeanUtil.beanToMap(tableProperties);

		log.debug("表属性处理完成，属性数量: {}", propertiesMap.size());
		return propertiesMap;
	}

	@Override
	public String getProcessorName() {
		return "TablePropertyProcessor";
	}

	@Override
	public int getPriority() {
		return 2; // 第二优先级
	}

}