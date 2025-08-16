<template>
  <div v-show="tableShow" class="space-y-6">
    <PageBreadcrumb />
    <!-- Data Card with CardToolbar -->
    <div class="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden">
      <!-- Card Toolbar: 标题/说明 + 操作 + 搜索 -->
      <div class="card-header">
        <div class="flex flex-col gap-3 md:flex-row md:items-center md:justify-between w-full">
          <div class="min-w-0">
            <div class="min-w-0">
              <h1 class="card-title truncate">
                <FileTextOutlined />
                模板组管理
              </h1>
            </div>
          </div>
          <div class="card-actions md:ml-6">
            <div class="flex-shrink-0 w-64 md:w-80">
              <a-input
                v-model:value="queryParam.name"
                placeholder="搜索模板组名称..."
                allow-clear
                @change="tableState.reloadTable(true)"
                @clear="tableState.reloadTable(true)"
                @press-enter="tableState.reloadTable(true)"
              >
                <template #prefix>
                  <svg class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <circle cx="11" cy="11" r="8"></circle>
                    <path d="m21 21-4.35-4.35"></path>
                  </svg>
                </template>
              </a-input>
            </div>
            <button 
              @click="handleAdd"
              class="btn-primary inline-flex items-center"
              title="添加模板组"
            >
              <PlusOutlined class="mr-2" />
              添加模板组
            </button>
          </div>
        </div>
      </div>
      
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">模板组信息</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">创建时间</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">管理操作</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr 
              v-for="record in dataSource" 
              :key="record.id"
              class="hover:bg-gray-50"
            >
              <!-- 模板组信息 -->
              <td class="px-6 py-4">
                <div class="flex items-start space-x-4">
                  <!-- 图标 -->
                  <div class="flex-shrink-0">
                    <div class="w-12 h-12 bg-gradient-to-r from-blue-500 to-purple-600 rounded-lg flex items-center justify-center shadow-sm">
                      <a-avatar 
                        v-if="record.icon" 
                        shape="square" 
                        :src="record.icon" 
                        :size="48"
                        class="rounded-lg"
                      />
                      <svg v-else class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10"></path>
                      </svg>
                    </div>
                  </div>
                  
                  <!-- 详细信息 -->
                  <div class="flex-1 min-w-0">
                    <div class="flex items-center space-x-2 mb-1">
                      <h3 class="text-sm font-semibold text-gray-900 truncate">{{ record.name }}</h3>
                      <span class="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-blue-100 text-blue-800">
                        ID: {{ record.id }}
                      </span>
                    </div>
                    
                    <div class="flex items-center space-x-2 mb-2">
                      <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-indigo-100 text-indigo-800">
                        <svg class="w-3 h-3 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A1.994 1.994 0 013 12V7a4 4 0 014-4z"></path>
                        </svg>
                        {{ record.groupKey }}
                      </span>
                    </div>
                    
                    <p v-if="record.remarks" class="text-sm text-gray-600 line-clamp-2">
                      {{ record.remarks }}
                    </p>
                    <p v-else class="text-sm text-gray-400 italic">暂无描述</p>
                  </div>
                </div>
              </td>
              
              <!-- 创建时间 -->
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="flex items-center space-x-2">
                  <svg class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                  </svg>
                  <div>
                    <div class="text-sm font-medium text-gray-900">{{ formatDate(record.createTime || '') }}</div>
                    <div class="text-xs text-gray-500">{{ formatTime(record.createTime || '') }}</div>
                  </div>
                </div>
              </td>
              
              <!-- 管理操作 -->
              <td class="px-6 py-4">
                <div class="flex flex-col space-y-3">
                  <!-- 基础操作行 -->
                  <div class="flex items-center space-x-2">
                    <button 
                      @click="handleEdit(record)"
                      class="action-btn primary"
                      title="编辑模板组"
                    >
                      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"></path>
                      </svg>
                      编辑
                    </button>
                    <button 
                      @click="handleCopy(record)"
                      class="action-btn secondary"
                      title="复制模板组"
                    >
                      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 16H6a2 2 0 01-2-2V6a2 2 0 012-2h8a2 2 0 012 2v2m-6 12h8a2 2 0 002-2v-8a2 2 0 00-2-2h-8a2 2 0 00-2 2v8a2 2 0 002 2z"></path>
                      </svg>
                      复制
                    </button>
                    <a-popconfirm title="确认要删除这个模板组吗？此操作不可恢复！" @confirm="handleRemove(record)">
                      <button class="action-btn danger" title="删除模板组">
                        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"></path>
                        </svg>
                        删除
                      </button>
                    </a-popconfirm>
                  </div>
                  
                  <!-- 管理操作行 -->
                  <div class="flex items-center space-x-2">
                    <button 
                      @click="handleEntry(record)"
                      class="action-btn file"
                      title="管理模板文件"
                    >
                      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path>
                      </svg>
                      文件管理
                    </button>
                    <button 
                      @click="handlePropertyManagement(record)"
                      class="action-btn config"
                      title="管理模板属性"
                    >
                      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z"></path>
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"></path>
                      </svg>
                      属性管理
                    </button>
                  </div>
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

  <!-- 模板组编辑页面 -->
  <template-entry-edit-page
    v-show="!tableShow"
    ref="templateEntryEditPageRef"
    @handle-property="handleProperty(editedTemplateGroup!)"
    @go-back="tableShow = true"
  />

  <!-- 模板组表单弹窗 -->
  <template-group-form-modal
    ref="templateGroupFormModalRef"
    @done="tableState.reloadTable(false)"
  />

  <!-- 模板组表单弹窗 -->
  <template-property-modal ref="templatePropertyModalRef" />
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { FileTextOutlined, PlusOutlined } from '@ant-design/icons-vue'
import useTable from '@/hooks/table'
import { queryTemplateGroupPage, removeTemplateGroup } from '@/api/gen/template-group'
import {
  exportTemplateGroupProperties,
  importTemplateGroupProperties
} from '@/api/gen/template-property'
import TemplateEntryEditPage from '@/views/gen/template-group/TemplateEntryEditPage.vue'
import TemplatePropertyModal from '@/views/gen/template-property/TemplatePropertyModal.vue'
import TemplateGroupFormModal from '@/views/gen/template-group/TemplateGroupFormModal.vue'
import { doRequest } from '@/utils/axios/request'
import type { TemplateGroup } from '@/api/gen/template-group/types'
import type { TemplateGroupFormModalInstance, TemplatePropertyModalInstance } from './types'
import type { TemplateGroupPageParam } from '@/api/gen/template-group/types'
import { remoteFileDownload } from '@/utils/file-util'
import type { UploadRequestOption } from 'ant-design-vue/es/vc-upload/interface'
import PageBreadcrumb from '@/components/breadcrumb/PageBreadcrumb.vue'

const templateGroupFormModalRef = ref<TemplateGroupFormModalInstance>()
const templatePropertyModalRef = ref<TemplatePropertyModalInstance>()
const templateEntryEditPageRef = ref()

const tableShow = ref<boolean>(true)
const editedTemplateGroup = ref<TemplateGroup>()

// 查询参数
const queryParam = reactive<TemplateGroupPageParam>({})
// 数据表格
const tableState = useTable<TemplateGroup>({
  pageRequest: queryTemplateGroupPage,
  queryParam: queryParam
})
const { dataSource, pagination, loading } = tableState
// 立刻加载数据
tableState.loadData()

function handleAdd() {
  templateGroupFormModalRef.value?.add()
}
function handleEdit(record: TemplateGroup) {
  templateGroupFormModalRef.value?.update(record)
}
function handleCopy(record: TemplateGroup) {
  templateGroupFormModalRef.value?.copy(record)
}
function handleRemove(record: TemplateGroup) {
  doRequest({
    request: removeTemplateGroup(record.groupKey as string),
    successMessage: '删除成功！',
    onSuccess() {
      tableState.reloadTable(false)
    }
  })
}

function handleEntry(record: TemplateGroup) {
  tableShow.value = false
  editedTemplateGroup.value = record
  templateEntryEditPageRef.value.edit(record)
}

/** 模板组属性编辑新建 **/
function handleProperty(record: TemplateGroup) {
  templatePropertyModalRef.value?.open(record)
}

/** 属性管理（增强版弹窗，支持导入导出） **/
function handlePropertyManagement(record: TemplateGroup) {
  templatePropertyModalRef.value?.open(record)
}

/** 模板组属性导入 **/
function handlePropertyImport(fileInfo: UploadRequestOption, record: TemplateGroup) {
  doRequest({
    request: importTemplateGroupProperties(record.groupKey as string, fileInfo.file as File),
    successMessage: '导入模板组属性成功！',
    onSuccess() {
      tableState.reloadTable(false)
    }
  })
}
/** 模板组属性导出 **/
function handlePropertyExport(record: TemplateGroup) {
  exportTemplateGroupProperties(record.groupKey as string).then(response => {
    remoteFileDownload(response)
  })
}

/** 分页变化处理 **/
function handlePaginationChange(current: number, pageSize: number) {
  pagination.value.current = current
  pagination.value.pageSize = pageSize
  tableState.loadData()
}

/** 格式化日期 **/
function formatDate(dateTime: string) {
  if (!dateTime) return '-'
  const date = new Date(dateTime)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}

/** 格式化时间 **/
function formatTime(dateTime: string) {
  if (!dateTime) return ''
  const date = new Date(dateTime)
  return date.toLocaleTimeString('zh-CN', {
    hour: '2-digit',
    minute: '2-digit'
  })
}
</script>

<style scoped lang="less">
/* 现代化表格样式 */
.hover\:bg-gray-50:hover {
  background-color: @slate-50;
}

/* 操作按钮样式：使用全局 .action-btn，保留文件内的小型响应式覆盖 */
.action-btn svg { margin-right: 4px; width: 16px; height: 16px; }

/* 基线对齐修正：紧凑的行高 + svg block 消除下沉 */
.action-btn { line-height: 1; display: inline-flex; align-items: center; }
.action-btn svg { display: block; }

/* 对齐输入框前缀搜索图标 */
:deep(.ant-input-affix-wrapper) { display: flex; align-items: center; }
:deep(.ant-input-prefix) { display: inline-flex; align-items: center; }
:deep(.ant-input-prefix svg) { display: block; }

/* 图标容器样式 */
.w-10.h-10 {
  width: 2.5rem;
  height: 2.5rem;
}

.w-12.h-12 {
  width: 3rem;
  height: 3rem;
}

/* 按钮组间距 */
.space-x-2 > * + * {
  margin-left: 0.5rem;
}

.space-y-2 > * + * {
  margin-top: 0.5rem;
}

.space-y-3 > * + * {
  margin-top: 0.75rem;
}

.space-x-4 > * + * {
  margin-left: 1rem;
}

/* 文本截断 */
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* Align card header padding and corner radius to datasource list header */
.card-header {
  padding: 24px 24px;
  border-top-left-radius: 0.5rem;
  border-top-right-radius: 0.5rem;
}

/* 表格行高度 */
tbody tr {
  min-height: auto;
}

tbody tr td {
  vertical-align: top;
  padding-top: 1rem;
  padding-bottom: 1rem;
}

/* 渐变背景 */
.bg-gradient-to-r {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

/* 上传组件样式重置 */
:deep(.ant-upload) {
  display: inline-block;
  width: 100%;
}

/* 响应式表格 */
@media (max-width: 768px) {
  .overflow-x-auto {
    -webkit-overflow-scrolling: touch;
  }
  
  .px-6 {
    padding-left: 1rem;
    padding-right: 1rem;
  }
  
  .space-x-4 > * + * {
    margin-left: 0.5rem;
  }
  
  tbody tr td {
    padding-top: 0.75rem;
    padding-bottom: 0.75rem;
  }
  
  .action-btn { padding: 4px 8px; font-size: 11px; }
  
  .action-btn span {
    display: none;
  }
  
  .flex.flex-col.space-y-3 {
    flex-direction: row;
  }
  
  .flex.flex-col.space-y-3 > * + * {
    margin-top: 0;
    margin-left: 0.5rem;
  }
}</style>
