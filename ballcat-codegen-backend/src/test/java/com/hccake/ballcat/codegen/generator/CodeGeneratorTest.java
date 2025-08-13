package com.hccake.ballcat.codegen.generator;

import com.hccake.ballcat.codegen.constant.TemplateEntryTypeEnum;
import com.hccake.ballcat.codegen.engine.TemplateEngineTypeEnum;
import com.hccake.ballcat.codegen.generator.context.CodeGenerationContext;
import com.hccake.ballcat.codegen.generator.context.CodeGenerationResult;
import com.hccake.ballcat.codegen.generator.context.TemplateContextBuilder;
import com.hccake.ballcat.codegen.model.bo.FileEntry;
import com.hccake.ballcat.codegen.model.bo.TemplateFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * 代码生成器测试
 *
 * @author hccake
 */
@ExtendWith(MockitoExtension.class)
class CodeGeneratorTest {

	@Mock
	private TemplateContextBuilder templateContextBuilder;

	@Mock
	private TemplateFileRenderer templateFileRenderer;

	private CodeGenerator codeGenerator;

	@BeforeEach
	void setUp() {
		codeGenerator = new CodeGenerator(templateContextBuilder, templateFileRenderer);
	}

	@Test
	void testTemplateOnlyGeneration() {
		// 准备测试数据
		TemplateFile templateFile = createTestTemplateFile();
		List<TemplateFile> templateFiles = Collections.singletonList(templateFile);

		Map<String, String> customProperties = new HashMap<>();
		customProperties.put("projectName", "TestProject");

		CodeGenerationContext context = CodeGenerationContext.builder()
			.templateGroupKey("test-group")
			.templateFiles(templateFiles)
			.customProperties(customProperties)
			.generationMode(CodeGenerationContext.GenerationMode.TEMPLATE_ONLY)
			.build();

		// Mock 模板上下文构建器
		Map<String, Object> templateContext = new HashMap<>();
		templateContext.put("projectName", "TestProject");

		when(templateContextBuilder.buildTemplateContext(eq(context), isNull())).thenReturn(templateContext);

		// Mock 模板文件渲染器
		FileEntry expectedFileEntry = new FileEntry();
		expectedFileEntry.setId("test-file-1");
		expectedFileEntry.setFilename("TestTestProject.java");
		expectedFileEntry.setFilePath("src/main/java/TestTestProject.java");
		expectedFileEntry.setType(TemplateEntryTypeEnum.TEMPLATE_FILE);
		expectedFileEntry.setFileContent("public class TestTestProject {}".getBytes(StandardCharsets.UTF_8));

		when(templateFileRenderer.processTemplateFile(eq(templateFile), eq(templateContext)))
			.thenReturn(expectedFileEntry);

		// 执行生成
		CodeGenerationResult result = codeGenerator.generate(context);

		// 验证结果
		assertTrue(result.isSuccess());
		assertNotNull(result.getFileEntries());
		assertEquals(1, result.getFileEntries().size());

		FileEntry fileEntry = result.getFileEntries().get(0);
		assertEquals("TestTestProject.java", fileEntry.getFilename());
		assertEquals("src/main/java/TestTestProject.java", fileEntry.getFilePath());

		CodeGenerationResult.GenerationStatistics stats = result.getStatistics();
		assertEquals(1, stats.getTotalFiles());
		assertEquals(1, stats.getTemplateFiles());
		assertEquals(0, stats.getBinaryFiles());
		assertEquals(0, stats.getFolders());
		assertTrue(stats.getDuration() >= 0);
	}

	@Test
	void testInvalidContext() {
		// 测试无效上下文 - 空的模板组标识
		CodeGenerationContext invalidContext = CodeGenerationContext.builder()
			.templateGroupKey("")
			.templateFiles(Collections.emptyList())
			.generationMode(CodeGenerationContext.GenerationMode.TEMPLATE_ONLY)
			.build();

		CodeGenerationResult result = codeGenerator.generate(invalidContext);
		assertFalse(result.isSuccess());
		assertEquals("无效的生成上下文", result.getErrorMessage());

		// 测试无效上下文 - 空的模板文件列表
		CodeGenerationContext emptyFilesContext = CodeGenerationContext.builder()
			.templateGroupKey("test-group")
			.templateFiles(Collections.emptyList())
			.generationMode(CodeGenerationContext.GenerationMode.TEMPLATE_ONLY)
			.build();

		CodeGenerationResult emptyResult = codeGenerator.generate(emptyFilesContext);
		assertFalse(emptyResult.isSuccess());
		assertEquals("无效的生成上下文", emptyResult.getErrorMessage());
	}

	@Test
	void testGenerationWithTableDetails() {
		// 测试基于表的生成
		TemplateFile templateFile = createTestTemplateFile();
		List<TemplateFile> templateFiles = Collections.singletonList(templateFile);

		Map<String, String> customProperties = new HashMap<>();
		customProperties.put("projectName", "TestProject");

		CodeGenerationContext context = CodeGenerationContext.builder()
			.templateGroupKey("test-group")
			.templateFiles(templateFiles)
			.customProperties(customProperties)
			.generationMode(CodeGenerationContext.GenerationMode.TABLE_BASED)
			.tableDetailsList(Collections.emptyList()) // 空表列表也应该能正常处理
			.build();

		// Mock 模板上下文构建器
		Map<String, Object> templateContext = new HashMap<>();
		templateContext.put("projectName", "TestProject");

		when(templateContextBuilder.buildTemplateContext(eq(context), isNull())).thenReturn(templateContext);

		// Mock 模板文件渲染器
		FileEntry expectedFileEntry = new FileEntry();
		expectedFileEntry.setId("test-file-1");
		expectedFileEntry.setFilename("TestTestProject.java");
		expectedFileEntry.setFilePath("src/main/java/TestTestProject.java");
		expectedFileEntry.setType(TemplateEntryTypeEnum.TEMPLATE_FILE);

		when(templateFileRenderer.processTemplateFile(eq(templateFile), eq(templateContext)))
			.thenReturn(expectedFileEntry);

		// 执行生成
		CodeGenerationResult result = codeGenerator.generate(context);

		// 验证结果
		assertTrue(result.isSuccess());
		assertNotNull(result.getFileEntries());
		assertEquals(1, result.getFileEntries().size());
	}

	@Test
	void testGenerationException() {
		// 测试生成过程中的异常处理
		TemplateFile templateFile = createTestTemplateFile();
		List<TemplateFile> templateFiles = Collections.singletonList(templateFile);

		Map<String, String> customProperties = new HashMap<>();
		customProperties.put("projectName", "TestProject");

		CodeGenerationContext context = CodeGenerationContext.builder()
			.templateGroupKey("test-group")
			.templateFiles(templateFiles)
			.customProperties(customProperties)
			.generationMode(CodeGenerationContext.GenerationMode.TEMPLATE_ONLY)
			.build();

		// Mock 模板上下文构建器
		Map<String, Object> templateContext = new HashMap<>();
		templateContext.put("projectName", "TestProject");

		when(templateContextBuilder.buildTemplateContext(eq(context), isNull())).thenReturn(templateContext);

		// Mock 模板文件渲染器抛出异常
		when(templateFileRenderer.processTemplateFile(eq(templateFile), eq(templateContext)))
			.thenThrow(new RuntimeException("Template processing failed"));

		// 执行生成
		CodeGenerationResult result = codeGenerator.generate(context);

		// 验证结果
		assertFalse(result.isSuccess());
		assertNotNull(result.getErrorMessage());
		assertTrue(result.getErrorMessage().contains("Template processing failed"));
	}

	@Test
	void testContextValidation() {
		// 测试有效上下文 - 通过生成结果来验证
		CodeGenerationContext validContext = CodeGenerationContext.builder()
			.templateGroupKey("test-group")
			.templateFiles(Collections.singletonList(createTestTemplateFile()))
			.generationMode(CodeGenerationContext.GenerationMode.TEMPLATE_ONLY)
			.build();

		// Mock 依赖
		Map<String, Object> templateContext = new HashMap<>();
		templateContext.put("projectName", "TestProject");

		when(templateContextBuilder.buildTemplateContext(eq(validContext), isNull())).thenReturn(templateContext);

		FileEntry fileEntry = new FileEntry();
		fileEntry.setId("test-file-1");
		fileEntry.setFilename("TestTestProject.java");
		fileEntry.setFilePath("src/main/java/TestTestProject.java");
		fileEntry.setType(TemplateEntryTypeEnum.TEMPLATE_FILE);
		fileEntry.setFileContent("Generated content".getBytes(StandardCharsets.UTF_8));

		when(templateFileRenderer.processTemplateFile(any(TemplateFile.class), eq(templateContext)))
			.thenReturn(fileEntry);

		CodeGenerationResult result = codeGenerator.generate(validContext);
		assertTrue(result.isSuccess());

		// 测试无效上下文 - 空的模板组标识
		CodeGenerationContext invalidContext = CodeGenerationContext.builder()
			.templateGroupKey("")
			.templateFiles(Collections.emptyList())
			.build();

		CodeGenerationResult invalidResult = codeGenerator.generate(invalidContext);
		assertFalse(invalidResult.isSuccess());
		assertEquals("无效的生成上下文", invalidResult.getErrorMessage());
	}

	@Test
	void testMultipleFileTypes() {
		// 测试多种文件类型的生成
		TemplateFile templateFile = createTestTemplateFile();
		TemplateFile binaryFile = createTestBinaryFile();
		TemplateFile folder = createTestFolder();

		List<TemplateFile> templateFiles = Arrays.asList(templateFile, binaryFile, folder);

		Map<String, String> customProperties = new HashMap<>();
		customProperties.put("projectName", "TestProject");

		CodeGenerationContext context = CodeGenerationContext.builder()
			.templateGroupKey("test-group")
			.templateFiles(templateFiles)
			.customProperties(customProperties)
			.generationMode(CodeGenerationContext.GenerationMode.TEMPLATE_ONLY)
			.build();

		// Mock 模板上下文构建器
		Map<String, Object> templateContext = new HashMap<>();
		templateContext.put("projectName", "TestProject");

		when(templateContextBuilder.buildTemplateContext(eq(context), isNull())).thenReturn(templateContext);

		// Mock 模板文件渲染器
		FileEntry templateEntry = new FileEntry();
		templateEntry.setId("test-file-1");
		templateEntry.setFilename("TestTestProject.java");
		templateEntry.setFilePath("src/main/java/TestTestProject.java");
		templateEntry.setType(TemplateEntryTypeEnum.TEMPLATE_FILE);

		FileEntry binaryEntry = new FileEntry();
		binaryEntry.setId("test-binary-1");
		binaryEntry.setFilename("test.txt");
		binaryEntry.setFilePath("src/main/resources/test.txt");
		binaryEntry.setType(TemplateEntryTypeEnum.BINARY_FILE);

		FileEntry folderEntry = new FileEntry();
		folderEntry.setId("test-folder-1");
		folderEntry.setFilename("testFolder");
		folderEntry.setFilePath("src/main/java/testFolder");
		folderEntry.setType(TemplateEntryTypeEnum.FOLDER);

		when(templateFileRenderer.processTemplateFile(eq(templateFile), eq(templateContext))).thenReturn(templateEntry);
		when(templateFileRenderer.processTemplateFile(eq(binaryFile), eq(templateContext))).thenReturn(binaryEntry);
		when(templateFileRenderer.processTemplateFile(eq(folder), eq(templateContext))).thenReturn(folderEntry);

		// 执行生成
		CodeGenerationResult result = codeGenerator.generate(context);

		// 验证结果
		assertTrue(result.isSuccess());
		assertNotNull(result.getFileEntries());
		assertEquals(3, result.getFileEntries().size());

		CodeGenerationResult.GenerationStatistics stats = result.getStatistics();
		assertEquals(3, stats.getTotalFiles());
		assertEquals(1, stats.getTemplateFiles());
		assertEquals(1, stats.getBinaryFiles());
		assertEquals(1, stats.getFolders());
	}

	private TemplateFile createTestTemplateFile() {
		TemplateFile templateFile = new TemplateFile();
		templateFile.setId("test-file-1");
		templateFile.setFilename("Test{projectName}.java");
		templateFile.setType(TemplateEntryTypeEnum.TEMPLATE_FILE);
		templateFile.setEngineType(TemplateEngineTypeEnum.VELOCITY.getType());
		templateFile.setFileContent("public class Test{projectName} {}".getBytes(StandardCharsets.UTF_8));
		templateFile.setParentFilePath("src/main/java");
		return templateFile;
	}

	private TemplateFile createTestBinaryFile() {
		TemplateFile templateFile = new TemplateFile();
		templateFile.setId("test-binary-1");
		templateFile.setFilename("test.txt");
		templateFile.setType(TemplateEntryTypeEnum.BINARY_FILE);
		templateFile.setFileContent("binary content".getBytes(StandardCharsets.UTF_8));
		templateFile.setParentFilePath("src/main/resources");
		return templateFile;
	}

	private TemplateFile createTestFolder() {
		TemplateFile templateFile = new TemplateFile();
		templateFile.setId("test-folder-1");
		templateFile.setFilename("testFolder");
		templateFile.setType(TemplateEntryTypeEnum.FOLDER);
		templateFile.setParentFilePath("src/main/java");
		return templateFile;
	}

}