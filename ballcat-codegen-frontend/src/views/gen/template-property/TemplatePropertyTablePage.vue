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

<style scoped>
/* 表格整体样式 */
:deep(.ant-table) {
  background: white;
  border-radius: 0;
  box-shadow: none;
  border: none;
}

:deep(.ant-table-thead > tr > th) {
  background: #f8fafc;
  border-bottom: 2px solid #e2e8f0;
  padding: 20px 16px !important;
  font-weight: 600;
  font-size: 15px;
  color: #1e293b;
  line-height: 1.4;
}

:deep(.ant-table-tbody > tr > td) {
  border-bottom: 1px solid #f1f5f9;
  padding: 20px 16px !important;
  vertical-align: top;
  font-size: 14px;
}

:deep(.ant-table-tbody > tr:hover > td) {
  background: #f8fafc !important;
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
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  line-height: 1.4;
}

.required-badge {
  background: #dc2626;
  color: white;
  padding: 3px 8px;
  border-radius: 4px;
  font-size: 11px;
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
  background: #059669;
  color: white;
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.config-type.computed {
  background: #7c3aed;
}

.component-text {
  color: #3b82f6;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
}

.component-text:hover {
  color: #2563eb;
  text-decoration: underline;
}

.engine-text {
  color: #dc2626;
  font-size: 14px;
  font-weight: 500;
}

.options-count {
  color: #64748b;
  font-size: 12px;
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
  color: #64748b;
  font-weight: 500;
  min-width: 70px;
}

.detail-code {
  background: #f1f5f9;
  border: 1px solid #e2e8f0;
  padding: 4px 8px;
  border-radius: 6px;
  font-family: 'SFMono-Regular', 'Monaco', 'Consolas', monospace;
  font-size: 12px;
  color: #3b82f6;
  font-weight: 500;
}

.detail-code.expression {
  color: #1d4ed8;
  background: #eff6ff;
  border-color: #bfdbfe;
}

.detail-value {
  font-size: 13px;
  color: #1e293b;
  font-weight: 500;
}

.option-name {
  font-size: 11px;
  color: #16a34a;
  font-style: italic;
  font-weight: 500;
}

/* 备注区域 */
.remarks-section {
  padding: 4px 0;
}

.remarks-text {
  font-size: 13px;
  color: #64748b;
  line-height: 1.5;
}

.remarks-empty {
  font-size: 13px;
  color: #cbd5e1;
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
  border-radius: 6px;
  font-size: 13px;
  font-weight: 500;
  transition: all 0.2s ease;
}

.action-buttons .ant-btn-link {
  color: #3b82f6;
}

.action-buttons .ant-btn-link:hover {
  color: #2563eb;
  background-color: rgba(59, 130, 246, 0.1);
}

.action-buttons .ant-btn-link.ant-btn-dangerous {
  color: #dc2626;
}

.action-buttons .ant-btn-link.ant-btn-dangerous:hover {
  color: #b91c1c;
  background-color: rgba(220, 38, 38, 0.1);
}

/* Popover 样式 */
:deep(.ant-popover-inner) {
  border-radius: 8px;
  box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.1);
}

.popover-options {
  max-width: 350px;
}

.popover-title {
  color: #1e293b;
  margin-bottom: 12px;
  font-size: 14px;
  font-weight: 600;
  padding-bottom: 8px;
  border-bottom: 1px solid #e2e8f0;
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
  border-radius: 6px;
  font-size: 13px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  transition: all 0.2s ease;
}

.popover-option-item:hover {
  background: #eff6ff;
  border-color: #bfdbfe;
}

.popover-option-item.is-default {
  background: #f0fdf4;
  border-color: #bbf7d0;
}

.popover-option-item .option-label {
  color: #1e293b;
  min-width: 80px;
  font-weight: 500;
}

.popover-option-item .option-code {
  background: white;
  border: 1px solid #cbd5e1;
  padding: 3px 6px;
  border-radius: 4px;
  font-family: 'SFMono-Regular', 'Monaco', 'Consolas', monospace;
  font-size: 11px;
  color: #3b82f6;
  font-weight: 500;
  flex: 1;
}

.popover-option-item.is-default .option-code {
  border-color: #16a34a;
  color: #15803d;
}

.default-tag {
  background: #16a34a;
  color: white;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 10px;
  font-weight: 600;
  letter-spacing: 0.3px;
}

.no-options-text {
  color: #9ca3af;
  font-size: 13px;
  font-style: italic;
  text-align: center;
  padding: 12px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  :deep(.ant-table-thead > tr > th) {
    padding: 16px 12px !important;
    font-size: 14px;
  }
  
  :deep(.ant-table-tbody > tr > td) {
    padding: 16px 12px !important;
  }
  
  .property-title {
    font-size: 15px;
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
