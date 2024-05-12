package com.hccake.ballcat.codegen.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.hccake.ballcat.codegen.constant.TemplateEntryConstants;
import com.hccake.ballcat.codegen.constant.TemplateEntryRemoveModeEnum;
import com.hccake.ballcat.codegen.constant.TemplateEntryTypeEnum;
import com.hccake.ballcat.codegen.converter.TemplateModelConverter;
import com.hccake.ballcat.codegen.mapper.TemplateEntryMapper;
import com.hccake.ballcat.codegen.model.bo.TemplateFile;
import com.hccake.ballcat.codegen.model.dto.TemplateEntryCreateDTO;
import com.hccake.ballcat.codegen.model.dto.TemplateEntryUpdateDTO;
import com.hccake.ballcat.codegen.model.entity.TemplateEntry;
import com.hccake.ballcat.codegen.model.vo.TemplateEntryTree;
import com.hccake.ballcat.codegen.service.TemplateEntryService;
import com.hccake.ballcat.codegen.util.GenerateUtils;
import com.hccake.ballcat.common.core.exception.BusinessException;
import com.hccake.ballcat.common.model.result.BaseResultCode;
import com.hccake.ballcat.common.util.tree.TreeUtils;
import com.hccake.extend.mybatis.plus.service.impl.ExtendServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 模板文件目录项
 *
 * @author hccake 2020-06-19 19:11:41
 */
@Service
@RequiredArgsConstructor
public class TemplateEntryServiceImpl extends ExtendServiceImpl<TemplateEntryMapper, TemplateEntry>
		implements TemplateEntryService {

	/**
	 * 查询指定模板组下所有的目录项
	 * @param groupKey 模板组标识
	 * @return 所有的目录项
	 */
	@Override
	public List<TemplateEntry> listByGroupKey(String groupKey) {
		return baseMapper.listByGroupKey(groupKey);
	}

	/**
	 * 移动目录项
	 * @param horizontalMove 是否移动到目标目录平级，否则移动到其内部
	 * @param entryId 被移动的目录项ID
	 * @param targetEntryId 目标目录项ID
	 * @return boolean 移动成功或者失败
	 */
	@Override
	public boolean move(boolean horizontalMove, String entryId, String targetEntryId) {
		// 目录项必须存在
		TemplateEntry entry = baseMapper.selectById(entryId);
		Assert.notNull(entry, "This is a nonexistent directory entry!");

		TemplateEntry targetEntry = baseMapper.selectById(targetEntryId);
		// 目标必须存
		Assert.notNull(entry, "Target directory entry does not exist!");
		// 非平级移动时，目标必须是文件夹
		Assert.isTrue(horizontalMove || TemplateEntryTypeEnum.FOLDER.getType().equals(targetEntry.getType()),
				"The target is not a folder");

		// 平级移动则目标父节点就是其父节点
		String parentId = horizontalMove ? targetEntry.getParentId() : targetEntry.getId();
		// 如果已经在该文件夹下则无需移动
		Assert.notEquals(parentId, entry.getParentId(), "The entry do not need to be moved");

		// 重名校验
		this.duplicateNameCheck(parentId, entry.getFilename(), entry.getGroupKey());

		// 更新目录项
		TemplateEntry entity = new TemplateEntry();
		entity.setId(entryId);
		entity.setParentId(parentId);
		return SqlHelper.retBool(baseMapper.updateById(entity));
	}

	/**
	 * 重名校验，同文件夹下不允许重名
	 * @param entryId 目录项ID
	 * @param name 文件名
	 * @param groupKey 模板组标识
	 */
	@Override
	public void duplicateNameCheck(String entryId, String name, String groupKey) {
		boolean existed = baseMapper.existSameName(entryId, name, groupKey);
		Assert.isFalse(existed, "The entry with the same name already exists");
	}

	/**
	 * 判断目录项是否存在
	 * @param entryId 目录项ID
	 * @return boolean 存在：true
	 */
	@Override
	public boolean exists(String entryId) {
		return baseMapper.existEntryId(entryId);
	}

	/**
	 * 删除目录项
	 * @param entryId 目录项id
	 * @param mode 删除模式
	 * @return boolean 成功：true
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean removeEntry(String entryId, Integer mode) {
		TemplateEntry entry = baseMapper.selectById(entryId);
		String groupKey = entry.getGroupKey();
		Assert.notNull(entry, "This is a nonexistent directory entry!");

		// 如果是文件夹类型，则根据删除模式进行子节点删除或上移操作
		if (TemplateEntryTypeEnum.FOLDER.getType().equals(entry.getType())) {
			if (TemplateEntryRemoveModeEnum.RESERVED_CHILD_NODE.getType().equals(mode)) {
				// 子节点上移
				baseMapper.updateParentId(groupKey, entryId, entry.getParentId());
			}
			else if (TemplateEntryRemoveModeEnum.REMOVE_CHILD_NODE.getType().equals(mode)) {
				// ==========删除所有子节点=============
				// 1. 获取所有目录项（目录项不会太多，一次查询比较方便）
				List<TemplateEntry> entryList = baseMapper.listByGroupKey(groupKey);
				// 2. 获取当前删除目录项的孩子节点列表
				List<TemplateEntryTree> treeList = TreeUtils.buildTree(entryList, entryId,
						TemplateModelConverter.INSTANCE::entryPoToTree);
				// 3. 获取当前删除目录项的孩子节点Id
				List<String> treeNodeIds = TreeUtils.getTreeNodeIds(treeList);
				// 4. 删除所有孩子节点
				if (CollUtil.isNotEmpty(treeNodeIds)) {
					baseMapper.deleteBatchIds(treeNodeIds);
				}
			}
			else {
				throw new BusinessException(BaseResultCode.LOGIC_CHECK_ERROR.getCode(), "error delete mode");
			}
		}

		// 删除自身
		return SqlHelper.retBool(baseMapper.deleteById(entryId));
	}

	/**
	 * 复制模板目录项文件
	 * @param resourceGroupKey 原模板组
	 * @param targetGroupKey 模板模板组
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void copy(String resourceGroupKey, String targetGroupKey) {
		// 1. ===============获取模板目录项==================
		List<TemplateEntry> list = baseMapper.listByGroupKey(resourceGroupKey);

		// 2. ============== 复制模板文件 ===================
		Set<String> oldParentIdSet = new HashSet<>();
		List<String> originEntryIds = new ArrayList<>();
		for (TemplateEntry entry : list) {
			originEntryIds.add(entry.getId());
			oldParentIdSet.add(entry.getParentId());

			entry.setGroupKey(targetGroupKey);
			entry.setId(null);
			entry.setCreateTime(null);
			entry.setUpdateTime(null);
		}
		this.saveBatchSomeColumn(list);

		// 3. =============== 获取新老ID的映射表，key: oldId, value: newId ==========
		Map<String, String> idMap = new HashMap<>(originEntryIds.size());
		for (int i = 0; i < originEntryIds.size(); i++) {
			idMap.put(originEntryIds.get(i), list.get(i).getId());
		}

		// 4. =============== 更新复制出来的模板文件的父级ID ===============
		// 父节点为根节点的不需要修改
		oldParentIdSet.remove(TemplateEntryConstants.TREE_ROOT_ID);
		for (String oldParentId : oldParentIdSet) {
			baseMapper.updateParentId(targetGroupKey, oldParentId, idMap.get(oldParentId));
		}

	}

	/**
	 * 删除模板文件
	 * @param groupKey 模板组标识
	 */
	@Override
	public void removeByGroupKey(String groupKey) {
		baseMapper.deleteByGroupKey(groupKey);
	}

	@Override
	public boolean updateContent(String id, String content) {
		TemplateEntry entry = new TemplateEntry();
		entry.setId(id);
		entry.setFileContent(content.getBytes(StandardCharsets.UTF_8));
		return SqlHelper.retBool(baseMapper.updateById(entry));
	}

	@Override
	public List<TemplateFile> convertToTemplateFile(List<TemplateEntry> templateEntryList) {
		// 转树形目录结构
		List<TemplateEntryTree> treeList = TreeUtils.buildTree(templateEntryList, TemplateEntryConstants.TREE_ROOT_ID,
				TemplateModelConverter.INSTANCE::entryPoToTree);

		// 填充模板文件
		List<TemplateFile> templateFiles = new ArrayList<>();
		treeList.forEach(tree -> fillTemplateFiles(tree, templateFiles, ""));
		return templateFiles;
	}

	/**
	 * 填充模板文件信息
	 * @param current 当前目录项
	 * @param list 模板文件列表
	 * @param path 当前目录路径
	 */
	private void fillTemplateFiles(TemplateEntryTree current, List<TemplateFile> list, String path) {

		// 文件夹类型则递归子节点
		if (TemplateEntryTypeEnum.FOLDER.equals(current.getType())) {
			List<TemplateEntryTree> children = current.getChildren();
			// 递归调用子节点，查找叶子节点
			if (CollUtil.isNotEmpty(children)) {
				for (TemplateEntryTree child : children) {
					fillTemplateFiles(child, list, GenerateUtils.concatFilePath(path, current.getFilename()));
				}
			}
		}

		TemplateFile templateFile = new TemplateFile().setId(current.getId())
			.setFilename(current.getFilename())
			.setParentFilePath(path)
			.setType(current.getType());
		// 目录项类型为文件则记录（文件必然是叶子节点）
		if (TemplateEntryTypeEnum.TEMPLATE_FILE.equals(current.getType())) {
			// 查找对应的模板文件详情信息
			templateFile.setEngineType(current.getEngineType());
		}
		templateFile.setFileContent(current.getFileContent());

		list.add(templateFile);
	}

	/**
	 * 新建一个目录项
	 * @param entryDTO 目录项新建传输对象
	 * @param file 二进制文件
	 * @return entryId
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public String createEntry(TemplateEntryCreateDTO entryDTO, MultipartFile file) throws IOException {
		boolean isBinaryFile = TemplateEntryTypeEnum.BINARY_FILE.getType().equals(entryDTO.getType());
		if (isBinaryFile && file == null) {
			throw new IllegalArgumentException("二进制模板项必须要上传对应的文件");
		}
		// 若父节点不是根，则校验父级节点是否有效
		String parentId = entryDTO.getParentId();
		Assert.isTrue(TemplateEntryConstants.TREE_ROOT_ID.equals(parentId) || this.exists(parentId),
				"This is a nonexistent parent directory entry!");
		// 重名校验
		this.duplicateNameCheck(parentId, entryDTO.getFilename(), entryDTO.getGroupKey());
		// 转持久层对象
		TemplateEntry entity = TemplateModelConverter.INSTANCE.entryCreateDtoToPo(entryDTO);
		if (isBinaryFile) {
			entity.setFileContent(file.getBytes());
		}
		// 落库
		baseMapper.insert(entity);
		return entity.getId();
	}

	/**
	 * 更新目录项
	 * @param entryDTO 目录项修改传输对象
	 * @param file 二进制文件
	 * @return success:true
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean updateEntry(TemplateEntryUpdateDTO entryDTO, MultipartFile file) throws IOException {
		String entryId = entryDTO.getId();
		String filename = entryDTO.getFilename();
		// 目录项必须存在
		TemplateEntry oldEntry = baseMapper.selectById(entryId);
		Assert.notNull(oldEntry, "This is a nonexistent directory entry!");
		// 如果更新了文件名，则进行重名校验
		if (!filename.equals(oldEntry.getFilename())) {
			this.duplicateNameCheck(oldEntry.getParentId(), filename, oldEntry.getGroupKey());
		}
		// 如果修改类型为模板文件或者二进制文件，则现在不能有子文件存在
		if (!TemplateEntryTypeEnum.FOLDER.getType().equals(entryDTO.getType())) {
			Assert.isFalse(baseMapper.existSubEntry(entryId), "当前文件是文件夹类型且其路径下有子文件，不允许修改为其他类型！");
		}
		// 更新 entry
		TemplateEntry entry = TemplateModelConverter.INSTANCE.entryUpdateDtoToPo(entryDTO);
		if (TemplateEntryTypeEnum.BINARY_FILE.getType().equals(entryDTO.getType()) && file != null) {
			entry.setFileContent(file.getBytes());
		}
		return SqlHelper.retBool(baseMapper.updateById(entry));
	}

}