import request from '@/utils/axios'
import type { R } from '@/utils/axios/types'
import { TemplateProperty } from '@/api/gen/template-property/types'
import { PageParam, PageResult } from '@/api/types'

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
 * @param groupId 模板组id
 */
export function listTemplateProperty(groupId: number) {
  return request.get<R<TemplateProperty[]>>(`/gen/template/property/list/${groupId}`)
}
