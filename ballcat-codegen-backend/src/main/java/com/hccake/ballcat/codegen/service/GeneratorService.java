package com.hccake.ballcat.codegen.service;

import com.hccake.ballcat.codegen.generator.context.CodeGenerationContext;
import com.hccake.ballcat.codegen.generator.context.CodeGenerationContextBuilder;
import com.hccake.ballcat.codegen.generator.context.CodeGenerationResult;
import com.hccake.ballcat.codegen.generator.CodeGenerator;
import com.hccake.ballcat.codegen.model.bo.FileEntry;
import com.hccake.ballcat.codegen.model.dto.GeneratorOptionDTO;
import com.hccake.ballcat.common.core.exception.BusinessException;
import com.hccake.ballcat.common.model.result.SystemResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 代码生成服务
 * 
 * @author hccake
 * @date 2018/7/29
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GeneratorService {

	private final CodeGenerator codeGenerator;

	private final CodeGenerationContextBuilder contextBuilder;

	/**
	 * 生成代码文件条目
	 * @param generatorOptionDTO 代码生成的一些参数
	 * @return 已生成的代码文件条目列表
	 */
	public List<FileEntry> generateFileEntries(GeneratorOptionDTO generatorOptionDTO) {
		// 构建生成上下文
		CodeGenerationContext context = contextBuilder.buildFromDTO(generatorOptionDTO);

		// 执行代码生成
		CodeGenerationResult result = codeGenerator.generate(context);

		// 检查生成结果
		if (!result.isSuccess()) {
			throw new BusinessException(SystemResultCode.SERVER_ERROR.getCode(), "代码生成失败: " + result.getErrorMessage());
		}

		return result.getFileEntries();
	}

}
