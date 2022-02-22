/**
 * 模板信息
 */
export interface TemplateInfo {
  // 目录项ID
  directoryEntryId?: number
  // 模板组ID
  groupId?: number
  // 模板标题
  title: string
  // 模板内容
  content?: string
  // 模板引擎类型 1：velocity
  engineType: number
  // 备注信息
  remarks: string
}
