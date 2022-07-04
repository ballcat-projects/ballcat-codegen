package com.hccake.ballcat.codegen.config;

import com.hccake.ballcat.codegen.model.entity.TypeScriptType;
import com.hccake.ballcat.codegen.service.TypeScriptTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

import static com.hccake.ballcat.codegen.typescript.TypeScriptTypeConverter.JAVA_TO_TS;

/**
 * software：IntelliJ IDEA 2022.1
 * class name: DbTypeManage
 * class description： 数据库类型管理器
 *
 * @author MoBaiJun 2022/7/4 14:46
 */
@Component
@RequiredArgsConstructor
public class DbTypeManage {
	private final TypeScriptTypeService typeScriptTypeService;

	@PostConstruct
	public void setTypeScriptType() {
		List<TypeScriptType> list = typeScriptTypeService.list();
		list.forEach(temp -> JAVA_TO_TS.put(temp.getCodeKey(), temp.getCodeValue()));
	}
}
