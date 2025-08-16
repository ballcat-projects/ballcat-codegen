<template>
  <div class="template-property-page">
    <div class="template-property-page__container">
      <!--数据表格区域-->
      <div class="template-property-page__table">
        <a-table
          ref="table"
          size="small"
          row-key="id"
          :columns="columns"
          :data-source="dataSource"
          :pagination="pagination"
          :loading="loading"
          :scroll="{ x: 720 }"
          @change="tableState.handleTableChange"
        >
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'property'">
          <div class="property-section">
            <!-- 主标题区域 -->
            <div class="property-main">
              <h4 class="property-title">{{ record.title }}</h4>
              <span v-if="record.required === 1" class="required-badge">必填</span>
            </div>
            <!-- 详细信息区域 -->
            <div class="property-details">
              <div class="detail-item">
                <span class="detail-label">属性键:</span>
                <code class="detail-code">{{ record.propKey }}</code>
              </div>
              <div class="detail-item">
                <span class="detail-label">排序:</span>
                <span class="detail-value">{{ record.orderValue }}</span>
              </div>
            </div>
          </div>
        </template>
        
        <template v-else-if="column.dataIndex === 'details'">
          <div class="config-section">
            <!-- 简化的类型信息 -->
            <div class="config-primary">
              <!-- 配置属性：简化显示 -->
              <div v-if="record.propType === 1" class="config-info">
                <span class="config-type">配置</span>
                <a-popover v-if="record.componentType" placement="topLeft" trigger="hover">
                  <template #content>
                    <div v-if="record.componentOptions?.length" class="popover-options">
                      <div class="popover-title">{{ getComponentTypeLabel(record.componentType) }} 组件选项</div>
                      <div class="popover-option-list">
                        <div 
                          v-for="option in record.componentOptions" 
                          :key="option.value"
                          class="popover-option-item"
                          :class="{ 'is-default': option.value === record.defaultValue }"
                        >
                          <span class="option-label">{{ option.name }}</span>
                          <span class="option-separator">:</span>
                          <code class="option-code">{{ option.value }}</code>
                          <span v-if="option.value === record.defaultValue" class="default-tag">默认</span>
                        </div>
                      </div>
                    </div>
                    <div v-else class="popover-no-options">
                      <div class="popover-title">{{ getComponentTypeLabel(record.componentType) }} 组件</div>
                      <div class="no-options-text">该组件无可配置选项</div>
                    </div>
                  </template>
                  <span class="component-text">
                    {{ getComponentTypeLabel(record.componentType) }}
                    <span v-if="record.componentOptions?.length" class="options-count">({{ record.componentOptions.length }}个选项)</span>
                  </span>
                </a-popover>
              </div>
              
              <!-- 计算属性：简化显示 -->
              <div v-if="record.propType === 2" class="config-info">
                <span class="config-type computed">计算</span>
                <span class="engine-text">{{ getEngineTypeName(record.engineType) }}</span>
              </div>
            </div>
            
            <!-- 默认值/表达式信息 -->
            <div v-if="(record.propType === 1 && record.defaultValue) || (record.propType === 2 && record.expression)" class="config-secondary">
              <!-- 配置属性：默认值 -->
              <div v-if="record.propType === 1 && record.defaultValue" class="detail-item">
                <span class="detail-label">默认值:</span>
                <a-tooltip v-if="record.defaultValue && record.defaultValue.length > 30" placement="top">
                  <template #title>{{ record.defaultValue }}</template>
                  <code class="detail-code">{{ truncateText(record.defaultValue, 30) }}</code>
                </a-tooltip>
                <code v-else class="detail-code">{{ record.defaultValue }}</code>
                <span v-if="getDefaultOptionName(record)" class="option-name">
                  ({{ getDefaultOptionName(record) }})
                </span>
              </div>
              
              <!-- 计算属性：表达式 -->
              <div v-if="record.propType === 2 && record.expression" class="detail-item">
                <span class="detail-label">表达式:</span>
                <a-tooltip v-if="record.expression && record.expression.length > 50" placement="top">
                  <template #title>
                    <pre class="tooltip-code">{{ record.expression }}</pre>
                  </template>
                  <code class="detail-code expression">{{ truncateText(record.expression, 50) }}</code>
                </a-tooltip>
                <code v-else class="detail-code expression">{{ record.expression }}</code>
              </div>
            </div>
          </div>
        </template>
        
        <template v-else-if="column.dataIndex === 'remarks'">
          <div class="remarks-section">
            <span v-if="record.remarks" class="remarks-text">
              {{ record.remarks }}
            </span>
            <span v-else class="remarks-empty">-</span>
          </div>
        </template>
        
        <template v-else-if="column.dataIndex === 'action'">
          <div class="action-buttons">
            <a-button type="link" size="small" @click="handleUpdate(record)">
              编辑
            </a-button>
            <a-popconfirm title="确定要删除这个属性吗?" @confirm="handleRemove(record.id)">
              <a-button type="link" size="small" danger>
                删除
              </a-button>
            </a-popconfirm>
          </div>
        </template>
      </template>
        </a-table>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import useTable from '@/hooks/table'
import { queryTemplatePropertyPage, removeTemplateProperty } from '@/api/gen/template-property'
import { doRequest } from '@/utils/axios/request'
import type { TemplateGroup } from '@/api/gen/template-group/types'
import type { TemplateProperty } from '@/api/gen/template-property/types'
import type { PageParam } from '@/api/types'

const emits = defineEmits<{
  // 提交完成事件
  (e: 'update', record: TemplateProperty): void
}>()

const columns = [
  {
    title: '属性配置',
    dataIndex: 'property',
    width: 320,
    fixed: 'left'
  },
  {
    title: '配置详情',
    dataIndex: 'details',
    minWidth: 280
  },
  {
    title: '备注',
    dataIndex: 'remarks',
    width: 200
  },
  {
    title: '操作',
    dataIndex: 'action',
    width: 120,
    fixed: 'right'
  }
]

// 所属模板组标识
const templateGroupKey = ref<string>()

// 数据表格
const tableState = useTable<TemplateProperty>({
  pageRequest: (query: PageParam) => {
    const params = Object.assign({ groupKey: templateGroupKey.value, sort: 'orderValue' }, query)
    return queryTemplatePropertyPage(params)
  }
})
const { dataSource, pagination, loading } = tableState

// 获取模板引擎类型名称
function getEngineTypeName(engineType?: number) {
  switch (engineType) {
    case 1:
      return 'Velocity'
    case 2:
      return 'Freemarker'
    default:
      return 'Velocity' // 计算属性默认使用 Velocity
  }
}

// 获取组件类型标签
function getComponentTypeLabel(componentType?: string) {
  switch (componentType) {
    case 'input':
      return 'Input'
    case 'input-number':
      return 'Number'
    case 'select':
      return 'Select'
    case 'radio':
      return 'Radio'
    default:
      return componentType || 'Input'
  }
}

// 文本截断函数
function truncateText(text: string, maxLength: number) {
  if (!text) return ''
  return text.length > maxLength ? text.substring(0, maxLength) + '...' : text
}

// 获取默认选项的名称
function getDefaultOptionName(record: TemplateProperty) {
  if (!record.defaultValue || !record.componentOptions?.length) return null
  const defaultOption = record.componentOptions.find(option => option.value === record.defaultValue)
  return defaultOption?.name || null
}

function handleUpdate(record: TemplateProperty) {
  emits('update', record)
}

function handleRemove(id: number) {
  doRequest({
    request: removeTemplateProperty(id),
    successMessage: '删除成功',
    onSuccess() {
      tableState.reloadTable(false)
    }
  })
}

defineExpose({
  load(templateGroup: TemplateGroup) {
    templateGroupKey.value = templateGroup.groupKey
    tableState.loadData()
  },
  reloadTable() {
    tableState.loadData()
  }
})
</script>

<script lang="ts">
export default {
  name: 'TemplatePropertyEditPage'
}
</script>

<style scoped lang="less">
/* 表格整体样式 */
:deep(.ant-table) {
  background: @bg-color-container;
  border-radius: 0;
  box-shadow: none;
  border: none;
}

:deep(.ant-table-thead > tr > th) {
  background: @slate-50;
  border-bottom: 2px solid @slate-200;
  padding: 20px 16px !important;
  font-weight: 600;
  font-size: @font-size-2lg;
  color: @slate-800;
  line-height: @line-height-compact;
}

:deep(.ant-table-tbody > tr > td) {
  border-bottom: 1px solid @slate-100;
  padding: 20px 16px !important;
  vertical-align: top;
  font-size: @font-size-base;
}

:deep(.ant-table-tbody > tr:hover > td) {
  background: @slate-50 !important;
}

/* 修复测量行占用空间的问题 */
:deep(.ant-table-measure-row) {
  height: 0 !important;
  line-height: 0 !important;
  padding: 0 !important;
  margin: 0 !important;
  visibility: hidden !important;
  display: none !important;
}

:deep(.ant-table-measure-row > td) {
  height: 0 !important;
  line-height: 0 !important;
  padding: 0 !important;
  margin: 0 !important;
  border: none !important;
  font-size: 0 !important;
  overflow: hidden !important;
  min-height: 0 !important;
  max-height: 0 !important;
}

/* 分页器样式修复 */
:deep(.ant-pagination-item-active) {
  background-color: #3b82f6;
  border-color: #3b82f6;
}

:deep(.ant-pagination-item-active a) {
  color: white;
}

:deep(.ant-pagination-item:hover) {
  border-color: #3b82f6;
}

:deep(.ant-pagination-item:hover a) {
  color: #3b82f6;
}

/* 属性配置区域 */
.property-section {
  line-height: 1.6;
}

.property-main {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.property-title {
  margin: 0;
  font-size: @font-size-lg;
  font-weight: 600;
  color: @slate-800;
  line-height: @line-height-compact;
}

.required-badge {
  background: @red-600;
  color: white;
  padding: 3px 8px;
  border-radius: 4px;
  font-size: @font-size-2xs;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.property-details {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

/* 配置详情区域 */
.config-section {
  line-height: 1.6;
}

.config-primary {
  margin-bottom: 12px;
}

.config-info {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
}

.config-type {
  background: @teal-600;
  color: white;
  padding: 4px 10px;
  border-radius: @border-radius-base;
  font-size: @font-size-sm;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.config-type.computed {
  background: @purple-600;
}

.component-text {
  color: @blue-500;
  cursor: pointer;
  font-size: @font-size-base;
  font-weight: 500;
}

.component-text:hover {
  color: @blue-600;
  text-decoration: underline;
}

.engine-text {
  color: @red-600;
  font-size: @font-size-base;
  font-weight: 500;
}

.options-count {
  color: @slate-500;
  font-size: @font-size-sm;
  margin-left: 4px;
}

/* 详情项样式 */
.detail-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  line-height: 1.5;
}

.detail-label {
  color: @slate-500;
  font-weight: 500;
  min-width: 70px;
}

.detail-code {
  background: @slate-100;
  border: 1px solid @slate-200;
  padding: 4px 8px;
  border-radius: @border-radius-base;
  font-family: 'SFMono-Regular', 'Monaco', 'Consolas', monospace;
  font-size: @font-size-sm;
  color: @blue-500;
  font-weight: 500;
}

.detail-code.expression {
  color: @blue-700;
  background: @blue-50;
  border-color: @blue-200;
}

.detail-value {
  font-size: @font-size-2sm;
  color: @slate-800;
  font-weight: 500;
}

.option-name {
  font-size: @font-size-2xs;
  color: @green-600;
  font-style: italic;
  font-weight: 500;
}

/* 备注区域 */
.remarks-section {
  padding: 4px 0;
}

.remarks-text {
  font-size: @font-size-2sm;
  color: @slate-500;
  line-height: 1.5;
}

.remarks-empty {
  font-size: @font-size-2sm;
  color: @slate-300;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  justify-content: center;
  gap: 8px;
}

.action-buttons .ant-btn {
  padding: 8px 12px;
  height: auto;
  border: none;
  border-radius: @border-radius-base;
  font-size: @font-size-2sm;
  font-weight: 500;
  transition: all 0.2s ease;
}

.action-buttons .ant-btn-link {
  color: @blue-500;
}

.action-buttons .ant-btn-link:hover {
  color: @blue-600;
  background-color: fade(@blue-500, 10%);
}

.action-buttons .ant-btn-link.ant-btn-dangerous {
  color: @red-600;
}

.action-buttons .ant-btn-link.ant-btn-dangerous:hover {
  color: @red-700;
  background-color: fade(@red-600, 10%);
}

/* Popover 样式 */
:deep(.ant-popover-inner) {
  border-radius: @border-radius-lg;
  box-shadow: @shadow-md-10;
}

.popover-options {
  max-width: 350px;
}

.popover-title {
  color: @slate-800;
  margin-bottom: 12px;
  font-size: @font-size-base;
  font-weight: 600;
  padding-bottom: 8px;
  border-bottom: 1px solid @slate-200;
}

.popover-option-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.popover-option-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border-radius: @border-radius-base;
  font-size: @font-size-2sm;
  background: @slate-50;
  border: 1px solid @slate-200;
  transition: all 0.2s ease;
}

.popover-option-item:hover {
  background: @blue-50;
  border-color: @blue-200;
}

.popover-option-item.is-default {
  background: @green-50;
  border-color: @green-200;
}

.popover-option-item .option-label {
  color: @slate-800;
  min-width: 80px;
  font-weight: 500;
}

.popover-option-item .option-code {
  background: @bg-color-container;
  border: 1px solid @slate-300;
  padding: 3px 6px;
  border-radius: 4px;
  font-family: 'SFMono-Regular', 'Monaco', 'Consolas', monospace;
  font-size: @font-size-2xs;
  color: @blue-500;
  font-weight: 500;
  flex: 1;
}

.popover-option-item.is-default .option-code {
  border-color: @green-600;
  color: @green-700;
}

.default-tag {
  background: @green-600;
  color: white;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: @font-size-2xs;
  font-weight: 600;
  letter-spacing: 0.3px;
}

.no-options-text {
  color: @gray-400;
  font-size: @font-size-2sm;
  font-style: italic;
  text-align: center;
  padding: 12px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  :deep(.ant-table-thead > tr > th) {
    padding: 16px 12px !important;
  font-size: @font-size-base;
  }
  
  :deep(.ant-table-tbody > tr > td) {
    padding: 16px 12px !important;
  }
  
  .property-title {
  font-size: @font-size-2lg;
  }
  
  .detail-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }
  
  .detail-label {
    min-width: auto;
  }
}
</style>
