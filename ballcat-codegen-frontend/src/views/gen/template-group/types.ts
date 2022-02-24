import { TemplateGroup } from '@/api/gen/template-group/types'

/**
 * 模板配置弹窗实例接口
 */
export interface TemplatePropertyModalInstance {
  open: (templateGroup: TemplateGroup) => void
}

/**
 * 模板组表单弹窗实例接口
 */
export interface TemplateGroupFormModalInstance {
  // 新建
  add: () => void
  // 更新
  update: (templateGroup: TemplateGroup) => void
  // 复制模板组
  copy: (templateGroup: TemplateGroup) => void
}
