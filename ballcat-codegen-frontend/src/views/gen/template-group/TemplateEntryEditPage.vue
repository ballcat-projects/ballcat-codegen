<template>
  <a-card
    ref="entryEditor"
    size="small"
    :bordered="false"
    :head-style="{ 
      minHeight: '60px', 
      padding: 0,
      border: 'none'
    }"
    :body-style="{
      padding: 0,
      height: isFullscreen ? 'calc(100vh - 61px)' : 'calc(100vh - 242px)'
    }"
  >
    <template #title>
      <div class="template-header">
        <div class="template-info">
          <div class="template-name">
            <svg class="template-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path>
            </svg>
            {{ templateGroup?.name }}
          </div>
        </div>
        <div class="template-actions">
          <a-space size="small">
            <a-button type="text" size="small" class="action-btn primary" @click="handleProperty">
              <template #icon>
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z"></path>
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"></path>
                </svg>
              </template>
              属性配置
            </a-button>
            <a-dropdown trigger="click" placement="bottomRight">
              <a-button type="text" size="small" class="action-btn">
                <template #icon>
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10"></path>
                  </svg>
                </template>
                文件导入/导出
                <svg class="w-3 h-3 ml-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path>
                </svg>
              </a-button>
              <template #overlay>
                <a-menu class="file-menu">
                  <a-menu-item>
                    <a-upload
                      accept=".zip,.7z,.rar"
                      :show-upload-list="false"
                      :custom-request="handleFileImport"
                    >
                      <div class="menu-item-content">
                        <svg class="menu-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M9 19l3 3m0 0l3-3m-3 3V10"></path>
                        </svg>
                        <span>导入文件包</span>
                      </div>
                    </a-upload>
                  </a-menu-item>
                  <a-menu-item @click="handleFileExport">
                    <div class="menu-item-content">
                      <svg class="menu-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12"></path>
                      </svg>
                      <span>导出文件包</span>
                    </div>
                  </a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
            <a-divider type="vertical" />
            <a-button type="text" size="small" class="action-btn" @click="toggle">
              <template #icon>
                <svg v-if="isFullscreen" class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
                </svg>
                <svg v-else class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 8V4m0 0h4M4 4l5 5m11-1V4m0 0h-4m4 0l-5 5M4 16v4m0 0h4m-4 0l5-5m11 5l-5-5m5 5v-4m0 4h-4"></path>
                </svg>
              </template>
              {{ isFullscreen ? '退出全屏' : '全屏模式' }}
            </a-button>
            <a-button type="text" size="small" class="action-btn secondary" @click="handleGoBack">
              <template #icon>
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"></path>
                </svg>
              </template>
              返回列表
            </a-button>
          </a-space>
        </div>
      </div>
    </template>

    <splitpanes class="default-theme">
      <pane size="25" style="border-bottom-left-radius: 10px">
        <template-entry-tree ref="templateEntryTreeRef" @edit-template-info="editTemplateInfo" />
      </pane>
      <pane size="75" style="border-bottom-right-radius: 10px">
        <template-entry-content-editor ref="editorRef" @re-upload="updateTemplateEntry" />
      </pane>
    </splitpanes>
  </a-card>
</template>

<script setup lang="ts">
import { Splitpanes, Pane } from 'splitpanes'
import 'splitpanes/dist/splitpanes.css'
import TemplateEntryTree from '@/views/gen/template-group/components/TemplateEntryTree.vue'
import type { TemplateGroup } from '@/api/gen/template-group/types'
import { ref } from 'vue'
import type { TemplateEntry } from '@/api/gen/template-entry/types'
import TemplateEntryContentEditor from '@/views/gen/template-group/components/TemplateEntryContentEditor.vue'
import { Modal } from 'ant-design-vue'
import type { TemplateContentEditorInstance } from '@/views/gen/template-group/components/types'
import { useFullscreen } from '@vueuse/core'
import { exportTemplateGroupEntries, importTemplateGroupEntries } from '@/api/gen/template-entry'
import { doRequest } from '@/utils/axios/request'
import { remoteFileDownload } from '@/utils/file-util'
import type { UploadRequestOption } from 'ant-design-vue/es/vc-upload/interface'

const emits = defineEmits<{
  (e: 'handle-property'): void
  (e: 'go-back'): void
}>()

const entryEditor = ref<HTMLElement | null>(null)
const templateEntryTreeRef = ref()
const { isFullscreen, exit, toggle } = useFullscreen(entryEditor)

// 模板组
const templateGroup = ref<TemplateGroup>()

// 模板信息编辑器实例
const editorRef = ref<TemplateContentEditorInstance>()

/** 模板修改属性 **/
const handleProperty = () => {
  emits('handle-property')
}

/** 文件包导入 **/
function handleFileImport(fileInfo: UploadRequestOption) {
  if (!templateGroup.value?.groupKey) return
  
  doRequest({
    request: importTemplateGroupEntries(templateGroup.value.groupKey, fileInfo.file as File),
    successMessage: '导入模板组文件成功！',
    onSuccess() {
      // 重新加载文件树
      templateEntryTreeRef.value?.load(templateGroup.value!.groupKey)
    }
  })
}

/** 文件包导出 **/
function handleFileExport() {
  if (!templateGroup.value?.groupKey) return
  
  exportTemplateGroupEntries(templateGroup.value.groupKey).then(response => {
    remoteFileDownload(response)
  })
}

/** 返回上级 */
function handleGoBack() {
  if (editorRef.value?.checkSaveState()) {
    isFullscreen.value && exit()
    emits('go-back')
    return
  }
  Modal.confirm({
    content: '部分文件已修改但未保存，确定放弃修改内容吗？ (选中文件编辑器后，按 Ctrl+S 保存）',
    onOk: () => emits('go-back')
  })
}

/** 编辑模板信息内容 */
function editTemplateInfo(entry: TemplateEntry) {
  editorRef.value?.editContent(entry)
}

/** 编辑模板 */
function updateTemplateEntry() {
  templateEntryTreeRef.value?.updateEntry()
}

defineExpose({
  edit(record: TemplateGroup) {
    templateGroup.value = record
    // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
    const templateGroupKey = record.groupKey!
    templateEntryTreeRef.value?.load(templateGroupKey)
    editorRef.value?.load(templateGroupKey)
  }
})
</script>

<style lang="less">
// 模板头部样式
.template-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 20px;
  min-height: 60px;
  background: linear-gradient(135deg, @slate-50 0%, @slate-200 100%);
  border-radius: @border-radius-md @border-radius-md 0 0;
  margin: 0;
}

.template-info {
  display: flex;
  flex-direction: column;
  justify-content: center;
  flex: 1;
  min-height: 36px;
}

.template-name {
  display: flex;
  align-items: center;
  font-size: @font-size-lg;
  font-weight: @font-weight-semibold;
  color: @slate-800;
  margin-bottom: 4px;
  line-height: 1.2;
}

.template-icon {
  width: 18px;
  height: 18px;
  margin-right: 8px;
  color: @blue-500;
  flex-shrink: 0;
}

.template-actions {
  display: flex;
  align-items: center;
}

// 操作按钮样式
.action-btn {
  display: inline-flex !important;
  align-items: center !important;
  font-size: @font-size-2sm !important; // 局部字号略小，保持现有视觉
  
  &:hover {
    transform: translateY(-1px);
    box-shadow: @shadow-button-hover;
  }

  &.primary {
    background-color: fade(@green-600, 10%) !important;
    color: @teal-600 !important;
    border-color: fade(@green-600, 20%) !important;
    
    &:hover {
      background-color: fade(@green-600, 15%) !important;
      border-color: fade(@green-600, 30%) !important;
    }
  }
  
  &.secondary {
    background-color: fade(@slate-600, 10%) !important;
    color: @slate-600 !important;
    border-color: fade(@slate-600, 20%) !important;
    
    &:hover {
      background-color: fade(@slate-600, 15%) !important;
      border-color: fade(@slate-600, 30%) !important;
    }
  }
}

// 分割线样式
:deep(.ant-divider-vertical) {
  height: 20px;
  margin: 0 8px;
  border-color: @slate-300;
}

// 面板容器样式优化
.splitpanes.default-theme {
  background: @slate-50;
  border-radius: 0 0 @border-radius-lg @border-radius-lg;
  overflow: hidden;
  box-shadow: inset 0 1px 0 fade(@slate-200, 50%);
}

// 面板样式优化
.splitpanes.default-theme .splitpanes__pane {
  background-color: @bg-color-container !important;
  border: none !important;
  position: relative;
  
  &:first-child {
    border-right: 1px solid @slate-200;
    background: linear-gradient(135deg, @gray-1 0%, @slate-50 100%) !important;
  }
  
  &:last-child {
    background: @bg-color-container !important;
  }
}

// 分割器样式优化
.default-theme.splitpanes--vertical > .splitpanes__splitter {
  background: linear-gradient(90deg, @slate-200 0%, @slate-300 50%, @slate-200 100%) !important;
  width: 4px;
  border: none;
  position: relative;
  cursor: col-resize;
  transition: all 0.2s ease;
  
  &:hover {
    background: linear-gradient(90deg, @blue-500 0%, @blue-600 50%, @blue-500 100%) !important;
    width: 6px;
    box-shadow: 0 0 10px fade(@blue-500, 30%);
  }
  
  &:before {
    content: '';
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    width: 2px;
    height: 30px;
    background: fade(@gray-1, 60%);
    border-radius: 1px;
  }
}

// 美化下拉菜单
.file-menu {
  border-radius: @border-radius-lg !important;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15) !important;
  border: 1px solid @slate-200 !important;
  padding: @spacing-xs !important;
  min-width: 180px;
  
  .ant-menu-item {
    border-radius: @border-radius-md !important;
    margin-bottom: 2px !important;
    padding: @spacing-sm @spacing-md !important;
    transition: @animation-base !important;
    
    &:hover {
      background: linear-gradient(135deg, @blue-50 0%, @blue-100 100%) !important;
      transform: translateX(2px);
    }
    
    &:last-child {
      margin-bottom: 0 !important;
    }
  }
}

.menu-item-content {
  display: flex;
  align-items: center;
  font-size: @font-size-2sm;
  font-weight: @font-weight-medium;
  color: @gray-700;
}

.menu-icon {
  width: 16px;
  height: 16px;
  margin-right: 10px;
  color: @slate-500;
}

// 上传组件样式优化
:deep(.ant-upload) {
  display: block !important;
  width: 100% !important;
  padding: 0 !important;
  background: transparent !important;
  border: none !important;
}

// 滚动条样式优化
.pane-scroll::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

.pane-scroll::-webkit-scrollbar-track {
  background: @slate-100;
  border-radius: 4px;
}

.pane-scroll::-webkit-scrollbar-thumb {
  background: linear-gradient(135deg, @slate-300 0%, @slate-400 100%);
  border-radius: 4px;
  transition: all 0.2s ease;
  
  &:hover {
    background: linear-gradient(135deg, @slate-400 0%, @slate-500 100%);
  }
}

// 响应式设计
@media (max-width: 1200px) {
  .template-header {
    padding: 10px 16px;
    flex-direction: column;
    min-height: 70px;
    gap: 6px;
  }
  
  .template-info {
    width: 100%;
    align-items: center;
    text-align: center;
    min-height: auto;
  }
  
  .template-actions {
    width: 100%;
    justify-content: center;
  }
  
  .action-btn {
    padding: 4px 8px !important;
    font-size: 12px !important;
  }
}

@media (max-width: 768px) {
  .template-name {
    font-size: 14px;
  }
  
  .action-btn span {
    display: none;
  }
  
  .splitpanes.default-theme .splitpanes__pane:first-child {
    display: none;
  }
}

// 卡片样式优化
:deep(.ant-card) {
  border-radius: @border-radius-lg;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  border: 1px solid @slate-200;
  overflow: hidden;
}

:deep(.ant-card-head) {
  padding: 0 !important;
  border-bottom: none !important;
  background: transparent !important;
}

:deep(.ant-card-body) {
  padding: 0 !important;
}
</style>
