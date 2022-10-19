package com.hccake.ballcat.codegen.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 模板目录项
 *
 * @author hccake 2020-06-19 19:11:41
 */
@Data
@TableName("gen_template_entry")
@Schema(title = "模板目录项")
public class TemplateEntry {

	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId(type = IdType.ASSIGN_ID)
	@Schema(title = "ID")
	private String id;

	/**
	 * 模板组标识
	 */
	@Schema(title = "模板组标识")
	private String groupKey;

	/**
	 * 文件夹全路径/模板文件名称（支持占位符）
	 */
	@Schema(title = "文件夹路径/模板文件名称（支持占位符）")
	private String filename;

	/**
	 * 文件类型 1：文件夹 2：模板文件
	 */
	@Schema(title = "文件类型 1：文件夹 2：模板文件")
	private Integer type;

	/**
	 * 父级Id
	 */
	@Schema(title = "父级Id")
	private String parentId;

	/**
	 * 模板内容
	 */
	@Schema(title = "模板内容")
	private String content;

	/**
	 * 模板引擎类型 1：velocity
	 */
	@Schema(title = "模板引擎类型 1：velocity")
	private Integer engineType;

	/**
	 * 备注
	 */
	@Schema(title = "备注")
	private String remarks;

	/**
	 * 逻辑删除
	 */
	@TableLogic
	@TableField(fill = FieldFill.INSERT)
	@Schema(title = "逻辑删除")
	private Long deleted;

	/**
	 * 创建时间
	 */
	@TableField(fill = FieldFill.INSERT)
	@Schema(title = "创建时间")
	private LocalDateTime createTime;

	/**
	 * 更新时间
	 */
	@TableField(fill = FieldFill.INSERT_UPDATE)
	@Schema(title = "更新时间")
	private LocalDateTime updateTime;

}
