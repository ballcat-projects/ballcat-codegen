import request from '@/utils/axios'
import type { R } from '@/utils/axios/types'
import type { PageResult, SelectData } from '@/api/types'
import type { FieldType, FieldTypePageParam } from '@/api/gen/field-type/types'

/**
 * 分页查询
 */
export function queryFieldTypePage(query: FieldTypePageParam) {
  return request.get<R<PageResult<FieldType>>>('/gen/field-type/page', {
    params: query
  })
}

/**
 * 获取下拉框数据
 */
export function listFieldTypeSelectData() {
  return request.get<R<SelectData[]>>('/gen/field-type/select')
}

/**
 * 添加数据类型
 */
export function addFieldType(data: FieldType) {
  return request.post<R<void>>('/gen/field-type', data)
}

/**
 * 修改数据类型
 */
export function updateFieldType(data: FieldType) {
  return request.put<R<void>>('/gen/field-type', data)
}

/**
 * 删除数据类型
 */
export function removeFieldType(id?: number) {
  return request.delete<R<void>>(`/gen/field-type/${id}`)
}
