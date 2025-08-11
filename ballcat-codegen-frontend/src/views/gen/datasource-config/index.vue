<template>
  <div class="h-[calc(100vh-200px)] flex flex-col overflow-hidden">
    <!-- Page Header -->
    <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6 flex-shrink-0 mb-6">
      <div class="flex items-center justify-between">
        <div>
          <h1 class="text-2xl font-bold text-gray-900 flex items-center">
            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none"
              stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
              class="lucide lucide-database w-6 h-6 mr-2 text-blue-600">
              <ellipse cx="12" cy="5" rx="9" ry="3"></ellipse>
              <path d="M3 5V19A9 3 0 0 0 21 19V5"></path>
              <path d="M3 12A9 3 0 0 0 21 12"></path>
            </svg>
            数据源管理
          </h1>
          <p class="text-gray-600 mt-1">配置和管理数据库连接</p>
        </div>
        <button @click="handleAdd"
          class="flex items-center px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none"
            stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
            class="lucide lucide-plus w-4 h-4 mr-2">
            <path d="M5 12h14"></path>
            <path d="M12 5v14"></path>
          </svg>
          添加数据源
        </button>
      </div>
    </div>

    <!-- Main Layout -->
    <div class="grid grid-cols-3 gap-6 flex-1 min-h-0">
      <!-- Data Source List -->
      <div class="col-span-2 min-h-0">
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 h-full flex flex-col min-h-0">
          <div class="p-6 border-b border-gray-200 flex-shrink-0">
            <!-- Header Row -->
            <div class="flex items-center justify-between">
              <div class="flex items-center space-x-4">
                <h3 class="text-lg font-semibold text-gray-900">
                  数据源列表 ({{ pagination.total || 0 }} 项)
                </h3>
                <div class="text-sm text-gray-500" v-if="selectedDataSource">
                  <span
                    class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                    已选择: {{ selectedDataSource.title }}
                  </span>
                </div>
              </div>

              <!-- Search Input -->
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
                        <span class="text-sm text-gray-600">{{ formatUrl(record.url) }}</span>
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
          <div class="px-6 py-6 border-b border-gray-200 flex-shrink-0">
            <div class="flex items-center justify-between">
              <h3 class="text-lg font-semibold text-gray-900">
                <span v-if="isAdding">添加数据源</span>
                <span v-else-if="isEditing">编辑数据源</span>
                <span v-else>数据源详情</span>
              </h3>
              <button v-if="selectedDataSource && !isAdding && !isEditing" @click="clearSelection"
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
            <!-- Add New Data Source Form -->
            <div v-if="isAdding" class="p-6">
              <div class="space-y-4">
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">
                    数据源名称 <span class="text-red-500">*</span>
                  </label>
                  <a-input v-model:value="editForm.title" placeholder="请输入数据源名称" />
                </div>

                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">
                    数据源键 <span class="text-red-500">*</span>
                  </label>
                  <a-input v-model:value="editForm.dsKey" placeholder="请输入数据源键" />
                </div>

                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">
                    连接地址 <span class="text-red-500">*</span>
                  </label>
                  <a-input v-model:value="editForm.url" placeholder="jdbc:mysql://localhost:3306/database" />
                </div>

                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">
                    用户名 <span class="text-red-500">*</span>
                  </label>
                  <a-input v-model:value="editForm.username" placeholder="请输入用户名" />
                </div>

                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">
                    密码 <span class="text-red-500">*</span>
                  </label>
                  <a-input-password v-model:value="editForm.password" placeholder="请输入密码" />
                </div>

                <div class="pt-4 border-t border-gray-200 space-y-2">
                  <button @click="handleSave"
                    class="w-full flex items-center justify-center px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
                    :disabled="!isFormValid">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none"
                      stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
                      class="w-4 h-4 mr-2">
                      <path d="M19 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11l5 5v11a2 2 0 0 1-2 2z"></path>
                      <polyline points="17,21 17,13 7,13 7,21"></polyline>
                      <polyline points="7,3 7,8 15,8"></polyline>
                    </svg>
                    保存
                  </button>
                  <button @click="cancelEdit"
                    class="w-full flex items-center justify-center px-4 py-2 text-gray-600 border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none"
                      stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
                      class="w-4 h-4 mr-2">
                      <path d="M18 6 6 18"></path>
                      <path d="m6 6 12 12"></path>
                    </svg>
                    取消
                  </button>
                </div>
              </div>
            </div>

            <!-- Edit Data Source Form -->
            <div v-else-if="isEditing && selectedDataSource" class="p-6">
              <div class="space-y-4">
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">
                    数据源名称 <span class="text-red-500">*</span>
                  </label>
                  <a-input v-model:value="editForm.title" placeholder="请输入数据源名称" />
                </div>

                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">
                    数据源键 <span class="text-red-500">*</span>
                  </label>
                  <a-input v-model:value="editForm.dsKey" placeholder="请输入数据源键" />
                </div>

                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">
                    连接地址 <span class="text-red-500">*</span>
                  </label>
                  <a-input v-model:value="editForm.url" placeholder="jdbc:mysql://localhost:3306/database" />
                </div>

                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">
                    用户名 <span class="text-red-500">*</span>
                  </label>
                  <a-input v-model:value="editForm.username" placeholder="请输入用户名" />
                </div>

                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">密码</label>
                  <a-input-password v-model:value="editForm.password" placeholder="不修改请留空" />
                </div>

                <div class="pt-4 border-t border-gray-200 space-y-2">
                  <button @click="handleSave"
                    class="w-full flex items-center justify-center px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
                    :disabled="!isFormValid">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none"
                      stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
                      class="w-4 h-4 mr-2">
                      <path d="M19 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11l5 5v11a2 2 0 0 1-2 2z"></path>
                      <polyline points="17,21 17,13 7,13 7,21"></polyline>
                      <polyline points="7,3 7,8 15,8"></polyline>
                    </svg>
                    保存修改
                  </button>
                  <button @click="cancelEdit"
                    class="w-full flex items-center justify-center px-4 py-2 text-gray-600 border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none"
                      stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
                      class="w-4 h-4 mr-2">
                      <path d="M18 6 6 18"></path>
                      <path d="m6 6 12 12"></path>
                    </svg>
                    取消
                  </button>
                </div>
              </div>
            </div>

            <!-- No Selection State -->
            <div v-else-if="!selectedDataSource" class="p-8 text-center text-gray-500">
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

            <!-- Data Source Details -->
            <div v-else-if="selectedDataSource && !isEditing" class="p-6 space-y-4">
              <!-- Title -->
              <div>
                <h4 class="text-lg font-semibold text-gray-900">{{ selectedDataSource.title }}</h4>
              </div>

              <!-- Connection Info -->
              <div class="space-y-3">
                <div v-if="selectedDataSource.url">
                  <label class="text-sm font-medium text-gray-700">连接地址</label>
                  <p class="text-sm text-gray-900 mt-1 break-all">{{ selectedDataSource.url }}</p>
                </div>

                <div v-if="selectedDataSource.username">
                  <label class="text-sm font-medium text-gray-700">用户名</label>
                  <p class="text-sm text-gray-900 mt-1">{{ selectedDataSource.username }}</p>
                </div>

                <div v-if="selectedDataSource.dsKey">
                  <label class="text-sm font-medium text-gray-700">数据源键</label>
                  <p class="text-sm text-gray-900 mt-1">{{ selectedDataSource.dsKey }}</p>
                </div>

                <div v-if="selectedDataSource.createTime">
                  <label class="text-sm font-medium text-gray-700">创建时间</label>
                  <p class="text-sm text-gray-900 mt-1">{{ selectedDataSource.createTime }}</p>
                </div>
              </div>

              <!-- Actions -->
              <div class="pt-4 border-t border-gray-200 space-y-2">
                <button @click="handleEdit(selectedDataSource)"
                  class="w-full flex items-center justify-center px-4 py-2 text-blue-600 border border-blue-200 rounded-lg hover:bg-blue-50 transition-colors">
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none"
                    stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
                    class="lucide lucide-square-pen w-4 h-4 mr-2">
                    <path d="M12 3H5a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                    <path
                      d="M18.375 2.625a1 1 0 0 1 3 3l-9.013 9.014a2 2 0 0 1-.853.505l-2.873.84a .5.5 0 0 1-.62-.62l.84-2.873a2 2 0 0 1 .506-.852z">
                    </path>
                  </svg>
                  编辑配置
                </button>
              </div>
            </div>
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
import { queryDatasourceConfigPage, removeDatasourceConfig, addDatasourceConfig, updateDatasourceConfig } from '@/api/gen/datasource-config'

import type { DataSourceConfig, DataSourcePageParam } from '@/api/gen/datasource-config/types'

// 选中的数据源
const selectedDataSource = ref<DataSourceConfig | null>(null)

// 编辑状态
const isAdding = ref(false)
const isEditing = ref(false)

// 编辑表单
const editForm = reactive({
  id: undefined as number | undefined,
  title: '',
  dsKey: '',
  url: '',
  username: '',
  password: ''
})

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

// 表单验证
const isFormValid = computed(() => {
  return editForm.title && editForm.dsKey && editForm.url && editForm.username &&
    (isAdding.value ? editForm.password : true)
})

// 重置表单
function resetForm() {
  editForm.id = undefined
  editForm.title = ''
  editForm.dsKey = ''
  editForm.url = ''
  editForm.username = ''
  editForm.password = ''
}

// 选择数据源
function selectDataSource(record: DataSourceConfig) {
  if (isAdding.value || isEditing.value) {
    return // 编辑状态下不允许切换
  }
  selectedDataSource.value = record
}

// 清除选择
function clearSelection() {
  if (isAdding.value || isEditing.value) {
    return // 编辑状态下不允许清除
  }
  selectedDataSource.value = null
}

// 开始添加
function startAdd() {
  resetForm()
  isAdding.value = true
  isEditing.value = false
  selectedDataSource.value = null
}

// 开始编辑
function startEdit(record: DataSourceConfig) {
  resetForm()
  editForm.id = record.id
  editForm.title = record.title || ''
  editForm.dsKey = record.dsKey || ''
  editForm.url = record.url || ''
  editForm.username = record.username || ''
  editForm.password = '' // 密码不显示，留空表示不修改

  isAdding.value = false
  isEditing.value = true
  selectedDataSource.value = record
}

// 取消编辑
function cancelEdit() {
  isAdding.value = false
  isEditing.value = false
  resetForm()
}

// 保存数据
function handleSave() {
  if (!isFormValid.value) return

  if (isAdding.value) {
    // 添加新数据源
    doRequest<void>({
      request: addDatasourceConfig({
        title: editForm.title,
        dsKey: editForm.dsKey,
        url: editForm.url,
        username: editForm.username,
        password: editForm.password
      }),
      successMessage: '添加成功！',
      onSuccess() {
        cancelEdit()
        tableState.loadData()
      }
    })
  } else if (isEditing.value && editForm.id) {
    // 更新数据源
    const updateData: any = {
      id: editForm.id,
      title: editForm.title,
      dsKey: editForm.dsKey,
      url: editForm.url,
      username: editForm.username
    }

    // 只有输入了密码才更新密码
    if (editForm.password) {
      updateData.password = editForm.password
    }

    doRequest<void>({
      request: updateDatasourceConfig(updateData),
      successMessage: '更新成功！',
      onSuccess() {
        cancelEdit()
        tableState.loadData()
      }
    })
  }
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
  startAdd()
}

// 编辑数据源
function handleEdit(record: DataSourceConfig) {
  startEdit(record)
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

// 格式化URL显示
function formatUrl(url?: string) {
  if (!url) return ''
  try {
    const urlObj = new URL(url)
    return `${urlObj.hostname}:${urlObj.port || '3306'}`
  } catch {
    return url
  }
}
</script>
