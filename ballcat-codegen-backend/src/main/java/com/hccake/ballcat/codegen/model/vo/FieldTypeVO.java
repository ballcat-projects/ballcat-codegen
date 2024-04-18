package com.hccake.ballcat.codegen.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * software：IntelliJ IDEA 2022.2 class name: FieldTypeVO description：DB和后端数据类型 view object
 *
 * @author MoBaiJun 2022-07-01 14:27:10
 */
@Getter
@Setter
@ToString
@Schema(title = "FieldTypeVO", description = "DB和后端数据类型 view object")
public class FieldTypeVO {

	@Schema(title = "id", description = "主键id")
	private Long id;

	@Schema(title = "groupKey", description = "模板组标识")
	private String groupKey;

	@Schema(title = "defaultValue", description = "是否默认值(0,默认值，1,非默认值)")
	private String defaultValue;

	@Schema(title = "columnKey", description = "DB属性类型")
	private String columnKey;

	@Schema(title = "columnValue", description = "对应属性类型")
	private String columnValue;

	@Schema(title = "dbType", description = "数据库类型（1:MySQL，2:Oracle，3:PostGreSql，4:SqlServer）")
	private String dbType;

	@Schema(title = "packageName", description = "属性包路径+类名")
	private String packageName;

	@Schema(title = "createTime", description = "创建时间")
	private LocalDateTime createTime;

	@Schema(title = "updateTime", description = "更新时间")
	private LocalDateTime updateTime;

}
