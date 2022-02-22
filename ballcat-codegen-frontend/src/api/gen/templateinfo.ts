import request from '@/utils/axios'
import type { R } from '@/utils/axios/types'
import type { TemplateInfo } from '@/api/gen/model/templateinfo'

/**
 * 获取模板组对应的文件信息列表
 * @param groupId 模板组id
 */
export function listTemplateInfo(groupId: number) {
  return request.get<R<TemplateInfo[]>>(`/gen/template/info/list/${groupId}`)
}

/**
 * 获取对应的模板信息
 * @param id 模板目录项id
 */
export function getTemplateInfo(id?: number) {
  return request.get<R<TemplateInfo>>(`/gen/template/info/${id}`)
}

/**
 * 修改模板内容
 * @param data 模板内容信息
 */
export function updateTemplateInfoContent(data: { directoryEntryId: number; content: string }) {
  return request.patch<R<void>>('/gen/template/info/content', data)
}
