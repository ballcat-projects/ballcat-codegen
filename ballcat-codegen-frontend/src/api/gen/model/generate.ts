import { PageParam } from '@/api/types'

/**
 * 数据库表信息查询对象
 */
export interface TableInfoPageParam extends PageParam {
  // 表名
  tableName?: string
}

/**
 * 数据库中的表信息
 */
export interface TableInfo {
  // 表名
  tableName: string
  // 存储引擎
  engine: string
  // 表备注
  tableComment: string
  // 创建时间
  createTime: string
}

/**
 * 代码生成的选项
 */
export interface GeneratorOption {
  // 表名称集合
  tableNames: string[]
  // 模板组Id
  templateGroupId: number | null
  // 模板项Ids
  templateEntryIds: number[]
  // 表前缀
  tablePrefix?: string
  // 生成的一些配置
  genProperties: {
    [key: string]: string | undefined | null
  }
}

/**
 * 模板目录项
 */
export interface TemplateEntryTree {
  // 模板组Id
  groupId: number
  // 文件夹全路径/模板文件名称（支持占位符）
  fileName: string
  // 文件类型 1：文件夹 2：模板文件
  type: 1 | 2
  // 文件内容
  content?: string
}
