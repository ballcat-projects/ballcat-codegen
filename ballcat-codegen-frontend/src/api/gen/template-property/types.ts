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
  // 属性类型：1=配置属性，2=计算属性
  propType: number
  // 表达式，计算属性必填
  expression?: string
  // 模板引擎类型，用于计算属性的模板引擎选择
  engineType?: number
  // 默认值(可为空值)
  defaultValue?: string
  // 组件类型
  componentType?: ComponentType
  // 组件选项
  componentOptions?: ComponentOption[]
  // 是否必填，1：是，0：否
  required?: 1 | 0
  // 排序值
  orderValue: number
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

/**
 * 属性类型。
 */
export enum PropType {
  CONFIG =  1, // 配置属性
  COMPUTED = 2 // 计算属性
}
