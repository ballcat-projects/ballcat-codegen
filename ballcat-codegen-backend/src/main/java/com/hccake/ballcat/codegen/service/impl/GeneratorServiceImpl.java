package com.hccake.ballcat.codegen.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ArrayUtil;
import com.hccake.ballcat.codegen.constant.DirectoryEntryTypeEnum;
import com.hccake.ballcat.codegen.converter.TemplateModelConverter;
import com.hccake.ballcat.codegen.model.bo.TemplateFile;
import com.hccake.ballcat.codegen.model.dto.GeneratorOptionDTO;
import com.hccake.ballcat.codegen.model.entity.TemplateDirectoryEntry;
import com.hccake.ballcat.codegen.model.entity.TemplateInfo;
import com.hccake.ballcat.codegen.model.vo.ColumnInfo;
import com.hccake.ballcat.codegen.model.vo.TableInfo;
import com.hccake.ballcat.codegen.model.vo.TemplateEntryTree;
import com.hccake.ballcat.codegen.service.GeneratorService;
import com.hccake.ballcat.codegen.service.TableInfoService;
import com.hccake.ballcat.codegen.service.TemplateDirectoryEntryService;
import com.hccake.ballcat.codegen.service.TemplateInfoService;
import com.hccake.ballcat.codegen.util.GenUtils;
import com.hccake.ballcat.common.core.constant.GlobalConstants;
import com.hccake.ballcat.common.util.tree.TreeNode;
import com.hccake.ballcat.common.util.tree.TreeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.ZipOutputStream;

/**
 * @author Hccake
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GeneratorServiceImpl implements GeneratorService {

	private final TableInfoService tableInfoService;

	private final TemplateDirectoryEntryService templateDirectoryEntryService;

	private final TemplateInfoService templateInfoService;

	/**
	 * 生成代码
	 * @param generatorOptionDTO 代码生成的一些配置信息
	 * @return 已生成的代码数据
	 */
	@Override
	public byte[] generatorCode(GeneratorOptionDTO generatorOptionDTO) throws IOException {
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				ZipOutputStream zip = new ZipOutputStream(outputStream)) {

			// 根据tableName 查询最新的表单配置
			List<TemplateFile> templateFiles = templateDirectoryEntryService.listTemplateFiles(
					generatorOptionDTO.getTemplateGroupId(), generatorOptionDTO.getTemplateFileIds());
			Assert.notEmpty(templateFiles, "模板组中模板文件为空！");

			for (String tableName : generatorOptionDTO.getTableNames()) {
				// 查询表信息
				TableInfo tableInfo = tableInfoService.queryTableInfo(tableName);
				// 查询列信息
				List<ColumnInfo> columnInfoList = tableInfoService.listColumnInfo(tableName);
				// 生成代码
				GenUtils.generatorCode(generatorOptionDTO.getTablePrefix(), generatorOptionDTO.getGenProperties(),
						tableInfo, columnInfoList, zip, templateFiles);
			}
			// 手动结束 zip，防止文件末端未被写入
			zip.finish();
			return outputStream.toByteArray();
		}
	}

	@Override
	public List<TemplateEntryTree> previewCode(GeneratorOptionDTO preGenerateOptionDTO) {
		// 只能单表预览
		String[] tableNames = preGenerateOptionDTO.getTableNames();
		Assert.isTrue(ArrayUtil.isNotEmpty(tableNames) && tableNames.length == 1, "预览仅支持单表");

		// 获取指定生成的文件信息
		List<TemplateInfo> templateInfos = templateInfoService.listByIds(preGenerateOptionDTO.getTemplateFileIds());
		Assert.isTrue(ArrayUtil.isNotEmpty(templateInfos), "至少选择一个需要生成的文件");
		Map<Integer, TemplateInfo> templateInfoMap = templateInfos.stream()
				.collect(Collectors.toMap(TemplateInfo::getDirectoryEntryId, Function.identity()));

		// 获取当前模板组下的所有文件目录
		List<TemplateDirectoryEntry> entryList = templateDirectoryEntryService
				.listByTemplateGroupId(preGenerateOptionDTO.getTemplateGroupId());
		// 转树形目录结构
		List<TemplateEntryTree> treeList = TreeUtils.buildTree(entryList, GlobalConstants.TREE_ROOT_ID,
				TemplateModelConverter.INSTANCE::entryPoToTree);
		// 剪枝 & 填充文件信息
		treeList = treeList.stream().map(node -> pruningAndFillContent(node, templateInfoMap)).filter(Objects::nonNull)
				.collect(Collectors.toList());
		// 树转list, 平铺树结构
		List<TemplateEntryTree> list = treeToList(treeList);

		// 获取表名
		String tableName = tableNames[0];
		// 查询表信息
		TableInfo tableInfo = tableInfoService.queryTableInfo(tableName);
		// 查询列信息
		List<ColumnInfo> columnInfoList = tableInfoService.listColumnInfo(tableName);
		// 生成代码
		GenUtils.previewCode(preGenerateOptionDTO.getTablePrefix(), preGenerateOptionDTO.getGenProperties(), tableInfo,
				columnInfoList, list);

		return list;
	}

	private List<TemplateEntryTree> treeToList(List<TemplateEntryTree> treeList) {
		List<TemplateEntryTree> list = new ArrayList<>();

		Queue<TemplateEntryTree> queue = new LinkedList<>(treeList);
		while (!queue.isEmpty()) {
			TemplateEntryTree entry = queue.poll();
			if (CollectionUtil.isNotEmpty(entry.getChildren())) {
				queue.addAll((List<TemplateEntryTree>) entry.getChildren());
			}
			entry.setChildren(null);
			list.add(entry);
		}

		return list;
	}

	/**
	 * 剪枝 && 填充文件内容，对于没有文件的目录进行删除
	 * @param node 当前节点
	 * @param templateInfoMap 模板信息 map
	 * @return null 或者 填充后的节点
	 */
	private TemplateEntryTree pruningAndFillContent(TemplateEntryTree node,
			Map<Integer, TemplateInfo> templateInfoMap) {

		// 文件必然是叶子节点，不用再向下查找了
		if (DirectoryEntryTypeEnum.FILE.getType().equals(node.getType())) {
			// 如果不是指定需要的
			TemplateInfo templateInfo = templateInfoMap.get(node.getId());
			if (templateInfo == null) {
				return null;
			}
			// 填充文件内容
			node.setContent(templateInfo.getContent());
			return node;
		}

		// 递归处理子节点
		List<? extends TreeNode<Integer>> children = node.getChildren();
		if (CollectionUtil.isNotEmpty(children)) {
			children = children.stream().map(x -> pruningAndFillContent((TemplateEntryTree) x, templateInfoMap))
					.filter(Objects::nonNull).collect(Collectors.toList());
			node.setChildren(children);
		}

		// 如果处理后没有子节点了，当前节点也不必存在
		return CollectionUtil.isEmpty(children) ? null : node;
	}

}
