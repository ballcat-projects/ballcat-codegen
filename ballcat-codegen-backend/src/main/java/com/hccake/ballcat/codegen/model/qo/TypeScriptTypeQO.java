package com.hccake.ballcat.codegen.model.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * software：IntelliJ IDEA 2022.2 class name: TypeScriptTypeQO description：前端和后端数据类型管理 The
 * QO added object
 *
 * @author MoBaiJun 2022-07-01 14:27:10
 */
@Getter
@Setter
@ToString
@Schema(title = "TypeScriptTypeQO", description = "前端和后端数据类型管理 QO object")
public class TypeScriptTypeQO {

	@Schema(title = "id", description = "主键id")
	private Long id;

	@Schema(title = "groupKey", description = "模板组标识")
	private String groupKey;

}