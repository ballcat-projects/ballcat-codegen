/**
 * 模板附属配置信息
 */
export interface TemplateProperty {
  // ID
  id?: number
  // 模板组标识
  groupKey?: string
  // 标题
  title?: string
  // 属性键
  propKey: string
  // 默认值(可为空值)
  defaultValue?: string
  // 是否必填，1：是，0：否
  required?: 1 | 0
  // 组件类型
  componentType: ComponentType
  // 组件选项
  componentOptions: ComponentOption[]
  // 备注信息
  remarks?: string
}

export interface ComponentOption {
  name: string
  value: string
}

export enum ComponentType {
  INPUT = 'input',
  INPUT_NUMBER = 'input-number',
  SELECT = 'select',
  RADIO = 'radio'
}
