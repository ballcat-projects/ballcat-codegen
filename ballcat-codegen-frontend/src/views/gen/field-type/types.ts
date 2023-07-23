import type { FieldType } from '@/api/gen/field-type/types'

/**
 * 数据源配置编辑弹窗
 */
export interface FieldTypeEditModalInstance {
  add: () => void
  update: (record: FieldType) => void
}
