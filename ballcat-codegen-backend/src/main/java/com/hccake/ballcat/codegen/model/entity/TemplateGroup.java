package com.hccake.ballcat.codegen.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

/**
 * 模板组
 *
 * @author hccake 2020-06-19 19:11:41
 */
@Data
@TableName("gen_template_group")
@Schema(title = "模板组")
public class TemplateGroup {

	/**
	 * ID
	 */
	@TableId
	@Schema(title = "ID")
	private Integer id;

	/**
	 * group 标识，唯一
	 */
	@NotBlank(message = "模板组标识不能为空")
	@Pattern(regexp = "^[a-zA-Z0-9_-]*$", message = "模板组标识只能由数字字母以及中划线下划线组成")
	@Length(min = 1, max = 50, message = "模板组标识最长50位")
	@Schema(title = "Key")
	private String groupKey;

	/**
	 * 名称
	 */
	@Schema(title = "名称")
	private String name;

	/**
	 * 图标（Base64）
	 */
	@Schema(title = "图标（Base64）")
	private String icon;

	/**
	 * 是否需要使用使用数据表
	 */
	@Schema(title = "是否需要使用使用数据表")
	private Integer useTable;

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
