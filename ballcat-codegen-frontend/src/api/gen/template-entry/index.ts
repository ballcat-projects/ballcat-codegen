import request from '@/utils/axios'
import { R } from '@/utils/axios/types'
import { TemplateEntry, TemplateEntryRemoveModeEnum } from '@/api/gen/template-entry/types'

/**
 * 获取模板项列表
 * @param templateGroupId
 */
export function listTemplateEntry(templateGroupId: number) {
  return request.get<R<TemplateEntry[]>>(`/gen/template-entry/list/${templateGroupId}`)
}

/**
 * 添加模板目录项
 * @param record
 */
export function addTemplateEntry(record: TemplateEntry) {
  return request.post<R<void>>('/gen/template-entry', record)
}

/**
 * 修改模板目录项
 * @param record
 */
export function updateTemplateEntry(record: TemplateEntry) {
  return request.put<R<void>>('/gen/template-entry', record)
}

/**
 * 删除模板目录项
 * @param id 目录项id
 * @param mode 删除模式
 */
export function removeTemplateEntry(id?: number, mode?: TemplateEntryRemoveModeEnum) {
  return request.delete<R>(`/gen/template-entry/${id}`, {
    params: { mode: mode }
  })
}

/**
 * 移动模板项
 * @param entryId 被移动的目录项ID
 * @param targetEntryId 目标目录项ID
 * @param horizontalMove 是否移动到目标目录平级，否则移动到其内部
 */
export function moveEntry(entryId?: number, targetEntryId?: number, horizontalMove?: boolean) {
  return request.patch(`/gen/template-entry/${entryId}/position`, null, {
    params: { targetEntryId: targetEntryId, horizontalMove: horizontalMove }
  })
}

/**
 * 修改模板内容
 */
export function updateTemplateEntryContent(id: number, content: string) {
  const params = new URLSearchParams()
  params.append('id', String(id))
  params.append('content', content)
  return request.patch<R<void>>('/gen/template-entry/content', params, {
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}
