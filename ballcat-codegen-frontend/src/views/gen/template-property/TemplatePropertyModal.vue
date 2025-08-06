<template>
  <a-modal
    v-model:open="visible"
    :title="null"
    :mask-closable="false"
    :footer="null"
    :body-style="{ padding: 0 }"
    :width="1400"
    :closable="true"
    class="template-property-modal"
    @cancel="handleClose"
  >
    <!-- 自定义标题栏 -->
    <div class="modal-header">
      <div class="header-content">
        <div class="header-left">
          <div class="header-icon">
            <svg class="w-6 h-6 text-indigo-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z"></path>
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"></path>
            </svg>
          </div>
          <div class="header-text">
            <h2 class="modal-title">{{ title }}</h2>
            <p class="modal-subtitle">管理模板属性配置，支持新增、编辑、删除以及批量导入导出操作</p>
          </div>
        </div>
        <div class="header-actions">
          <!-- 表格页面按钮组 -->
          <div v-show="showTable" class="action-group">
            <a-button 
              type="default"
              size="middle"
              class="action-btn"
              @click="handleCreate"
              :disabled="!currentTemplateGroup"
            >
              <template #icon>
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"></path>
                </svg>
              </template>
              新建属性
            </a-button>
            <div class="action-divider"></div>
            <a-upload
              accept=".json"
              :show-upload-list="false"
              :custom-request="handlePropertyImport"
              :disabled="!currentTemplateGroup"
            >
              <a-button type="default" size="middle" class="action-btn">
                <template #icon>
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M9 19l3 3m0 0l3-3m-3 3V10"></path>
                  </svg>
                </template>
                导入属性
              </a-button>
            </a-upload>
            <a-button 
              type="default" 
              size="middle"
              class="action-btn"
              @click="handlePropertyExport"
              :disabled="!currentTemplateGroup"
            >
              <template #icon>
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12"></path>
                </svg>
              </template>
              导出属性
            </a-button>
          </div>
          
          <!-- 编辑页面按钮组 -->
          <div v-show="!showTable" class="action-group">
            <a-button 
              type="default"
              size="middle"
              class="action-btn"
              @click="backToPage(false)"
            >
              <template #icon>
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"></path>
                </svg>
              </template>
              返回列表
            </a-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 内容区域 -->
    <div class="modal-body">
      <!-- 面包屑导航 -->
      <div class="breadcrumb-nav" v-show="!showTable">
        <div class="breadcrumb-content">
          <div class="breadcrumb-path">
            <span class="breadcrumb-item">{{ title }}</span>
            <svg class="breadcrumb-separator w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path>
            </svg>
            <span class="breadcrumb-item current">编辑属性</span>
          </div>
        </div>
      </div>

      <!-- 属性列表页面 -->
      <div v-show="showTable" class="content-wrapper">
        <template-property-table-page
          ref="templatePropertyTablePageRef"
          @update="handleUpdate"
        />
      </div>

      <!-- 属性编辑页面 -->
      <div v-show="!showTable" class="content-wrapper">
        <template-property-edit-page
          ref="templatePropertyEditPageRef"
          @back-page="backToPage"
        />
      </div>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
// import type
import { nextTick, ref, watch } from 'vue'
import { usePopup } from '@/hooks/popup'
import TemplatePropertyTablePage from '@/views/gen/template-property/TemplatePropertyTablePage.vue'
import TemplatePropertyEditPage from '@/views/gen/template-property/TemplatePropertyEditPage.vue'
import type { TemplatePropertyModalInstance } from '@/views/gen/template-group/types'
import type { TemplateGroup } from '@/api/gen/template-group/types'
import type { TemplateProperty } from '@/api/gen/template-property/types'
import { exportTemplateGroupProperties, importTemplateGroupProperties } from '@/api/gen/template-property'
import { doRequest } from '@/utils/axios/request'
import { remoteFileDownload } from '@/utils/file-util'
import type { UploadRequestOption } from 'ant-design-vue/es/vc-upload/interface'

const templatePropertyTablePageRef = ref()
const templatePropertyEditPageRef = ref()

let currentTemplateGroup: TemplateGroup | undefined = undefined

const { visible, handleOpen, handleClose } = usePopup()

const title = ref<string>()

const showTable = ref<boolean>(true)

function switchPage() {
  showTable.value = !showTable.value
}

function backToPage(reloadTable = false) {
  reloadTable && templatePropertyTablePageRef.value?.reloadTable()
  switchPage()
}

function handleCreate() {
  switchPage()
  templatePropertyEditPageRef.value.create(currentTemplateGroup?.groupKey)
}

function handleUpdate(record: TemplateProperty) {
  switchPage()
  templatePropertyEditPageRef.value.update(record)
}

/** 属性导入 **/
function handlePropertyImport(fileInfo: UploadRequestOption) {
  if (!currentTemplateGroup) return
  
  doRequest({
    request: importTemplateGroupProperties(currentTemplateGroup.groupKey as string, fileInfo.file as File),
    successMessage: '导入模板组属性成功！',
    onSuccess() {
      templatePropertyTablePageRef.value?.reloadTable()
    }
  })
}

/** 属性导出 **/
function handlePropertyExport() {
  if (!currentTemplateGroup) return
  
  exportTemplateGroupProperties(currentTemplateGroup.groupKey as string).then(response => {
    remoteFileDownload(response)
  })
}

defineExpose<TemplatePropertyModalInstance>({
  open(templateGroup: TemplateGroup) {
    currentTemplateGroup = templateGroup
    showTable.value = true
    title.value = '属性配置 - ' + templateGroup.name
    
    // 打开弹窗
    handleOpen()
    
    // 使用 nextTick 确保弹窗和组件完全渲染后再加载数据
    nextTick(() => {
      if (templatePropertyTablePageRef.value && currentTemplateGroup) {
        templatePropertyTablePageRef.value.load(currentTemplateGroup)
      } else {
        // 如果组件还没准备好，再延迟一些时间
        setTimeout(() => {
          if (templatePropertyTablePageRef.value && currentTemplateGroup) {
            templatePropertyTablePageRef.value.load(currentTemplateGroup)
          }
        }, 300)
      }
    })
  }
})
</script>

<style scoped>
/* 自定义标题栏 */
.modal-header {
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  color: #1e293b;
  padding: 0;
  border-bottom: 1px solid #e2e8f0;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
}

.header-left {
  display: flex;
  align-items: center;
  flex: 1;
}

.header-icon {
  width: 48px;
  height: 48px;
  background: rgba(59, 130, 246, 0.1);
  border: 1px solid rgba(59, 130, 246, 0.2);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
}

.header-icon svg {
  color: #3b82f6;
}

.header-text {
  flex: 1;
}

.modal-title {
  font-size: 20px;
  font-weight: 600;
  margin: 0 0 4px 0;
  color: #1e293b;
}

.modal-subtitle {
  font-size: 14px;
  margin: 0;
  color: #64748b;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-right: 60px; /* 为关闭按钮留出空间 */
}

.action-group {
  display: flex;
  align-items: center;
  gap: 12px;
}

.action-divider {
  width: 1px;
  height: 24px;
  background: #cbd5e1;
}

.action-btn {
  background: white;
  border: 1px solid #e2e8f0;
  color: #475569;
  border-radius: 8px;
  height: 36px;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 0 16px;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
}

.action-btn:hover {
  background: #f8fafc;
  border-color: #cbd5e1;
  color: #334155;
  transform: translateY(-1px);
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
}

.action-btn:focus {
  background: #f8fafc;
  border-color: #3b82f6;
  color: #334155;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.action-btn:disabled {
  background: #f1f5f9;
  border-color: #e2e8f0;
  color: #94a3b8;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.action-btn .anticon {
  font-size: 14px;
}

/* 内容区域 */
.modal-body {
  background: #f8fafc;
  height: 70vh;
  min-height: 600px;
  max-height: 800px;
  display: flex;
  flex-direction: column;
}

/* 面包屑导航 */
.breadcrumb-nav {
  background: white;
  border-bottom: 1px solid #e2e8f0;
  padding: 16px 24px;
  flex-shrink: 0;
}

.breadcrumb-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.breadcrumb-path {
  display: flex;
  align-items: center;
  gap: 8px;
}

.breadcrumb-item {
  font-size: 14px;
  color: #64748b;
}

.breadcrumb-item.current {
  color: #1e293b;
  font-weight: 500;
}

.breadcrumb-separator {
  color: #94a3b8;
}

/* 内容包装器 */
.content-wrapper {
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

/* 重置子组件样式 - 统一配色方案 */
:deep(.template-property-page) {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: transparent;
}

:deep(.template-property-page__container) {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #f8fafc;
  margin: 0;
  border-radius: 0;
  box-shadow: none;
  overflow: hidden;
}

:deep(.template-property-page__table) {
  flex: 1;
  background: #f8fafc;
  overflow: auto;
}

/* 表格样式优化 - 统一配色 */
:deep(.ant-table-wrapper) {
  height: 100%;
  background: white;
  border-radius: 12px;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
  border: 1px solid #e2e8f0;
}

:deep(.ant-table-container) {
  height: 100%;
  border-radius: 12px;
  overflow: hidden;
}

:deep(.ant-table-thead > tr > th) {
  background: #f8fafc !important;
  border-bottom: 1px solid #e2e8f0 !important;
  color: #374151 !important;
  font-weight: 600 !important;
  padding: 16px 12px !important;
}

:deep(.ant-table-tbody > tr > td) {
  border-bottom: 1px solid #f1f5f9 !important;
  padding: 16px 12px !important;
}

:deep(.ant-table-tbody > tr:hover > td) {
  background: #f8fafc !important;
}

:deep(.ant-table-body) {
  height: calc(100% - 55px);
  overflow-y: auto;
}

/* 分页器样式 - 统一配色 */
:deep(.ant-pagination) {
  margin: 16px 0 0 0;
  text-align: center;
  background: white;
  padding: 16px 24px;
  border-top: 1px solid #e2e8f0;
}

:deep(.ant-pagination .ant-pagination-item) {
  border-color: #e2e8f0;
}

:deep(.ant-pagination .ant-pagination-item:hover) {
  border-color: #3b82f6;
}

:deep(.ant-pagination .ant-pagination-item-active) {
  border-color: #3b82f6;
  background: #3b82f6;
}

/* 编辑页面样式 - 统一配色 */
:deep(.template-property-edit-page) {
  height: 100%;
  background: #f8fafc;
  overflow: auto;
  padding: 24px;
}

/* 编辑表单容器 - 统一配色 */
:deep(.ant-form) {
  max-width: none;
  background: white;
  padding: 32px;
  border-radius: 12px;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
  border: 1px solid #e2e8f0;
}

:deep(.ant-form-item-label > label) {
  color: #374151 !important;
  font-weight: 500 !important;
}

:deep(.ant-input) {
  border-color: #e2e8f0;
  border-radius: 8px;
}

:deep(.ant-input:hover) {
  border-color: #cbd5e1;
}

:deep(.ant-input:focus) {
  border-color: #3b82f6;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1);
}

:deep(.ant-input-number) {
  border-color: #e2e8f0;
  border-radius: 8px;
  width: 100%;
}

:deep(.ant-input-number:hover) {
  border-color: #cbd5e1;
}

:deep(.ant-input-number-focused) {
  border-color: #3b82f6;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1);
}

:deep(.ant-radio-button-wrapper) {
  border-color: #e2e8f0;
  color: #64748b;
}

:deep(.ant-radio-button-wrapper:hover) {
  color: #3b82f6;
  border-color: #3b82f6;
}

:deep(.ant-radio-button-wrapper-checked) {
  background: #3b82f6;
  border-color: #3b82f6;
  color: white;
}

:deep(.ant-radio-wrapper) {
  color: #64748b;
}

:deep(.ant-radio-wrapper:hover .ant-radio-inner) {
  border-color: #3b82f6;
}

:deep(.ant-radio-checked .ant-radio-inner) {
  background: #3b82f6;
  border-color: #3b82f6;
}

:deep(.ant-switch-checked) {
  background: #3b82f6;
}

/* 编辑页面内的卡片 - 统一配色 */
:deep(.ant-card) {
  border-radius: 12px;
  margin-bottom: 16px;
  border-color: #e2e8f0;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
}

:deep(.ant-card-head) {
  background: #f8fafc;
  border-bottom: 1px solid #e2e8f0;
}

:deep(.ant-card-head-title) {
  color: #374151;
  font-weight: 600;
}

/* 按钮组优化 - 统一配色 */
:deep(.ant-btn-group) {
  margin-top: 32px;
  display: flex;
  justify-content: center;
  gap: 12px;
}

:deep(.ant-btn) {
  border-radius: 8px;
  height: 40px;
  padding: 0 24px;
  font-weight: 500;
}

:deep(.ant-btn-primary) {
  background: #3b82f6;
  border-color: #3b82f6;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
}

:deep(.ant-btn-primary:hover) {
  background: #2563eb;
  border-color: #2563eb;
  transform: translateY(-1px);
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
}

:deep(.ant-btn-default) {
  background: white;
  border-color: #e2e8f0;
  color: #64748b;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
}

:deep(.ant-btn-default:hover) {
  background: #f8fafc;
  border-color: #cbd5e1;
  color: #374151;
  transform: translateY(-1px);
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
}

:deep(.ant-btn-dashed) {
  border-color: #cbd5e1;
  color: #64748b;
  border-style: dashed;
  background: #f8fafc;
}

:deep(.ant-btn-dashed:hover) {
  border-color: #3b82f6;
  color: #3b82f6;
  background: white;
}

/* 表格内按钮样式统一 */
:deep(.ant-btn-link) {
  color: #3b82f6;
  padding: 0;
  height: auto;
}

:deep(.ant-btn-link:hover) {
  color: #2563eb;
}

:deep(.ant-btn-link.ant-btn-dangerous) {
  color: #dc2626;
}

:deep(.ant-btn-link.ant-btn-dangerous:hover) {
  color: #b91c1c;
}

/* 表格行悬停效果优化 */
:deep(.ant-table-tbody > tr:hover .ant-btn-link) {
  background: rgba(59, 130, 246, 0.1);
  border-radius: 4px;
  padding: 4px 8px;
}

:deep(.ant-table-tbody > tr:hover .ant-btn-link.ant-btn-dangerous) {
  background: rgba(220, 38, 38, 0.1);
}

/* 表格内容样式优化 - 统一配色 */
:deep(.property-section .property-title) {
  color: #1f2937 !important;
  font-weight: 600 !important;
}

:deep(.config-section .detail-code) {
  background: #f1f5f9 !important;
  border: 1px solid #e2e8f0 !important;
  color: #374151 !important;
}

:deep(.config-section .detail-code.expression) {
  background: #eff6ff !important;
  border-color: #bfdbfe !important;
  color: #1d4ed8 !important;
}

:deep(.required-badge) {
  background: #dc2626 !important;
  color: white !important;
}

:deep(.config-type) {
  background: #059669 !important;
  color: white !important;
}

:deep(.config-type.computed) {
  background: #7c3aed !important;
  color: white !important;
}

:deep(.component-text) {
  color: #3b82f6 !important;
}

:deep(.component-text:hover) {
  color: #2563eb !important;
}

:deep(.engine-text) {
  color: #dc2626 !important;
}

:deep(.options-count) {
  color: #d97706 !important;
}

/* Popover 内容样式统一 */
:deep(.ant-popover-inner) {
  border-radius: 8px !important;
  border: 1px solid #e2e8f0 !important;
}

:deep(.ant-popover-inner-content) {
  padding: 16px !important;
}

:deep(.popover-option-item) {
  background: #f8fafc !important;
  border: 1px solid #e2e8f0 !important;
}

:deep(.popover-option-item:hover) {
  background: #eff6ff !important;
  border-color: #bfdbfe !important;
}

:deep(.popover-option-item.is-default) {
  background: #f0fdf4 !important;
  border: 1px solid #bbf7d0 !important;
}

:deep(.popover-option-item .option-code) {
  background: white !important;
  border: 1px solid #cbd5e1 !important;
  color: #3b82f6 !important;
}

:deep(.popover-option-item.is-default .option-code) {
  border-color: #16a34a !important;
  color: #15803d !important;
}

:deep(.default-tag) {
  background: #16a34a !important;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .header-content {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }
  
  .header-actions {
    width: 100%;
    justify-content: flex-end;
  }
  
  .action-group {
    flex-wrap: wrap;
  }
  
  .modal-subtitle {
    display: none;
  }
}

@media (max-width: 768px) {
  .header-left {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .header-icon {
    width: 40px;
    height: 40px;
    margin-right: 0;
  }
  
  .header-actions {
    flex-direction: column;
    width: 100%;
    gap: 12px;
  }
  
  .action-group {
    flex-direction: column;
    width: 100%;
    gap: 8px;
  }
  
  .action-divider {
    width: 100%;
    height: 1px;
  }
  
  .action-btn {
    width: 100%;
    justify-content: center;
  }
  
  .action-btn:disabled {
    transform: none;
  }
}</style>

<style>
/* 全局样式覆盖 - 确保弹窗样式生效 */
.ant-modal.template-property-modal .ant-modal-content {
  border-radius: 12px !important;
  overflow: hidden !important;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25) !important;
  padding: 0 !important;
}

.ant-modal.template-property-modal .ant-modal-body {
  padding: 0 !important;
}

.ant-modal.template-property-modal .ant-modal-header {
  display: none !important;
}

.ant-modal.template-property-modal .ant-modal-close {
  top: 16px !important;
  right: 16px !important;
  z-index: 1000 !important;
  background: rgba(255, 255, 255, 0.9) !important;
  border-radius: 50% !important;
  width: 32px !important;
  height: 32px !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15) !important;
}

.ant-modal.template-property-modal .ant-modal-close:hover {
  background: rgba(255, 255, 255, 1) !important;
}</style>
