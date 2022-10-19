package com.hccake.ballcat.codegen.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * software：IntelliJ IDEA 2022.2 class name: TypeScriptTypeDTO description：前端和后端数据类型管理 The
 * DTO added object
 *
 * @author MoBaiJun 2022-07-01 14:27:10
 */
@Getter
@Setter
@ToString
@Schema(title = "TypeScriptTypeDTO", description = "前端和后端数据类型管理 DTO object")
public class TypeScriptTypeDTO {

	@Schema(title = "id", description = "主键id")
	private Long id;

	@Schema(title = "groupKey", description = "模板组标识")
	private String groupKey;

	@Schema(title = "codeKey", description = "Java对应类型")
	private String codeKey;

	@Schema(title = "codeValue", description = "界面对应类型")
	private String codeValue;

}