/**
 * 分页查询参数
 */
export interface PageParam {
  // 当前页码
  current?: number
  // 每页显示条数
  size?: number
  // 排序字段
  sortFields?: string
  // 排序方式, asc升序, desc降序
  sortOrders?: 'asc' | 'desc'
}

/**
 * 分页返回结果
 */
export interface PageResult<T> {
  // 分页数据
  records: T[]
  // 数据总量
  total: number
}

/**
 * 下拉框数据
 */
export interface SelectData<T = unknown> {
  // 显示名称
  name: string
  // 选中值
  value: string
  // 默认是否被选中
  selected?: boolean
  // 是否被禁用
  disabled?: boolean
  // 类型，可分组
  type?: string
  // 扩展对象
  extendObj?: T
}
