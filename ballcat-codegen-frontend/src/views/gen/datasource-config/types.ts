import type { DataSourceConfig } from '@/api/gen/datasource-config/types'

/**
 * 数据源配置编辑弹窗
 */
export interface DataSourceConfigEditModalInstance {
  add: () => void
  update: (record: DataSourceConfig) => void
}

/**
 * 数据源表单组件实例接口
 */
export interface DataSourceFormInstance {
  /**
   * 打开表单以添加新数据源
   */
  add(): void

  /**
   * 打开表单以编辑现有数据源
   * @param record 要编辑的数据源记录
   */
  edit(record: DataSourceConfig): void
}

