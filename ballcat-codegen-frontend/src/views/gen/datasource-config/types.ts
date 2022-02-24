import { DataSourceConfig } from '@/api/gen/datasource-config/types'

/**
 * 数据源配置编辑弹窗
 */
export interface DataSourceConfigEditModalInstance {
  add: () => void
  update: (record: DataSourceConfig) => void
}
