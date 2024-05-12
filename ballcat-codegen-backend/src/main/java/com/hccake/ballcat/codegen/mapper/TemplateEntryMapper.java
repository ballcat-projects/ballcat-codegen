package com.hccake.ballcat.codegen.mapper;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hccake.ballcat.codegen.model.entity.TemplateEntry;
import com.hccake.ballcat.common.core.constant.GlobalConstants;
import com.hccake.extend.mybatis.plus.mapper.ExtendMapper;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

/**
 * 模板文件目录项
 *
 * @author hccake 2020-06-19 19:11:41
 */
@Mapper
public interface TemplateEntryMapper extends ExtendMapper<TemplateEntry> {

	/**
	 * 根据模板组标识查询模板文件目录项集合
	 * @param groupKey 模板组标识
	 * @return List<TemplateDirectoryEntry>
	 */
	default List<TemplateEntry> listByGroupKey(String groupKey) {
		return this.selectList(Wrappers.<TemplateEntry>lambdaQuery().eq(TemplateEntry::getGroupKey, groupKey));
	}

	/**
	 * 检测是否在指定目录下存在指定名称的文件
	 * @param entryId 目录项ID
	 * @param name 文件名称
	 * @param groupKey 模板组标识
	 * @return 是否存在
	 */
	default boolean existSameName(String entryId, String name, String groupKey) {
		Long count = this.selectCount(Wrappers.<TemplateEntry>lambdaQuery()
			.eq(TemplateEntry::getParentId, entryId)
			.eq(TemplateEntry::getGroupKey, groupKey)
			.eq(TemplateEntry::getFilename, name));
		return count != null && count > 0;
	}

	/**
	 * 判断目录项是否存在
	 * @param entryId 目录项ID
	 * @return boolean 存在：true
	 */
	default boolean existEntryId(String entryId) {
		Long count = this.selectCount(Wrappers.<TemplateEntry>lambdaQuery().eq(TemplateEntry::getId, entryId));
		return count != null && count > 0;
	}

	/**
	 * 更新父级目录id
	 * @param groupKey 模板组标识
	 * @param oldParentId 老的父级ID
	 * @param newParentId 新增父级ID
	 */
	default void updateParentId(String groupKey, String oldParentId, String newParentId) {
		LambdaUpdateWrapper<TemplateEntry> wrapper = Wrappers.<TemplateEntry>lambdaUpdate()
			.set(TemplateEntry::getParentId, newParentId)
			.eq(TemplateEntry::getGroupKey, groupKey)
			.eq(TemplateEntry::getParentId, oldParentId);
		this.update(null, wrapper);
	}

	/**
	 * 删除模板文件
	 * @param groupKey 模板组标识
	 */
	default void deleteByGroupKey(String groupKey) {
		this.delete(Wrappers.lambdaQuery(TemplateEntry.class).eq(TemplateEntry::getGroupKey, groupKey));
	}

	/**
	 * 是否存在子文件
	 * @param entryId 目录下id
	 * @return boolean
	 */
	default boolean existSubEntry(String entryId) {
		Long count = this.selectCount(Wrappers.<TemplateEntry>lambdaQuery()
			.eq(TemplateEntry::getParentId, entryId)
			.eq(TemplateEntry::getDeleted, GlobalConstants.NOT_DELETED_FLAG));
		return count != null && count > 0;
	}

}