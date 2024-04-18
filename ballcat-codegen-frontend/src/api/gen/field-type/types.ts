import type {PageParam} from '@/api/types'

/**
 * 数据类型查询对象
 */
export interface FieldTypePageParam extends PageParam {
  // ID
  id?: number
  // 模板组标识
  groupKey?: string
  // 数据库类型（1:MySQL，2:Oracle，3:PostGreSql，4:SqlServer）
  dbType?: string
}

/**
 * 数据类型
 */
export interface FieldType {
  // ID
  id?: number
  // 唯一标识
  groupKey: string
  // 是否默认值
  defaultValue?: string
  // DB属性类型
  columnKey?: string
    // Java对应数据类型
    columnValue?: string
  // 数据库类型
  dbType?: string
  // 属性包路径+类名
  packageName?: string
  // 创建时间
  createTime?: string
}

/**
 * 数据库类型（1:MySQL，2:Oracle，3:PostGreSql，4:SqlServer）
 */
export enum DbType {
  MySQL = '1',
  Oracle = '2',
  PostGreSql = '3',
  SqlServer = '4'
}
