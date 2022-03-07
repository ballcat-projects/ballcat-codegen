/**
 * 模板附属配置信息
 */
export interface TemplateProperty {
  // ID
  id?: number
  // 模板组ID
  groupId?: number
  // 标题
  title?: string
  // 属性键
  propKey: string
  // 默认值(可为空值)
  defaultValue?: string
  // 是否必填，1：是，0：否
  required?: 1 | 0
  // 备注信息
  remarks?: string
}
