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
  <div class="main-content" :style="panelHeightVars">
          <a-row :gutter="24">
            <!-- 数据源选择面板 -->
            <a-col :span="6">
              <div class="panel datasource-panel">
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
                        <template #icon>
                          <ReloadOutlined />
                        </template>
                      </a-button>
                    </a-tooltip>
                    <a-tooltip title="管理数据源">
                      <a-button type="text" size="small" @click="openDatasourcePage">
                        <template #icon>
                          <PlusOutlined />
                        </template>
                      </a-button>
                    </a-tooltip>
                  </div>
                </div>

                <div class="panel-content" :style="{ maxHeight: tablePanelMaxPx }">
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
                          <template #icon>
                            <PlusOutlined />
                          </template>
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
              <div class="panel table-panel">
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
                    :scroll="tableScroll"
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

// 面板内容高度与表格滚动统一：最多按 10 行表格内容高度显示
const TABLE_ROW_H = 54 // 依据默认尺寸，若全局设为 small/middle 可改为 40/48
const TABLE_THEAD_H = 54 // 估算表头高度
const TABLE_PAGINATION_H = 72 // 估算分页区块（含间距）高度，可按主题调整
const MAX_VISIBLE_ROWS = 10
// 数据源面板的最大高度：等于 10 行表体高度
const datasourceMaxPx = computed(() => `${TABLE_ROW_H * MAX_VISIBLE_ROWS}px`)
// 表格面板内容最大高度：表头 + 10 行表体 + 分页区块
const tablePanelMaxPx = computed(
  () => `${TABLE_THEAD_H + TABLE_ROW_H * MAX_VISIBLE_ROWS + TABLE_PAGINATION_H}px`
)
// 注入到容器的 CSS 变量，便于样式与媒体查询覆盖
const panelHeightVars = computed(() => ({
  "--panel-ds-max": datasourceMaxPx.value,
  "--panel-table-max": tablePanelMaxPx.value
}))
// 表格滚动：可见行数不超过 10；超过则按 10 行高度出现滚动
const pageSize = computed(() => pagination.value?.pageSize ?? MAX_VISIBLE_ROWS)
const visibleRows = computed(() => Math.min(pageSize.value, MAX_VISIBLE_ROWS))
const tableScroll = computed(() => {
  const len = dataSource.value?.length ?? 0
  const y = TABLE_ROW_H * visibleRows.value
  return len > visibleRows.value ? { y } : undefined
})

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
  padding: @spacing-lg; // 紧凑的外围留白，保证与网格 gutter 视觉对齐
  .no-datasource-container {
    display: flex;
    justify-content: center;
    align-items: center;

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
  padding: 0;

    :deep(.ant-row) {
      display: flex;
      align-items: stretch; // 保持两侧面板等高（以更高的一侧为准）
    }

    :deep(.ant-col) {
      display: flex;
      flex-direction: column;
    }

  // 复用全局 .panel 样式

    // 数据源面板特殊样式
    .datasource-panel {
      .panel-content {
        flex: 1;
        min-height: 0;
        overflow-y: auto;

        // 优化滚动条样式
        &::-webkit-scrollbar {
          width: 6px;
        }

        &::-webkit-scrollbar-track {
          background: @gray-3;
          border-radius: 3px;
        }

        &::-webkit-scrollbar-thumb {
          background: @gray-5;
          border-radius: 3px;

          &:hover {
            background: @gray-6;
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
        .header-actions {
          /* 按钮内图标水平/垂直居中，不改变按钮本身的 display */
          :deep(.ant-btn .anticon) {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            margin: 0;
            line-height: 1;
            vertical-align: -0.125em;
          }
          :deep(.ant-btn .anticon svg) {
            display: block;
          }
        }
      }

      .datasource-item {
  margin-bottom: @spacing-sm;
  padding: @spacing-sm;
  border: 1px solid @border-color-base;
  border-radius: @border-radius-base;
  transition: all @animation-duration-slow;
        box-sizing: border-box;

        &:hover {
          border-color: @primary-color;
          box-shadow: @box-shadow-base;
        }

        .datasource-radio {
          width: 100%;
          box-sizing: border-box;

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
        .header-actions {
          /* 搜索框后缀（放大镜）图标水平垂直居中 */
          :deep(.ant-input-affix-wrapper .ant-input-suffix) {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            width: 24px;
            min-width: 24px;
            padding: 0;
          }
          :deep(.ant-input-affix-wrapper .ant-input-suffix .anticon) {
            line-height: 1;
          }
          :deep(.ant-input-affix-wrapper .ant-input-suffix .anticon svg) {
            display: block;
          }
        }
      }

      .panel-content {
        flex: 1;
        overflow: visible; // 由 a-table 内部表体滚动控制
        max-height: var(--panel-table-max);
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
