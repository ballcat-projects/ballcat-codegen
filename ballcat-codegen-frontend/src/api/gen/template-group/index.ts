import request from '@/utils/axios'
import type { R } from '@/utils/axios/types'
import type { PageResult, SelectData } from '@/api/types'
import type { TemplateGroup, TemplateGroupPageParam } from '@/api/gen/template-group/types'

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
export function copyTemplateGroup(resourceGroupKey: string, record: TemplateGroup) {
  return request.post<R<void>>(`/gen/template/group/${resourceGroupKey}`, record)
}

/**
 * 删除模板组
 */
export function removeTemplateGroup(groupKey: string) {
  return request.delete<R<void>>(`/gen/template/group/${groupKey}`)
}

/**
 * 导入模板组属性
 */
export function importTemplateGroupProperties(groupKey: string, file: File) {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('groupKey', groupKey)

  return request.post(`/gen/template/group/import/property`, formData)
}

/**
 * 导出模板组属性
 */
export function exportTemplateGroupProperties(groupKey: string) {
  return request.get(`/gen/template/group/export/property?groupKey=${groupKey}`, {
    responseType: 'blob'
  })
}

/**
 * 导入模板组文件
 */
export function importTemplateGroupEntries(groupKey: string, file: File) {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('groupKey', groupKey)

  return request.post(`/gen/template/group/import/entry`, formData)
}


/**
 * 导出模板组文件
 */
export function exportTemplateGroupEntries(groupKey: string) {
  return request.get(`/gen/template/group/export/entry?groupKey=${groupKey}`, {
    responseType: 'blob'
  })
}
