package com.hccake.ballcat.codegen.model.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * software：IntelliJ IDEA 2022.1 class name: DbColumnType class description： 表字段类型
 *
 * @author MoBaiJun 2022/7/4 14:00
 */
@Accessors(chain = true)
@Getter
@Setter
public class DbColumnType {

	/**
	 * 类型
	 */
	private String type;

	/**
	 * 包路径
	 */
	private String pkg;

}
