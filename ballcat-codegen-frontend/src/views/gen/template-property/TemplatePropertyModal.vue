<template>
  <a-modal
    v-model:open="visible"
    :title="null"
    :mask-closable="false"
    :footer="null"
    :body-style="{ padding: 0 }"
  :width="'clamp(960px, 86vw, 1180px)'"
    :closable="true"
    class="template-property-modal"
    @cancel="handleClose"
  >
    <!-- 自定义标题栏 -->
    <div class="modal-header">
      <div class="header-content">
        <div class="header-left">
          <div class="header-icon">
            <SettingOutlined />
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
        size="small"
              class="action-btn"
              @click="handleCreate"
              :disabled="!currentTemplateGroup"
            :title="'新建属性'"
            >
              <template #icon>
        <PlusOutlined />
              </template>
              新建属性
            </a-button>
            <div class="action-divider"></div>
            <a-upload
              accept=".json"
              :show-upload-list="false"
              :custom-request="handlePropertyImport"
              :disabled="!currentTemplateGroup"
            :title="'导入属性'"
            >
        <a-button type="default" size="small" class="action-btn">
                <template #icon>
          <UploadOutlined />
                </template>
                导入属性
              </a-button>
            </a-upload>
      <a-button 
              type="default" 
              size="small"
              class="action-btn"
              @click="handlePropertyExport"
              :disabled="!currentTemplateGroup"
            :title="'导出属性'"
            >
              <template #icon>
        <DownloadOutlined />
              </template>
              导出属性
            </a-button>
          </div>
          
          <!-- 编辑页面按钮组 -->
      <div v-show="!showTable" class="action-group">
    <a-button 
        type="default"
        size="small"
              class="action-btn"
              @click="backToPage(false)"
            :title="'返回列表'"
            >
              <template #icon>
        <ArrowLeftOutlined />
              </template>
              返回列表
            </a-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 内容区域 -->
    <div class="modal-body">
  <!-- 面包屑导航移除以节省垂直空间 -->

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
import { nextTick, ref } from 'vue'
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
import { PlusOutlined, UploadOutlined, DownloadOutlined, ArrowLeftOutlined, SettingOutlined, RightOutlined } from '@ant-design/icons-vue'

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

<style scoped lang="less">
/* 自定义标题栏 */
.modal-header {
  background: linear-gradient(135deg, @slate-50 0%, @slate-200 100%);
  color: @slate-800;
  padding: 0;
  border-bottom: 1px solid @slate-200;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px; /* 更紧凑 */
  gap: 12px;
}

.header-left {
  display: flex;
  align-items: center;
  flex: 1;
}

.header-icon {
  width: 48px;
  height: 48px;
  background: fade(@blue-500, 10%);
  border: 1px solid fade(@blue-500, 20%);
  border-radius: @border-radius-xl;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
}

.header-icon .anticon {
  color: @blue-500;
  font-size: 24px;
  line-height: 1;
}
.header-icon .anticon > svg { display: block; }

.header-text {
  flex: 1;
  min-width: 0; /* 允许截断 */
}

.modal-title {
  font-size: @font-size-xl; /* 略缩小标题 */
  font-weight: 600;
  margin: 0 0 2px 0;
  color: @slate-800;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.modal-subtitle {
  font-size: @font-size-sm; /* 缩小副标题 */
  margin: 0;
  color: @slate-500;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-right: 48px; /* 为关闭按钮留出空间 */
  flex-wrap: nowrap; /* 强制单行 */
}

.action-group {
  display: flex;
  align-items: center;
  gap: 6px;
  flex-wrap: nowrap;
}

.action-divider {
  width: 1px;
  height: 18px;
  background: @slate-300;
}

/* 紧凑按钮：更小的内边距与图标间距 */
.action-btn { height: 28px; gap: 6px; padding: 0 10px; border-radius: @border-radius-md; font-size: @font-size-sm; }
.action-btn .anticon { font-size: 13px; display: inline-flex; align-items: center; justify-content: center; line-height: 1; vertical-align: -0.125em; }
.action-btn .anticon > svg { display: block; }

/* 缩小 AntD 按钮内置的图标-文字间距 */
.action-btn :deep(.anticon + span) { margin-left: 4px !important; }
.action-btn :deep(span > .anticon + span) { margin-left: 4px !important; }

/* 对齐上传按钮的容器，避免额外间距 */
:deep(.ant-upload) { display: inline-flex; }

/* 内容区域 */
.modal-body {
  background: @slate-50;
  height: 66vh; /* 略降整体高度 */
  min-height: 520px;
  max-height: 760px;
  display: flex;
  flex-direction: column;
}

/* 面包屑导航 */
.breadcrumb-nav {
  background: @bg-color-container;
  border-bottom: 1px solid @slate-200;
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
  font-size: @font-size-base;
  color: @slate-500;
}

.breadcrumb-item.current {
  color: @slate-800;
  font-weight: 500;
}

.breadcrumb-separator {
  color: @slate-400;
  display: inline-flex;
  line-height: 1;
}

/* 内容包装器 */
.content-wrapper {
  flex: 1;
  overflow: auto; /* 允许内容滚动，避免小屏无法操作 */
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
  background: @slate-50;
  margin: 0;
  border-radius: 0;
  box-shadow: none;
  overflow: hidden;
}

:deep(.template-property-page__table) {
  flex: 1;
  background: @slate-50;
  overflow: auto;
}

/* 表格样式优化 - 统一配色 */
:deep(.ant-table-wrapper) {
  height: 100%;
  background: @bg-color-container;
  border-radius: @border-radius-xl;
  box-shadow: @shadow-xs-05;
  border: 1px solid @slate-200;
}

:deep(.ant-table-container) {
  height: 100%;
  border-radius: @border-radius-xl;
  overflow: hidden;
}

:deep(.ant-table-thead > tr > th) {
  background: @slate-50 !important;
  border-bottom: 1px solid @slate-200 !important;
  color: #374151 !important;
  font-weight: 600 !important;
  padding: 16px 12px !important;
}

:deep(.ant-table-tbody > tr > td) {
  border-bottom: 1px solid @slate-100 !important;
  padding: 16px 12px !important;
}

:deep(.ant-table-tbody > tr:hover > td) {
  background: @slate-50 !important;
}

:deep(.ant-table-body) {
  height: calc(100% - 55px);
  overflow-y: auto;
}

/* 分页器样式 - 统一配色 */
:deep(.ant-pagination) {
  margin: 16px 0 0 0;
  text-align: center;
  background: @bg-color-container;
  padding: 16px 24px;
  border-top: 1px solid @slate-200;
}

:deep(.ant-pagination .ant-pagination-item) {
  border-color: @slate-200;
}

:deep(.ant-pagination .ant-pagination-item:hover) {
  border-color: @blue-500;
}

:deep(.ant-pagination .ant-pagination-item-active) {
  border-color: @blue-500;
  background: @blue-500;
}

/* 编辑页面样式 - 统一配色 */
:deep(.template-property-edit-page) {
  height: 100%;
  display: flex;
  flex-direction: column;
  min-height: 0; /* 允许内部滚动 */
  background: @slate-50;
  overflow: auto;
  padding: 0; /* 由表单自身提供内边距，避免双重 padding */
}

/* 编辑表单容器 - 统一配色 */
:deep(.ant-form) {
  max-width: none;
  background: @bg-color-container;
  padding: 32px;
  /* 顶部直角，与自定义头部无缝衔接 */
  border-radius: 0 0 @border-radius-xl @border-radius-xl;
  box-shadow: @shadow-xs-05;
  border: 1px solid @slate-200;
}

:deep(.ant-form-item-label > label) {
  color: #374151 !important;
  font-weight: 500 !important;
}

:deep(.ant-input) {
  border-color: @slate-200;
  border-radius: @border-radius-lg;
}

:deep(.ant-input:hover) {
  border-color: @slate-300;
}

:deep(.ant-input:focus) {
  border-color: @blue-500;
  box-shadow: 0 0 0 2px fade(@blue-500, 10%);
}

:deep(.ant-input-number) {
  border-color: @slate-200;
  border-radius: @border-radius-lg;
  width: 100%;
}

:deep(.ant-input-number:hover) {
  border-color: @slate-300;
}

:deep(.ant-input-number-focused) {
  border-color: @blue-500;
  box-shadow: 0 0 0 2px fade(@blue-500, 10%);
}

:deep(.ant-radio-button-wrapper) {
  border-color: @slate-200;
  color: @slate-500;
}

:deep(.ant-radio-button-wrapper:hover) {
  color: @blue-500;
  border-color: @blue-500;
}

:deep(.ant-radio-button-wrapper-checked) {
  background: @blue-500;
  border-color: @blue-500;
  color: white;
}

:deep(.ant-radio-wrapper) {
  color: @slate-500;
}

:deep(.ant-radio-wrapper:hover .ant-radio-inner) {
  border-color: @blue-500;
}

:deep(.ant-radio-checked .ant-radio-inner) {
  background: @blue-500;
  border-color: @blue-500;
}

:deep(.ant-switch-checked) {
  background: @blue-500;
}

/* 编辑页面内的卡片 - 统一配色 */
:deep(.ant-card) {
  border-radius: @border-radius-xl;
  margin-bottom: 16px;
  border-color: @slate-200;
  box-shadow: @shadow-xs-05;
}

:deep(.ant-card-head) {
  background: @slate-50;
  border-bottom: 1px solid @slate-200;
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
  border-radius: @border-radius-lg;
  height: 40px;
  font-weight: 500;
}

:deep(.ant-btn-primary) {
  background: @blue-500;
  border-color: @blue-500;
  box-shadow: @shadow-xs-05;
}

:deep(.ant-btn-primary:hover) {
  background: @blue-600;
  border-color: @blue-600;
  transform: translateY(-1px);
  box-shadow: @shadow-md-10;
}

:deep(.ant-btn-default) {
  background: @bg-color-container;
  border-color: @slate-200;
  color: @slate-500;
  box-shadow: @shadow-xs-05;
}

:deep(.ant-btn-default:hover) {
  background: @slate-50;
  border-color: @slate-300;
  color: #374151;
  transform: translateY(-1px);
  box-shadow: @shadow-md-10;
}

:deep(.ant-btn-dashed) {
  border-color: @slate-300;
  color: @slate-500;
  border-style: dashed;
  background: @slate-50;
}

:deep(.ant-btn-dashed:hover) {
  border-color: @blue-500;
  color: @blue-500;
  background: @bg-color-container;
}

/* 表格内按钮样式统一 */
:deep(.ant-btn-link) {
  color: @blue-500;
  padding: 0;
  height: auto;
}

:deep(.ant-btn-link:hover) {
  color: @blue-600;
}

:deep(.ant-btn-link.ant-btn-dangerous) {
  color: @red-600;
}

:deep(.ant-btn-link.ant-btn-dangerous:hover) {
  color: @red-700;
}

/* 表格行悬停效果优化 */
:deep(.ant-table-tbody > tr:hover .ant-btn-link) {
  background: fade(@blue-500, 10%);
  border-radius: 4px;
  padding: 4px 8px;
}

:deep(.ant-table-tbody > tr:hover .ant-btn-link.ant-btn-dangerous) {
  background: fade(@red-600, 10%);
}

/* 表格内容样式优化 - 统一配色 */
:deep(.property-section .property-title) {
  color: #1f2937 !important;
  font-weight: 600 !important;
}

:deep(.config-section .detail-code) {
  background: @slate-100 !important;
  border: 1px solid @slate-200 !important;
  color: #374151 !important;
}

:deep(.config-section .detail-code.expression) {
  background: @blue-50 !important;
  border-color: @blue-200 !important;
  color: @blue-700 !important;
}

:deep(.required-badge) {
  background: @red-600 !important;
  color: white !important;
}

:deep(.config-type) {
  background: @teal-600 !important;
  color: white !important;
}

:deep(.config-type.computed) {
  background: @purple-600 !important;
  color: white !important;
}

:deep(.component-text) {
  color: @blue-500 !important;
}

:deep(.component-text:hover) {
  color: @blue-600 !important;
}

:deep(.engine-text) {
  color: @red-600 !important;
}

:deep(.options-count) {
  color: @slate-500 !important;
}

/* Popover 内容样式统一 */
:deep(.ant-popover-inner) {
  border-radius: @border-radius-lg !important;
  border: 1px solid @slate-200 !important;
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
  .modal-title { font-size: @font-size-lg; }
  .modal-subtitle { display: none; }
  .header-actions { margin-right: 40px; gap: 6px; }
}

@media (max-width: 768px) {
  .header-content { padding: 10px 12px; }
  .header-icon { width: 36px; height: 36px; margin-right: 10px; }
  .modal-title { font-size: @font-size-base; }
  .header-actions { margin-right: 36px; gap: 4px; }
  .action-divider { display: none; }
  /* 窄屏仅保留图标，隐藏文字，节省空间保持单行 */
  .action-btn { padding: 0 8px; height: 26px; }
  .action-btn :deep(.anticon + span) { display: none !important; }
  .action-btn :deep(span > .anticon + span) { display: none !important; }
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
