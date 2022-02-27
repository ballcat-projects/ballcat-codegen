import { PageParam } from '@/api/types'

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
  // 名称
  name?: string
  // 备注
  remarks?: string
  // 创建时间
  createTime?: string
  // 更新时间
  updateTime?: string
}
