import { PageParam } from '@/api/types'

/**
 * 数据源配置查询对象
 */
export interface DataSourcePageParam extends PageParam {
  // 数据源名称
  name?: string
}

/**
 * 数据源配置
 */
export interface DataSourceConfig {
  // ID
  id?: number
  // 名称
  name?: string
  // 用户名
  username?: string
  // 密码
  password?: string
  // 数据源连接
  url?: string
  // 创建时间
  createTime?: string
  // 更新时间
  updateTime?: string
}
