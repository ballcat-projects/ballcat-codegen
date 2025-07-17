package com.hccake.ballcat.codegen.util;

import cn.hutool.core.text.CharSequenceUtil;

import java.io.File;

/**
 * 文件路径工具类
 *
 * @author hccake
 */
public class FilePathUtils {

	/**
	 * 路径拼接
	 * @param parentPath 父路径
	 * @param subPath 子路径
	 * @return 拼接后的路径
	 */
	public static String concatFilePath(String parentPath, String subPath) {
		if (CharSequenceUtil.isEmpty(parentPath)) {
			return subPath;
		}
		return parentPath.endsWith(File.separator) ? parentPath + subPath : parentPath + File.separator + subPath;
	}

}
