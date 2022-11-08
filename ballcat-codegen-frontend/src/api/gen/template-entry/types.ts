/**
 * 模板目录项类型
 */
export enum TemplateEntryTypeEnum {
  // 文件夹
  FOLDER = 1,
  // 模板文件
  TEMPLATE_FILE = 2,
  // 二进制文件
  BINARY_FILE = 3
}

/**
 * 模板引擎类型
 */
export class TemplateEngines {
  static readonly 1: string = 'Velocity'
  static readonly 2: string = 'Freemarker'

  static getEngineName(engineType?: number) {
    switch (engineType) {
      case 1:
        return 'Velocity'
      case 2:
        return 'Freemarker'
    }
  }
}

/**
 * 节点的删除模式枚举
 */
export enum TemplateEntryRemoveModeEnum {
  // 仅删除本身
  ONLY_ITSELF = 1,
  // 删除自己和所有子节点
  ITSELF_AND_CHILD = 2
}

/**
 * 模板目录项
 */
export interface TemplateEntry {
  // ID
  id?: string
  // 模板组标识
  groupKey?: string
  // 文件夹全路径/模板文件名称（支持占位符）
  filename?: string
  // 文件类型 1：文件夹 2：模板文件
  type: TemplateEntryTypeEnum
  // 父级Id
  parentId?: string
  // 模板内容
  templateContent: string
  // 引擎类型
  engineType?: number
  // 备注信息
  remarks?: ''
  // 创建时间
  createTime?: string
  // 更新时间
  updateTime?: string
}

/**
 * 模板目录项
 */
export type TemplateEntryDTO = TemplateEntry

/**
 * 预览模板文件
 */
export interface PreviewFile {
  // id
  id: string

  // 文件名
  filename: string

  // 完全文件路径
  filePath: string

  // 父级的完全文件路径
  parentFilePath: string

  // 类型 1：文件夹 2：文件
  type: TemplateEntryTypeEnum

  // 文件内容
  templateContent: string

  // 子文件
  children?: PreviewFile[]
}
