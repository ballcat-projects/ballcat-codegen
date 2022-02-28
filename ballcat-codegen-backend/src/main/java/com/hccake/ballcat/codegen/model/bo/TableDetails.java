package com.hccake.ballcat.codegen.model.bo;

import com.baomidou.mybatisplus.annotation.DbType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 表详情
 *
 * @author hccake
 */
@Data
public class TableDetails {

	/**
	 * 数据库类型
	 */
	private DbType dbType;

	/**
	 * 表名
	 */
	private String tableName;

	/**
	 * 存储引擎
	 */
	private String engine;

	/**
	 * 表备注
	 */
	private String tableComment;

	/**
	 * 创建时间
	 */
	private LocalDateTime createTime;

	/**
	 * 列信息
	 */
	private List<ColumnInfo> columnInfos;

}
