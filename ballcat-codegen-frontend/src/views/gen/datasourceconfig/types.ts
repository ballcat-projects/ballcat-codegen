import { DataSourceConfig } from '@/api/gen/model/datasourceconfig'

/**
 * 数据源配置编辑弹窗
 */
export interface DataSourceConfigEditModalInstance {
  add: () => void
  update: (record: DataSourceConfig) => void
}
