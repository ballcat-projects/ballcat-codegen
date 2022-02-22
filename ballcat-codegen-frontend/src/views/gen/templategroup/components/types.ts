import { TemplateDirectoryEntry } from '@/api/gen/model/templatedirectoryentry'

/**
 * 模板组表单弹窗实例
 */
export interface TemplateInfoEditorInstance {
  // 检查编辑器中打开的文件的存储状态，全部已保存返回 true
  checkSaveState: () => boolean
  // 编辑 TemplateInfo
  editTemplateInfo: (entry: TemplateDirectoryEntry) => void
}

/**
 * 模板项表单弹窗实例
 */
export interface TemplateEntryFormModalInstance {
  add: (currentParentFileName: string, record: TemplateDirectoryEntry) => void
  update: (record: TemplateDirectoryEntry) => void
}

/**
 * 模板项删除弹窗实例
 */
export interface TemplateEntryRemoveModalInstance {
  open: (templateEntryId: number) => void
}

/**
 * 节点的删除模式枚举
 */
export enum TemplateEntryRemoveModeEnum {
  // 仅删除本身
  ONLY_ITSELF = 1,
  // 删除自己和所有子节点
  ITSELF_AND_CHILD = 2
}

/**
 * 模板内容
 */
export interface TemplateContent {
  id: number
  content: string
  fileName: string
}
