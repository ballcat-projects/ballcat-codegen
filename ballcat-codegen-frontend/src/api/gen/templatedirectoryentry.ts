import request from '@/utils/axios'
import { R } from '@/utils/axios/types'
import { TemplateDirectoryEntry } from '@/api/gen/model/templatedirectoryentry'
import { TemplateEntryRemoveModeEnum } from '@/views/gen/templategroup/components/types'

/**
 * 获取模板项列表
 * @param templateGroupId
 */
export function listTemplateEntry(templateGroupId: number) {
  return request.get<R<TemplateDirectoryEntry[]>>(
    `/gen/template/directory-entry/list/${templateGroupId}`
  )
}

/**
 * 添加模板目录项
 * @param record
 */
export function addTemplateEntry(record: TemplateDirectoryEntry) {
  return request.post<R<void>>('/gen/template/directory-entry', record)
}

/**
 * 修改模板目录项
 * @param record
 */
export function updateTemplateEntry(record: TemplateDirectoryEntry) {
  return request.put<R<void>>('/gen/template/directory-entry', record)
}

/**
 * 删除模板目录项
 * @param id 目录项id
 * @param mode 删除模式
 */
export function removeTemplateEntry(id?: number, mode?: TemplateEntryRemoveModeEnum) {
  return request.delete<R>(`/gen/template/directory-entry/${id}`, {
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
  return request.patch(`/gen/template/directory-entry/${entryId}/position`, null, {
    params: { targetEntryId: targetEntryId, horizontalMove: horizontalMove }
  })
}
