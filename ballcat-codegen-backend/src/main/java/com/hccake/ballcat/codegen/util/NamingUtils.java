package com.hccake.ballcat.codegen.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

/**
 * 命名工具类 提供各种命名转换的工具方法
 *
 * @author hccake
 */
public class NamingUtils {

	/**
	 * 根据类名生成表别名 提取类名中的大写字母并转为小写
	 * @param className 类名，如 "UserInfo"
	 * @return 表别名，如 "ui"
	 */
	public static String prodAlias(String className) {
		if (StringUtils.isBlank(className)) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		for (char c : className.toCharArray()) {
			if (c >= 'A' && c <= 'Z') {
				sb.append(Character.toLowerCase(c));
			}
		}
		return sb.toString();
	}

	/**
	 * 下划线转驼峰命名
	 * @param underlineStr 下划线字符串，如 "user_name"
	 * @return 驼峰字符串，如 "UserName"
	 */
	public static String underlineToCamel(String underlineStr) {
		if (StringUtils.isBlank(underlineStr)) {
			return "";
		}
		return WordUtils.capitalizeFully(underlineStr, new char[] { '_' }).replace("_", "");
	}

	/**
	 * 移除表前缀
	 * @param tableName 表名
	 * @param tablePrefix 表前缀
	 * @return 移除前缀后的表名
	 */
	public static String removeTablePrefix(String tableName, String tablePrefix) {
		if (StringUtils.isBlank(tableName)) {
			return "";
		}

		if (StringUtils.isNotBlank(tablePrefix) && tableName.startsWith(tablePrefix)) {
			return tableName.substring(tablePrefix.length());
		}
		return tableName;
	}

	/**
	 * 驼峰转下划线命名
	 * @param camelStr 驼峰字符串，如 "UserName"
	 * @return 下划线字符串，如 "user_name"
	 */
	public static String camelToUnderline(String camelStr) {
		if (StringUtils.isBlank(camelStr)) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < camelStr.length(); i++) {
			char c = camelStr.charAt(i);
			if (Character.isUpperCase(c) && i > 0) {
				sb.append('_');
			}
			sb.append(Character.toLowerCase(c));
		}
		return sb.toString();
	}

	/**
	 * 首字母小写
	 * @param str 字符串
	 * @return 首字母小写的字符串
	 */
	public static String uncapitalize(String str) {
		return StringUtils.uncapitalize(str);
	}

	/**
	 * 首字母大写
	 * @param str 字符串
	 * @return 首字母大写的字符串
	 */
	public static String capitalize(String str) {
		return StringUtils.capitalize(str);
	}

}