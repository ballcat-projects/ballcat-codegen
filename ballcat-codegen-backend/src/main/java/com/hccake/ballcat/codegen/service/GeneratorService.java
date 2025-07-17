package com.hccake.ballcat.codegen.service;

import com.hccake.ballcat.codegen.model.bo.FileEntry;
import com.hccake.ballcat.codegen.model.dto.GeneratorOptionDTO;

import java.util.List;

/**
 * 代码生成服务
 * 
 * @author hccake
 * @date 2018/7/29
 */
public interface GeneratorService {

	/**
	 * 生成代码文件条目
	 * @param generatorOptionDTO 代码生成的一些参数
	 * @return 已生成的代码文件条目列表
	 */
	List<FileEntry> generateFileEntries(GeneratorOptionDTO generatorOptionDTO);

}
