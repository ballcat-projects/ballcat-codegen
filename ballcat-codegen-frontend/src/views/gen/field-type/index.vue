<template>
  <div class="space-y-6">
    <!-- Page Header -->
    <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
      <div class="flex items-center justify-between">
        <div>
          <h1 class="text-2xl font-bold text-gray-900 flex items-center">
            <svg class="w-6 h-6 mr-2 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <ellipse cx="12" cy="5" rx="9" ry="3"></ellipse>
              <path d="M3 5V19A9 3 0 0 0 21 19V5"></path>
              <path d="M3 12A9 3 0 0 0 21 12"></path>
            </svg>
            数据类型映射管理
          </h1>
          <p class="text-gray-600 mt-1">管理数据库字段类型到Java属性类型的映射关系</p>
        </div>
        <div class="flex items-center space-x-3">
          <button 
            @click="handleAdd"
            class="flex items-center px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
          >
            <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 12h14"></path>
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 5v14"></path>
            </svg>
            添加映射
          </button>
        </div>
      </div>
    </div>

    <!-- Search and Filters -->
    <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
      <div class="flex flex-col sm:flex-row gap-4">
        <div class="flex-1">
          <label class="block text-sm font-medium text-gray-700 mb-2">数据库类型</label>
          <a-select
            v-model:value="queryParam.dbType"
            class="w-full"
            placeholder="选择数据库类型"
            allow-clear
            @change="tableState.reloadTable(true)"
            @clear="tableState.reloadTable(true)"
          >
            <a-select-option v-for="(value, key) in DbType" :key="key" :value="value">
              {{ key }}
            </a-select-option>
          </a-select>
        </div>
        <div class="flex-1">
          <label class="block text-sm font-medium text-gray-700 mb-2">搜索</label>
          <a-input
            v-model:value="queryParam.groupKey"
            placeholder="搜索模板组标识..."
            allow-clear
            @change="tableState.reloadTable(true)"
            @clear="tableState.reloadTable(true)"
            @press-enter="tableState.reloadTable(true)"
          />
        </div>
      </div>
    </div>

    <!-- Data Table -->
    <div class="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden">
      <div class="px-6 py-4 border-b border-gray-200">
        <h2 class="text-lg font-semibold text-gray-900">
          类型映射 ({{ pagination.total || 0 }} 项)
        </h2>
      </div>
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">模板组标识</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">数据库类型</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Java类型</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">数据库类型</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">包路径</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">创建时间</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">操作</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr 
              v-for="record in dataSource" 
              :key="record.id"
              class="hover:bg-gray-50"
            >
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm font-medium text-gray-900">{{ record.id }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-900">{{ record.groupKey }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm font-medium text-gray-900">{{ record.columnKey }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                  {{ record.columnValue }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-600">
                  {{ Object.entries(DbType).find(([, enumValue]) => enumValue === record.dbType)?.[0] }}
                </div>
              </td>
              <td class="px-6 py-4">
                <div class="text-sm text-gray-600">{{ record.packageName }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-500">{{ record.createTime }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                <div class="flex items-center space-x-2">
                  <button 
                    @click="handleEdit(record)"
                    class="text-blue-600 hover:text-blue-900 p-1 rounded"
                  >
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 3H5a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18.375 2.625a1 1 0 0 1 3 3l-9.013 9.014a2 2 0 0 1-.853.505l-2.873.84a.5.5 0 0 1-.62-.62l.84-2.873a2 2 0 0 1 .506-.852z"></path>
                    </svg>
                  </button>
                  <a-popconfirm title="确认要删除这条记录吗？" @confirm="() => handleDel(record)">
                    <button class="text-red-600 hover:text-red-900 p-1 rounded">
                      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 11v6"></path>
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14 11v6"></path>
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6"></path>
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 6h18"></path>
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 6V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                      </svg>
                    </button>
                  </a-popconfirm>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      
      <!-- Pagination -->
      <div class="px-6 py-4 border-t border-gray-200" v-if="(pagination.total ?? 0) > 0">
        <a-pagination
          v-model:current="pagination.current"
          v-model:pageSize="pagination.pageSize"
          :total="pagination.total"
          :show-size-changer="true"
          :show-quick-jumper="true"
          :show-total="(total: number, range: [number, number]) => `第 ${range[0]}-${range[1]} 条，共 ${total} 条`"
          @change="handlePaginationChange"
          @showSizeChange="handlePaginationChange"
        />
      </div>
    </div>
  </div>

  <!-- 编辑弹窗 -->
  <field-type-edit-modal ref="editModal" @done="tableState.reloadTable(false)" />
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import useTable from '@/hooks/table'
import { doRequest } from '@/utils/axios/request'
import { queryFieldTypePage, removeFieldType } from '@/api/gen/field-type'
import FieldTypeEditModal from '@/views/gen/field-type/FieldTypeEditModal.vue'
import AddButton from '@/components/button/AddButton.vue'

import type { FieldType, FieldTypePageParam } from '@/api/gen/field-type/types'
import type { FieldTypeEditModalInstance } from './types'
import { DbType } from '@/api/gen/field-type/types'

// 编辑弹窗
const editModal = ref<FieldTypeEditModalInstance>()

// 表格列设置
const columns = [
  {
    title: '#',
    dataIndex: 'id'
  },
  {
    title: '模板组标识',
    dataIndex: 'groupKey'
  },
  {
    title: 'DB属性类型',
    dataIndex: 'columnKey'
  },
  {
    title: '对应属性类型',
    dataIndex: 'columnValue'
  },
  {
    title: '数据库类型',
    dataIndex: 'dbType',
    customRender: ({ text }: { text: string }) =>
      Object.entries(DbType).find(([, enumValue]) => enumValue === text)?.[0]
  },
  {
    title: '属性包路径',
    dataIndex: 'packageName'
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    width: '150px',
    sorter: true
  },
  {
    title: '操作',
    dataIndex: 'action',
    width: 160,
    scopedSlots: { customRender: 'action-slot' }
  }
]

// 查询参数
const queryParam = reactive<FieldTypePageParam>({})
// 类型过滤器
const typeFilter = ref<string>()
// 数据表格
const tableState = useTable<FieldType>({
  pageRequest: queryFieldTypePage,
  queryParam: queryParam
})
const { dataSource, pagination, loading } = tableState
// 立刻加载数据
tableState.loadData()

/* 新建数据源配置 */
const handleAdd = () => {
  editModal.value?.add()
}

/* 编辑数据源配置 */
const handleEdit = (record: FieldType) => {
  editModal.value?.update(record)
}

/* 删除数据源配置 */
const handleDel = (record: FieldType) => {
  doRequest<void>({
    request: removeFieldType(record.id),
    successMessage: '删除成功！',
    onSuccess() {
      tableState.loadData()
    }
  })
}

/* 分页变化处理 */
const handlePaginationChange = (current: number, pageSize: number) => {
  pagination.value.current = current
  pagination.value.pageSize = pageSize
  tableState.loadData()
}

/* 类型过滤变化处理 */
const handleTypeFilterChange = (value: string) => {
  // 这里可以根据需要添加过滤逻辑
  // 目前先重新加载数据
  tableState.reloadTable(true)
}
</script>

<style scoped>
/* 现代化表格样式 */
:deep(.modern-table) {
  /* 表头样式 */
  .ant-table-thead > tr > th {
    background: #f8fafc;
    border-bottom: 1px solid #e2e8f0;
    color: #1f2937;
    font-weight: 600;
    font-size: 14px;
  }

  /* 表格行样式 */
  .ant-table-tbody > tr {
    transition: all 0.2s;
  }

  .ant-table-tbody > tr:hover {
    background: #f8fafc;
  }

  /* 表格边框 */
  .ant-table-tbody > tr > td {
    border-bottom: 1px solid #f1f5f9;
  }

  /* 分页样式 */
  .ant-pagination {
    margin-top: 16px;
    margin-bottom: 0;
  }

  .ant-pagination-item {
    border: 1px solid #e2e8f0;
    background: white;
  }

  .ant-pagination-item-active {
    border-color: #3b82f6;
    background: #3b82f6;
  }

  .ant-pagination-item-active a {
    color: white;
  }
}
</style>
