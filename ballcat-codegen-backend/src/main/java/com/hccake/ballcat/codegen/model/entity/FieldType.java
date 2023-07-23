package com.hccake.ballcat.codegen.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * software：IntelliJ IDEA 2022.2 class name: FieldType description：DB和后端数据类型 the entity
 * object
 *
 * @author MoBaiJun 2022-07-01 14:27:10
 */
@Getter
@Setter
@TableName("gen_field_type")
@Schema(title = "FieldType", description = "DB和后端数据类型 entity object")
public class FieldType {

	@TableId(value = "id", type = IdType.AUTO)
	@Schema(title = "id", description = "主键id")
	private Long id;

	@Schema(title = "groupKey", description = "模板组标识")
	private String groupKey;

	@Schema(title = "columnKey", description = "DB属性类型")
	private String columnKey;

	@Schema(title = "columnValue", description = "对应属性类型")
	private String columnValue;

	@Schema(title = "dbType", description = "数据库类型（1:MySQL，2:Oracle，3:PostGreSql，4:SqlServer）")
	private String dbType;

	@Schema(title = "packageName", description = "属性包路径+类名")
	@TableField(value = "package_name", updateStrategy = FieldStrategy.IGNORED)
	private String packageName;

	@Schema(title = "deleted", description = "逻辑删除字段（1删除0正常）")
	private String deleted;

	@TableField(value = "create_time", fill = FieldFill.INSERT)
	@Schema(title = "createTime", description = "创建时间")
	private LocalDateTime createTime;

	@TableField(value = "update_time", fill = FieldFill.UPDATE)
	@Schema(title = "updateTime", description = "更新时间")
	private LocalDateTime updateTime;

}
