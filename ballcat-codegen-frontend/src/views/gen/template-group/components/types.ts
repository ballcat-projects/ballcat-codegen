import { TemplateEntry } from '@/api/gen/template-entry/types'

/**
 * 模板项内容编辑器实例
 */
export interface TemplateContentEditorInstance {
  // 检查编辑器中打开的文件的存储状态，全部已保存返回 true
  checkSaveState: () => boolean
  // 编辑 TemplateInfo
  editContent: (entry: TemplateEntry) => void
}

/**
 * 模板项表单弹窗实例
 */
export interface TemplateEntryFormModalInstance {
  add: (currentParentFilename: string, record: TemplateEntry) => void
  update: (record: TemplateEntry) => void
}

/**
 * 模板项删除弹窗实例
 */
export interface TemplateEntryRemoveModalInstance {
  open: (templateEntryId: number) => void
}
