import request from '@/utils/axios'
import type { R } from '@/utils/axios/types'
import type { PageResult } from '@/api/types'
import type { TableInfo, TableInfoPageParam } from '@/api/gen/generate/types'
import { GeneratorOption } from '@/api/gen/generate/types'
import { FileEntry } from '@/api/gen/template-entry/types'

/**
 * 获取表信息
 * @param dsName 数据源名称
 * @param query 查询参数
 */
export function queryTableInfoPage(dsName: string, query: TableInfoPageParam) {
  return request.get<R<PageResult<TableInfo>>>('/table-info/page', {
    params: query,
    headers: { dsName: dsName }
  })
}

/**
 * 代码生成
 * @param dsName 数据源名称
 * @param generatorOption 生成配置
 */
export function generate(dsName: string, generatorOption: GeneratorOption) {
  return request.post('/generate', generatorOption, {
    headers: { dsName: dsName },
    responseType: 'blob'
  })
}

/**
 * 代码预览
 * @param dsName 数据源名称
 * @param generatorOption 生成配置
 */
export function preview(dsName: string, generatorOption: GeneratorOption) {
  return request.post<R<FileEntry[]>>('/preview', generatorOption, {
    headers: { dsName: dsName }
  })
}
