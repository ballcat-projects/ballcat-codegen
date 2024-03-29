import type { PageParam } from '@/api/types'

/**
 * 模板组查询对象
 */
export interface TemplateGroupPageParam extends PageParam {
  // 模板组名称
  name?: string
}

/**
 * 模板组
 */
export interface TemplateGroup {
  // ID
  id?: number
  // 模板组标识
  groupKey?: string
  // 名称
  name?: string
  // 图标
  icon?: string
  // 是否使用 table
  useTable: 1 | 0
  // 备注
  remarks?: string
  // 创建时间
  createTime?: string
  // 更新时间
  updateTime?: string
}
