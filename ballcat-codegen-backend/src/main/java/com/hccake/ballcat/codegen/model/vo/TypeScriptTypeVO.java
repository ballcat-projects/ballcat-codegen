package com.hccake.ballcat.codegen.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * software：IntelliJ IDEA 2022.2
 * class name: TypeScriptTypeVO
 * description：前端和后端数据类型管理 view object
 *
 * @author MoBaiJun 2022-07-01 14:27:10
 */
@Getter
@Setter
@ToString
@Schema(title = "TypeScriptTypeVO", description = "前端和后端数据类型管理 view object")
public class TypeScriptTypeVO {

	@Schema(title = "id", description = "主键id")
	private Long id;

	@Schema(title = "groupId", description = "模板组id")
	private Long groupId;

	@Schema(title = "defaultValue", description = "是否默认（0：默认，1：非默认）")
	private String defaultValue;

	@Schema(title = "codeKey", description = "Java对应类型")
	private String codeKey;

	@Schema(title = "codeValue", description = "界面对应类型")
	private String codeValue;

	@Schema(title = "createTime", description = "创建时间")
	private LocalDateTime createTime;

	@Schema(title = "updateTime", description = "更新时间")
	private LocalDateTime updateTime;
}
