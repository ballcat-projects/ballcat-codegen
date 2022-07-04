package com.hccake.ballcat.codegen.database;

import com.baomidou.mybatisplus.annotation.DbType;
import com.hccake.ballcat.codegen.converter.DbColumnTypeConverter;
import com.hccake.ballcat.codegen.model.entity.DbColumnType;
import com.hccake.ballcat.codegen.model.entity.FieldType;
import com.hccake.ballcat.codegen.service.FieldTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;

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
	public List<FieldType> getDbTypeList(DbType dbType, Integer templateGroupId) {
		List<FieldType> fieldTypeList = fieldTypeService.selectDbTypeList(dbType, templateGroupId);
		if (!ObjectUtils.isEmpty(fieldTypeList)) {
			return fieldTypeList;
		}
		if (ObjectUtils.isEmpty(fieldTypeList)) {
			fieldTypeList = fieldTypeService.selectDbTypeList(dbType);
		}
		return fieldTypeList;
	}

	/**
	 * 获取对应数据类型
	 * @param dataType 字段类型
	 * @return 数据类型
	 */
	public DbColumnType getTypeConverter(List<FieldType> typeList, String dataType) {
		for (FieldType type : typeList) {
			if (type.getColumnKey().equals(dataType)) {
				return DbColumnTypeConverter.INSTANCE.toModel(type);
			}
		}
		log.warn("未找到对应数据类型，字段类型是：{}", dataType);
		return null;
	}

}
