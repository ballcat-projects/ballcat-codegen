package com.hccake.ballcat.codegen.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * software：IntelliJ IDEA 2022.2 class name: FieldTypeDTO description：DB和后端数据类型 The DTO
 * added object
 *
 * @author MoBaiJun 2022-07-01 14:27:10
 */
@Getter
@Setter
@ToString
@Schema(title = "FieldTypeDTO", description = "DB和后端数据类型 DTO object")
public class FieldTypeDTO {

	@Schema(title = "id", description = "主键id")
	private Long id;

	@Schema(title = "groupId", description = "模板组id")
	private Long groupId;

    @Schema(title = "columnKey", description = "DB属性类型")
    private String columnKey;

	@Schema(title = "columnValue", description = "对应属性类型")
	private String columnValue;

	@Schema(title = "dbType", description = "数据库类型（1:MySQL，2:Oracle，3:PostGreSql，4:SqlServer）")
	private String dbType;

	@Schema(title = "packageName", description = "属性包路径+类名")
	private String packageName;

}