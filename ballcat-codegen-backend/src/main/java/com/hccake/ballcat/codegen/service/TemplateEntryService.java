package com.hccake.ballcat.codegen.service;

import com.hccake.ballcat.codegen.model.bo.TemplateFile;
import com.hccake.ballcat.codegen.model.dto.TemplateEntryCreateDTO;
import com.hccake.ballcat.codegen.model.dto.TemplateEntryUpdateDTO;
import com.hccake.ballcat.codegen.model.entity.TemplateEntry;
import com.hccake.extend.mybatis.plus.service.ExtendService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 模板文件目录项
 *
 * @author hccake
 * @date 2020-06-19 19:11:41
 */
public interface TemplateEntryService extends ExtendService<TemplateEntry> {

	/**
	 * 查询指定模板组下所有的目录项
	 * @param groupKey 模板组标识
	 * @return 所有的目录项
	 */
	List<TemplateEntry> listByGroupKey(String groupKey);

	/**
	 * 移动目录项
	 * @param horizontalMove 是否移动到目标目录平级，否则移动到其内部
	 * @param entryId 被移动的目录项ID
	 * @param targetEntryId 目标目录项ID
	 * @return boolean
	 */
	boolean move(boolean horizontalMove, String entryId, String targetEntryId);

	/**
	 * 重名校验，同文件夹下不允许重名
	 * @param entryId 目录项ID
	 * @param name 文件名
	 * @param groupKey 模板组标识
	 */
	void duplicateNameCheck(String entryId, String name, String groupKey);

	/**
	 * 判断目录项是否存在
	 * @param entryId 目录项ID
	 * @return boolean 存在：true
	 */
	boolean exists(String entryId);

	/**
	 * 新建一个目录项
	 * @param templateEntryCreateDTO 目录项新建传输对象
	 * @param file 二进制文件
	 * @return entryId
	 */
	String createEntry(TemplateEntryCreateDTO templateEntryCreateDTO, MultipartFile file) throws IOException;

	/**
	 * 更新目录项
	 * @param templateEntryUpdateDTO 目录项修改传输对象
	 * @param file
	 * @return success:true
	 */
	boolean updateEntry(TemplateEntryUpdateDTO templateEntryUpdateDTO, MultipartFile file) throws IOException;

	/**
	 * 删除目录项
	 * @param entryId 目录项id
	 * @param mode 删除模式
	 * @return boolean 成功：true
	 */
	boolean removeEntry(String entryId, Integer mode);

	/**
	 * 转换为模板文件
	 * @param templateEntryList 模板目录项集合
	 * @return List<TemplateFile>
	 */
	List<TemplateFile> convertToTemplateFile(List<TemplateEntry> templateEntryList);

	/**
	 * 复制模板目录项文件
	 * @param resourceGroupKey 原模板组
	 * @param targetGroupKey 模板模板组
	 */
	void copy(String resourceGroupKey, String targetGroupKey);

	/**
	 * 删除模板文件
	 * @param groupKey 模板组标识
	 */
	void removeByGroupKey(String groupKey);

	/**
	 * 修改目录项内容
	 * @param id 目录项id
	 * @param content 内容
	 * @return 更新成功 true
	 */
	boolean updateContent(String id, String content);

}