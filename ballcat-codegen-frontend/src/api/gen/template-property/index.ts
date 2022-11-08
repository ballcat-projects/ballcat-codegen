import request from '@/utils/axios'
import type { R } from '@/utils/axios/types'
import type { TemplateProperty } from '@/api/gen/template-property/types'
import type { PageParam, PageResult } from '@/api/types'

/**
 * 分页查询
 */
export function queryTemplatePropertyPage(query: PageParam) {
  return request.get<R<PageResult<TemplateProperty>>>('/gen/template/property/page', {
    params: query
  })
}

/**
 * 添加模板配置
 */
export function addTemplateProperty(record: TemplateProperty) {
  return request.post<R<void>>('/gen/template/property', record)
}

/**
 * 修改模板配置
 */
export function updateTemplateProperty(record: TemplateProperty) {
  return request.put<R<void>>('/gen/template/property', record)
}

/**
 * 删除模板配置
 */
export function removeTemplateProperty(templatePropertyId: number) {
  return request.delete<R<void>>(`/gen/template/property/${templatePropertyId}`)
}

/**
 * 获取模板组对应的配置列表
 * @param templateGroupKey 模板组标识
 */
export function listTemplateProperty(templateGroupKey: string) {
  return request.get<R<TemplateProperty[]>>(`/gen/template/property/list/${templateGroupKey}`)
}

/**
 * 导入模板组属性
 */
export function importTemplateGroupProperties(groupKey: string, file: File) {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('groupKey', groupKey)

  return request.post(`/gen/template/property/import`, formData)
}

/**
 * 导出模板组属性
 */
export function exportTemplateGroupProperties(groupKey: string) {
  return request.get(`/gen/template/property/export?groupKey=${groupKey}`, {
    responseType: 'blob'
  })
}
