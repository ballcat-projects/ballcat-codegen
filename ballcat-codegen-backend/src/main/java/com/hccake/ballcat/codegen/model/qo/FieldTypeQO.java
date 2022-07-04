package com.hccake.ballcat.codegen.model.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * software：IntelliJ IDEA 2022.2
 * class name: FieldTypeQO
 * description：DB和后端数据类型 The QO added object
 * 
 * @author MoBaiJun 2022-07-01 14:27:10
 */
@Getter
@Setter
@ToString
@Schema(title = "FieldTypeQO", description = "DB和后端数据类型 QO object")
public class FieldTypeQO  {

    @Schema(title = "id", description = "主键id")
    private Long id;

    @Schema(title = "groupId", description = "模板组id")
    private Long groupId;

    @Schema(title = "defaultValue", description = "是否默认值(0,默认值，1,非默认值)")
    private String defaultValue;

    @Schema(title = "dbType", description = "数据库类型（1:MySQL，2:Oracle，3:PostGreSql，4:SqlServer）")
    private String dbType;
}