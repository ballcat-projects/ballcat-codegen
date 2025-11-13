<template>
  <div class="h-[calc(100vh-200px)] flex flex-col overflow-hidden">
    <PageBreadcrumb />

    <!-- Main Layout -->
    <div class="grid grid-cols-3 gap-6 flex-1 min-h-0">
      <!-- Data Source List -->
      <div class="col-span-2 min-h-0">
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 h-full flex flex-col min-h-0">
          <div class="card-header flex-shrink-0">
            <div class="flex items-center justify-between gap-3 w-full">
              <div class="flex items-center space-x-4 min-w-0">
                <h3 class="card-title">
                  <ClusterOutlined /> 数据源列表 ({{ pagination.total || 0 }} 项)
                </h3>
              </div>

              <!-- Search + Add -->
              <div class="card-actions">
                <div class="w-80">
                  <a-input v-model:value="queryParam.title" placeholder="搜索数据源..." allow-clear
                    @change="tableState.loadData()" @clear="tableState.loadData()" @press-enter="tableState.loadData()"
                    class="w-full" size="middle">
                    <template #prefix>
                      <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none"
                        stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
                        class="lucide lucide-search text-gray-400">
                        <circle cx="11" cy="11" r="8"></circle>
                        <path d="m21 21-4.35-4.35"></path>
                      </svg>
                    </template>
                  </a-input>
                </div>
                <button class="btn-primary inline-flex items-center" @click="handleAdd">
                  <PlusOutlined class="mr-2" />
                  添加数据源
                </button>
              </div>
            </div>
          </div>

          <!-- Scrollable Content Area -->
          <div class="flex-1 overflow-y-auto min-h-0">
            <!-- Empty State -->
            <div v-if="!dataSource || dataSource.length === 0" class="p-8 text-center text-gray-500">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none"
                stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
                class="lucide lucide-database w-12 h-12 mx-auto mb-2 text-gray-400">
                <ellipse cx="12" cy="5" rx="9" ry="3"></ellipse>
                <path d="M3 5V19A9 3 0 0 0 21 19V5"></path>
                <path d="M3 12A9 3 0 0 0 21 12"></path>
              </svg>
              <p class="text-lg font-medium text-gray-900 mb-1">暂无数据源</p>
              <p class="text-gray-500">点击右上角"添加数据源"开始配置</p>
            </div>

            <!-- Data Source Cards -->
            <div v-else class="divide-y divide-gray-200">
              <div v-for="record in dataSource" :key="record.id" :id="`datasource-${record.id}`"
                class="p-6 hover:bg-gray-50 cursor-pointer transition-colors" @click="selectDataSource(record)" :class="{
                  'bg-blue-50 border-l-4 border-blue-600': selectedDataSource?.id === record.id,
                  'hover:bg-blue-25': selectedDataSource?.id !== record.id
                }">
                <div class="flex items-start justify-between">
                  <div class="flex items-start space-x-3">
                    <!-- Database Icon -->
                    <div class="w-10 h-10 bg-blue-100 rounded-lg flex items-center justify-center">
                      <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none"
                        stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
                        class="lucide lucide-database w-5 h-5 text-blue-600">
                        <ellipse cx="12" cy="5" rx="9" ry="3"></ellipse>
                        <path d="M3 5V19A9 3 0 0 0 21 19V5"></path>
                        <path d="M3 12A9 3 0 0 0 21 12"></path>
                      </svg>
                    </div>

                    <!-- Database Info -->
                    <div>
                      <h4 class="font-semibold text-gray-900">{{ record.title }}</h4>
                      <div class="flex items-center space-x-2 mt-1">
                        <span class="text-sm text-gray-600">{{ getJdbcSummary(record.url) }}</span>
                        <LockOutlined v-if="hasSsl(record.url)" class="text-gray-400 text-xs align-middle" />
                      </div>
                      <p class="text-sm text-gray-500 mt-1">{{ record.username }}@{{ record.dsKey }}</p>
                    </div>
                  </div>

                  <!-- Actions -->
                  <div class="flex items-center space-x-2">
                    <div class="flex space-x-1">
                      <button @click.stop="handleEdit(record)"
                        class="p-2 text-gray-400 hover:text-blue-600 hover:bg-blue-50 rounded" title="编辑">
                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none"
                          stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
                          class="lucide lucide-square-pen w-4 h-4">
                          <path d="M12 3H5a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                          <path
                            d="M18.375 2.625a1 1 0 0 1 3 3l-9.013 9.014a2 2 0 0 1-.853.505l-2.873.84a .5.5 0 0 1-.62-.62l.84-2.873a2 2 0 0 1 .506-.852z">
                          </path>
                        </svg>
                      </button>
                      <a-popconfirm title="确认要删除这个数据源吗？" @confirm="() => handleDel(record)">
                        <button @click.stop class="p-2 text-gray-400 hover:text-red-600 hover:bg-red-50 rounded"
                          title="删除">
                          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none"
                            stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
                            class="lucide lucide-trash2 w-4 h-4">
                            <path d="M10 11v6"></path>
                            <path d="M14 11v6"></path>
                            <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6"></path>
                            <path d="M3 6h18"></path>
                            <path d="M8 6V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                          </svg>
                        </button>
                      </a-popconfirm>
                    </div>
                  </div>
                </div>

                <!-- Create Time -->
                <div class="mt-3 text-xs text-gray-500">
                  创建时间: {{ record.createTime || '未知' }}
                </div>
              </div>
            </div>
          </div>

          <!-- Pagination -->
          <div class="px-6 py-4 border-t border-gray-200 flex-shrink-0" v-if="dataSource && dataSource.length > 0">
            <a-pagination v-model:current="pagination.current" v-model:pageSize="pagination.pageSize"
              :total="pagination.total" :show-size-changer="true" :show-quick-jumper="true"
              :show-total="(total: number, range: [number, number]) => `第 ${range[0]}-${range[1]} 条，共 ${total} 条`"
              @change="handlePaginationChange" @showSizeChange="handlePaginationChange" />
          </div>
        </div>


      </div>
      <!-- Detail Panel -->
      <div class="col-span-1 min-h-0">
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 h-full flex flex-col min-h-0">
          <div class="card-header flex-shrink-0">
            <div class="flex items-center justify-between w-full">
              <h3 class="card-title">{{ headerTitle }}</h3>
              <button v-if="selectedDataSource && panelMode === 'view'" @click="clearSelection"
                class="text-gray-400 hover:text-gray-600 p-1 rounded-lg hover:bg-gray-100 transition-colors"
                title="清除选择">
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none"
                  stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
                  class="lucide lucide-x">
                  <path d="M18 6 6 18"></path>
                  <path d="m6 6 12 12"></path>
                </svg>
              </button>
            </div>
          </div>

          <div class="flex-1 overflow-y-auto min-h-0">
            <!-- No Selection State -->
            <div v-if="panelMode === 'empty'" class="p-8 text-center text-gray-500">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none"
                stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
                class="lucide lucide-database w-12 h-12 mx-auto mb-2 text-gray-400">
                <ellipse cx="12" cy="5" rx="9" ry="3"></ellipse>
                <path d="M3 5V19A9 3 0 0 0 21 19V5"></path>
                <path d="M3 12A9 3 0 0 0 21 12"></path>
              </svg>
              <p class="font-medium text-gray-900 mb-1">选择数据源查看详情</p>
              <p class="text-sm text-gray-500">点击左侧列表中的数据源</p>
              <div class="mt-4 p-3 bg-blue-50 rounded-lg text-left">
                <div class="flex items-start space-x-2">
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none"
                    stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
                    class="lucide lucide-lightbulb text-blue-600 mt-0.5">
                    <path
                      d="M15 14c.2-1 .7-1.7 1.5-2.5 1-.9 1.5-2.2 1.5-3.5A6 6 0 0 0 6 8c0 1 .2 2.2 1.5 3.5.7.7 1.3 1.5 1.5 2.5">
                    </path>
                    <path d="M9 18h6"></path>
                    <path d="M10 22h4"></path>
                  </svg>
                  <div class="text-sm text-blue-800">
                    <p class="font-medium mb-1">💡 使用提示</p>
                    <ul class="space-y-1 text-xs">
                      <li>• 详情面板固定显示，不会因滚动消失</li>
                      <li>• 支持快速编辑和删除操作</li>
                      <li>• 使用搜索功能快速定位数据源</li>
                    </ul>
                  </div>
                </div>
              </div>
            </div>

            <!-- Data Source Form / Details -->
            <DataSourceForm
              v-if="panelMode === 'form'"
              ref="formRef"
              @done="handleFormDone"
              @cancel="handleFormCancel"
            />

            <DataSourceDetails
              v-if="panelMode === 'view' && selectedDataSource"
              :record="selectedDataSource"
              @edit="handleEdit(selectedDataSource)"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, computed } from 'vue'
import useTable from '@/hooks/table'
import { doRequest } from '@/utils/axios/request'
import { queryDatasourceConfigPage, removeDatasourceConfig } from '@/api/gen/datasource-config'
import PageBreadcrumb from '@/components/breadcrumb/PageBreadcrumb.vue'
import { ClusterOutlined, PlusOutlined, LockOutlined } from '@ant-design/icons-vue'
import { getJdbcSummary, hasSsl } from '@/utils/jdbc-url'
import DataSourceForm from './components/DataSourceForm.vue'
import DataSourceDetails from './components/DataSourceDetails.vue'

import type { DataSourceConfig, DataSourcePageParam } from '@/api/gen/datasource-config/types'
import type { DataSourceFormInstance } from './types'

// 选中的数据源
const selectedDataSource = ref<DataSourceConfig | null>(null)

// 右侧面板模式：empty | view | form
const panelMode = ref<'empty' | 'view' | 'form'>('empty')

const headerTitle = computed(() => {
  if (panelMode.value === 'form') return '数据源表单'
  if (panelMode.value === 'view') return '数据源详情'
  return '数据源详情'
})

// 表单组件引用
const formRef = ref<DataSourceFormInstance>()

// 查询参数
const queryParam = reactive<DataSourcePageParam>({})

// 表格设置
const tableState = useTable<DataSourceConfig>({
  pageRequest: queryDatasourceConfigPage,
  queryParam: queryParam
})

const { dataSource, pagination } = tableState

// 立刻加载数据
tableState.loadData()

// 选择数据源
function selectDataSource(record: DataSourceConfig) {
  selectedDataSource.value = record
  panelMode.value = 'view'
}

// 清除选择
function clearSelection() {
  selectedDataSource.value = null
  panelMode.value = 'empty'
}

// 表单完成回调
function handleFormDone() {
  tableState.loadData()
  selectedDataSource.value = null
  panelMode.value = 'empty'
}

// 表单取消回调
function handleFormCancel() {
  panelMode.value = selectedDataSource.value ? 'view' : 'empty'
}



// 分页变化
function handlePaginationChange(page: number, pageSize?: number) {
  pagination.value.current = page
  if (pageSize) {
    pagination.value.pageSize = pageSize
  }
  tableState.loadData()
}

// 添加数据源
function handleAdd() {
  formRef.value?.add()
  selectedDataSource.value = null
  panelMode.value = 'form'
}

// 编辑数据源
function handleEdit(record: DataSourceConfig) {
  formRef.value?.edit(record)
  selectedDataSource.value = record
  panelMode.value = 'form'
}

// 删除数据源
function handleDel(record: DataSourceConfig) {
  doRequest<void>({
    request: removeDatasourceConfig(record.id),
    successMessage: '删除成功！',
    onSuccess() {
      // 如果删除的是当前选中的数据源，清空选择
      if (selectedDataSource.value?.id === record.id) {
        selectedDataSource.value = null
      }
      tableState.loadData()
    }
  })
}

// 已移除旧的 formatUrl，改用通用 JDBC 工具
</script>

<style scoped lang="less">
.btn-primary :deep(.anticon) { display: block; }
</style>
