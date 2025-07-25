<template>
  <div class="table-select-step">
    <!-- 无需数据源的模板组 -->
    <template v-if="!generatorConfigStore.isUseTable">
      <div class="no-datasource-container">
        <a-result
          status="info"
          title="无需数据源"
          sub-title="当前选择的模板组不需要数据源，可以直接进行代码生成"
        >
          <template #icon>
            <CodeOutlined style="color: #52c41a" />
          </template>
        </a-result>
      </div>
    </template>

    <!-- 需要数据源的模板组 -->
    <template v-else>
      <div class="step-content">
        <div class="main-content">
          <a-row :gutter="24">
            <!-- 数据源选择面板 -->
            <a-col :span="6">
              <div class="datasource-panel">
                <div class="panel-header">
                  <div class="header-content">
                    <DatabaseOutlined class="header-icon" />
                    <span class="header-title">数据源</span>
                  </div>
                  <div class="header-actions">
                    <a-tooltip title="刷新数据源">
                      <a-button
                        type="text"
                        size="small"
                        :loading="datasourceLoading"
                        @click="refreshData"
                      >
                        <ReloadOutlined />
                      </a-button>
                    </a-tooltip>
                    <a-tooltip title="管理数据源">
                      <a-button type="text" size="small" @click="openDatasourcePage">
                        <PlusOutlined />
                      </a-button>
                    </a-tooltip>
                  </div>
                </div>

                <div class="panel-content">
                  <a-spin :spinning="datasourceLoading">
                    <template v-if="dataSourceSelectData.length > 0">
                      <a-radio-group v-model:value="selectedDsNames" style="width: 100%">
                        <div
                          v-for="item in dataSourceSelectData"
                          :key="item.value"
                          class="datasource-item"
                        >
                          <a-radio :value="item.value" class="datasource-radio">
                            <div class="datasource-info">
                              <div class="datasource-name">{{ item.name }}</div>
                              <div class="datasource-desc">{{ item.value }}</div>
                            </div>
                          </a-radio>
                        </div>
                      </a-radio-group>
                    </template>
                    <template v-else>
                      <a-empty description="暂无数据源">
                        <a-button type="primary" @click="openDatasourcePage">
                          <PlusOutlined />
                          添加数据源
                        </a-button>
                      </a-empty>
                    </template>
                  </a-spin>
                </div>
              </div>
            </a-col>

            <!-- 表格选择面板 -->
            <a-col :span="18">
              <div class="table-panel">
                <div class="panel-header">
                  <div class="header-content">
                    <TableOutlined class="header-icon" />
                    <span class="header-title">数据表</span>
                    <a-tag v-if="selectedRowKeys && selectedRowKeys.length > 0" color="blue">
                      已选择 {{ selectedRowKeys.length }} 张表
                    </a-tag>
                  </div>
                  <div class="header-actions">
                    <a-input-search
                      v-model:value="searchKeyword"
                      placeholder="搜索表名..."
                      style="width: 200px"
                      @search="handleTableSearch"
                    />
                  </div>
                </div>

                <div class="panel-content">
                  <a-table
                    :columns="enhancedColumns"
                    :data-source="dataSource"
                    :pagination="pagination"
                    :loading="loading"
                    :scroll="{ y: 'calc(100vh - 400px)' }"
                    row-key="tableName"
                    @change="tableState.handleTableChange"
                    :row-selection="{
                      selectedRowKeys: selectedRowKeys,
                      onChange: onSelectChange
                    }"
                  />
                </div>
              </div>
            </a-col>
          </a-row>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, watch, computed } from 'vue'
import {
  SearchOutlined,
  ReloadOutlined,
  PlusOutlined,
  DatabaseOutlined,
  TableOutlined,
  CodeOutlined,
  ThunderboltOutlined
} from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import { queryTableInfoPage } from '@/api/gen/generate'
import { listDatasourceConfigSelectData } from '@/api/gen/datasource-config'
import type { ColumnProps } from 'ant-design-vue/es/table'
import type { TableInfo } from '@/api/gen/generate/types'
import type { TableInfoPageParam } from '@/api/gen/generate/types'
import useTable from '@/hooks/table'
import { doRequest } from '@/utils/axios/request'
import type { SelectData } from '@/api/types'
import type { GenerateStepInstance } from './types'
import { useGeneratorConfigStore } from '@/store'
import { useRouter } from 'vue-router'

const generatorConfigStore = useGeneratorConfigStore()
const router = useRouter()

// 搜索关键词
const searchKeyword = ref<string>('')

// 数据源相关
const dataSourceSelectData = ref<SelectData[]>([])
const selectedDsNames = ref<string>('')
const datasourceLoading = ref(false)

// 使用 table hook
const tableState = useTable<TableInfo>({
  pageRequest: (pageParams: TableInfoPageParam) => {
    const params = {
      ...pageParams,
      tableName: searchKeyword.value || undefined
    }
    return queryTableInfoPage(selectedDsNames.value || '', params)
  }
})

const { dataSource, pagination, loading, selectedRowKeys, onSelectChange } = tableState

// 表格列配置
const enhancedColumns = computed<ColumnProps[]>(() => [
  {
    title: '表名',
    dataIndex: 'tableName',
    key: 'tableName',
    width: 280,
    sorter: (a: unknown, b: unknown) => {
      const tableA = a as TableInfo
      const tableB = b as TableInfo
      return tableA.tableName.localeCompare(tableB.tableName)
    }
  },
  {
    title: '引擎',
    dataIndex: 'engine',
    key: 'engine',
    width: 100
  },
  {
    title: '表备注',
    dataIndex: 'tableComment',
    key: 'tableComment',
    ellipsis: true,
    customFilterDropdown: true,
    onFilter: (value: string | number | boolean, record: unknown) => {
      const tableInfo = record as TableInfo
      return (tableInfo.tableComment || '').toLowerCase().includes(String(value).toLowerCase())
    }
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    key: 'createTime',
    width: 200,
    sorter: true
  }
])

// 加载数据源列表
const loadDataSources = () => {
  datasourceLoading.value = true
  doRequest({
    request: listDatasourceConfigSelectData(),
    onSuccess: res => {
      dataSourceSelectData.value = res.data || []
      if (dataSourceSelectData.value.length > 0 && !selectedDsNames.value) {
        selectedDsNames.value = dataSourceSelectData.value[0].value
      }
    },
    onError: () => {
      message.error('加载数据源失败')
    },
    onFinally: () => {
      datasourceLoading.value = false
    }
  })
}

// 刷新所有数据
const refreshData = () => {
  loadDataSources()
  if (selectedDsNames.value) {
    tableState.loadData()
  }
}

// 表格搜索
const handleTableSearch = (value: string) => {
  searchKeyword.value = value
  // 重置到第一页并重新加载数据
  tableState.loadData()
}

// 跳转到数据源配置页面
const openDatasourcePage = () => {
  router.push({ name: 'DataSourceConfig' })
}

// 监听数据源选择变化
watch(
  () => selectedDsNames.value,
  () => {
    if (selectedDsNames.value) {
      tableState.loadData()
      generatorConfigStore.dsName = selectedDsNames.value
    }
  }
)

// 组件初始化
onMounted(() => {
  loadDataSources()
})

// 组件暴露的方法
defineExpose<GenerateStepInstance>({
  enter: loadDataSources,
  validate: () => {
    if (!generatorConfigStore.isUseTable) {
      return Promise.resolve()
    }

    if (!selectedDsNames.value || selectedDsNames.value.length === 0) {
      return Promise.reject({ message: '请选择一个数据源' })
    }

    if (!selectedRowKeys.value || selectedRowKeys.value.length === 0) {
      return Promise.reject({ message: '请至少选择一张表' })
    }

    return Promise.resolve()
  },
  next: () => {
    if (generatorConfigStore.isUseTable) {
      generatorConfigStore.dsName = selectedDsNames.value
      generatorConfigStore.options.tableNames = selectedRowKeys.value as string[]
    }
  }
})
</script>

<style lang="less" scoped>
.table-select-step {
  .no-datasource-container {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 400px;
    padding: @spacing-xl;

    .template-info {
      text-align: center;

      h4 {
        margin: 0 0 @spacing-sm;
        color: @text-color-primary;
        font-size: @font-size-lg;
      }

      p {
        margin: 0;
        color: @text-color-secondary;
        font-size: @font-size-base;
      }
    }
  }

  .step-content {
    padding: 0;
    height: 100%;
    display: flex;
    flex-direction: column;
  }

  .main-content {
    flex: 1;
    display: flex;
    flex-direction: column;
    padding: @spacing-lg;

    :deep(.ant-row) {
      flex: 1;
      display: flex;
    }

    :deep(.ant-col) {
      display: flex;
      flex-direction: column;
    }

    .datasource-panel,
    .table-panel {
      background: @component-background;
      border-radius: @border-radius-base;
      box-shadow: @box-shadow-base;
      overflow: hidden;
      flex: 1;
      display: flex;
      flex-direction: column;

      .panel-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: @spacing-md @spacing-lg;
        background: @background-color-light;
        border-bottom: 1px solid @border-color-base;
        height: 64px;
        min-height: 64px;

        .header-content {
          display: flex;
          align-items: center;
          gap: @spacing-sm;

          .header-icon {
            color: @primary-color;
            font-size: @font-size-lg;
          }

          .header-title {
            font-weight: @font-weight-medium;
            font-size: @font-size-base;
            color: @text-color;
          }
        }

        .header-actions {
          display: flex;
          align-items: center;
          gap: @spacing-xs;
        }
      }

      .panel-content {
        padding: @spacing-lg;
      }
    }

    // 数据源面板特殊样式
    .datasource-panel {
      .panel-content {
        padding: @spacing-lg;
        flex: 1;
        min-height: 0;
        max-height: calc(100vh - 400px);
        overflow-y: auto;

        // 优化滚动条样式
        &::-webkit-scrollbar {
          width: 6px;
        }

        &::-webkit-scrollbar-track {
          background: #f1f1f1;
          border-radius: 3px;
        }

        &::-webkit-scrollbar-thumb {
          background: #c1c1c1;
          border-radius: 3px;

          &:hover {
            background: #a8a8a8;
          }
        }
      }
    }

    // 表格面板特殊样式 - 移除内边距让表格无缝融合
    .table-panel {
      .panel-content {
        padding: 0;

        // 移除表格的外边框，让它与面板融合
        :deep(.ant-table-wrapper) {
          border: none;
          border-radius: 0;
          box-shadow: none;

          .ant-table {
            border-radius: 0;

            .ant-table-container {
              border-radius: 0;
            }

            .ant-table-thead > tr > th {
              border-top: none;

              &:first-child {
                border-left: none;
              }

              &:last-child {
                border-right: none;
              }
            }

            .ant-table-tbody > tr > td {
              &:first-child {
                border-left: none;
              }

              &:last-child {
                border-right: none;
              }
            }

            // 最后一行去掉下边框
            .ant-table-tbody > tr:last-child > td {
              border-bottom: none;
            }
          }

          // 分页器样式调整
          .ant-pagination {
            margin: @spacing-md @spacing-lg @spacing-lg;
            border-top: 1px solid @border-color-base;
            padding-top: @spacing-md;
          }
        }
      }
    }

    .datasource-panel {
      .panel-header {
        flex-shrink: 0;
      }

      .datasource-item {
        margin-bottom: @spacing-sm;
        padding: @spacing-sm;
        border: 1px solid @border-color-base;
        border-radius: @border-radius-base;
        transition: all @animation-duration-slow;

        &:hover {
          border-color: @primary-color;
          box-shadow: @box-shadow-base;
        }

        .datasource-radio {
          width: 100%;

          .datasource-info {
            margin-left: @spacing-sm;

            .datasource-name {
              font-weight: @font-weight-medium;
              color: @text-color;
              margin-bottom: @spacing-xs;
            }

            .datasource-desc {
              font-size: @font-size-sm;
              color: @text-color-secondary;
            }
          }
        }
      }
    }

    .table-panel {
      .panel-header {
        flex-shrink: 0;
      }

      .panel-content {
        flex: 1;
        overflow: visible;
      }
    }
  }
}

// 响应式设计
@media (max-width: @screen-lg) {
  .table-select-step {
    .main-content {
      :deep(.ant-col:first-child) {
        flex: 0 0 100%;
        max-width: 100%;
        margin-bottom: @spacing-lg;
      }

      :deep(.ant-col:last-child) {
        flex: 0 0 100%;
        max-width: 100%;
      }

      .datasource-panel {
        height: auto;
        max-height: 300px;

        .panel-content {
          height: auto;
          max-height: 236px; // 300px - 64px (头部高度)
        }
      }

      .table-panel {
        height: auto;
        max-height: 300px;

        .panel-content {
          height: auto;
          max-height: 236px; // 300px - 64px (头部高度)
        }
      }
    }
  }
}
</style>
