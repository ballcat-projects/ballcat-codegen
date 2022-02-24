import request from '@/utils/axios'
import type { R } from '@/utils/axios/types'
import { PageResult, SelectData } from '@/api/types'
import { TemplateGroup, TemplateGroupPageParam } from '@/api/gen/template-group/types'

/**
 * 获取模板组下拉框数据
 */
export function listSelectData() {
  return request.get<R<SelectData[]>>('/gen/template/group/select')
}

/**
 * 分页查询
 */
export function queryTemplateGroupPage(query: TemplateGroupPageParam) {
  return request.get<R<PageResult<TemplateGroup>>>('/gen/template/group/page', {
    params: query
  })
}

/**
 * 添加模板组
 */
export function addTemplateGroup(record: TemplateGroup) {
  return request.post<R<void>>('/gen/template/group', record)
}

/**
 * 修改模板组
 */
export function updateTemplateGroup(record: TemplateGroup) {
  return request.put<R<void>>('/gen/template/group', record)
}

/**
 * 复制模板组
 */
export function copyTemplateGroup(resourceGroupId: number, record: TemplateGroup) {
  return request.post<R<void>>(`/gen/template/group/${resourceGroupId}`, record)
}

/**
 * 删除模板组
 */
export function removeTemplateGroup(recordId: number) {
  return request.delete<R<void>>(`/gen/template/group/${recordId}`)
}
