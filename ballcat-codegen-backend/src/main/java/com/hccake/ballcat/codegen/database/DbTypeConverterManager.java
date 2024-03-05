package com.hccake.ballcat.codegen.database;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.annotation.DbType;
import com.hccake.ballcat.codegen.constant.TemplateEntryConstants;
import com.hccake.ballcat.codegen.converter.DbColumnTypeConverter;
import com.hccake.ballcat.codegen.model.entity.DbColumnType;
import com.hccake.ballcat.codegen.model.entity.FieldType;
import com.hccake.ballcat.codegen.service.FieldTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author hccake
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DbTypeConverterManager {

	private final FieldTypeService fieldTypeService;

	/**
	 * 获取字段集合
	 * @return 字段集合
	 */
	public List<FieldType> getDbTypeList(DbType dbType, String templateGroupKey) {
		List<FieldType> fieldTypeList = fieldTypeService.selectDbTypeList(dbType, templateGroupKey);
		Map<String, FieldType> fieldTypeMap = new HashMap<>(fieldTypeList.size());
		for (FieldType fieldType : fieldTypeList) {
			String columnKey = fieldType.getColumnKey();
			FieldType fieldTypeMapValue = fieldTypeMap.get(columnKey);
			if (Objects.isNull(fieldTypeMapValue)) {
				fieldTypeMap.put(columnKey, fieldType);
			}
			else {
				String groupKey = fieldTypeMapValue.getGroupKey();
				if (!Objects.equals(TemplateEntryConstants.TREE_ROOT_ID, groupKey)) {
					fieldTypeMap.put(columnKey, fieldType);
				}
			}
		}
		return new ArrayList<>(fieldTypeMap.values());
	}

	/**
	 * 获取对应数据类型
	 * @param dataType 字段类型
	 * @return 数据类型
	 */
	public DbColumnType getTypeConverter(List<FieldType> typeList, String dataType) {
		for (FieldType type : typeList) {
			if (type.getColumnKey().equalsIgnoreCase(dataType)) {
				return DbColumnTypeConverter.INSTANCE.toModel(type);
			}
		}
		log.warn("未找到对应数据类型，字段类型是：{}", dataType);
		return null;
	}

}
