import request from '@/utils/axios'
import type { R } from '@/utils/axios/types'
import { PageResult, SelectData } from '@/api/types'
import { DataSourceConfig, DataSourcePageParam } from '@/api/gen/datasource-config/types'

/**
 * 分页查询
 */
export function queryDatasourceConfigPage(query: DataSourcePageParam) {
  return request.get<R<PageResult<DataSourceConfig>>>('/gen/datasource-config/page', {
    params: query
  })
}

/**
 * 获取下拉框数据
 */
export function listDatasourceConfigSelectData() {
  return request.get<R<SelectData[]>>('/gen/datasource-config/select')
}

/**
 * 添加数据源配置
 */
export function addDatasourceConfig(data: DataSourceConfig) {
  return request.post<R<void>>('/gen/datasource-config', data)
}

/**
 * 修改数据源配置
 */
export function updateDatasourceConfig(data: DataSourceConfig) {
  return request.put<R<void>>('/gen/datasource-config', data)
}

/**
 * 删除数据源配置
 */
export function removeDatasourceConfig(id?: number) {
  return request.get<R<void>>(`/gen/datasource-config/${id}`)
}
