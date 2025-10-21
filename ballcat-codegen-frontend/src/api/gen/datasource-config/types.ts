import type { PageParam } from '@/api/types'

/**
 * 数据源配置查询对象
 */
export interface DataSourcePageParam extends PageParam {
  // 数据源标题
  title?: string
}

/**
 * 数据源配置
 */
export interface DataSourceConfig {
  // ID
  id?: number
  // 标题
  title: string
  // dsKey
  dsKey?: string
  // 用户名
  username?: string
  // 提交表单时的密码参数
  pass?: string
  // 密码
  password?: string
  // 数据源连接
  url?: string
  // 创建时间
  createTime?: string
  // 更新时间
  updateTime?: string
}
