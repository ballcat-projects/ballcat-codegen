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
 * 模板信息
 *
 * @author hccake
 * @date 2020-06-19 18:09:08
 */
@Data
@TableName("gen_template_info")
@Schema(title = "模板信息")
public class TemplateInfo {

	private static final long serialVersionUID = 1L;

	/**
	 * 目录项ID
	 */
	@TableId(type = IdType.INPUT)
	@Schema(title = "目录项ID")
	private Integer directoryEntryId;

	/**
	 * 模板组Id
	 */
	@Schema(title = "模板组Id")
	private Integer groupId;

	/**
	 * 模板名称
	 */
	@Schema(title = "模板标题")
	private String title;

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
	 * 修改时间
	 */
	@TableField(fill = FieldFill.INSERT_UPDATE)
	@Schema(title = "修改时间")
	private LocalDateTime updateTime;

}
