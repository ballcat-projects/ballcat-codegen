package com.hccake.ballcat.codegen.model.dto;

import lombok.Data;

import java.util.Map;
import java.util.Set;

/**
 * 代码生成选项
 *
 * @author Hccake
 */
@Data
public class GeneratorOptionDTO {

	/**
	 * 表前缀
	 */
	private String tablePrefix;

	/**
	 * 模板组标识
	 */
	private String templateGroupKey;

	/**
	 * 表名称
	 */
	private String[] tableNames;

	/**
	 * 需要生成的的模板目录项
	 */
	private Set<String> templateEntryIds;

	/**
	 * 配置
	 */
	private Map<String, String> genProperties;

}
