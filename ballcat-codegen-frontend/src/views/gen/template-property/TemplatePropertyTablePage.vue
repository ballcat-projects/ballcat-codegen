<template>
  <div>
    <!-- 操作按钮区域 -->
    <div class="table-list-toolbar">
      <add-button @click="handleCreate()" />
    </div>

    <!--数据表格区域-->
    <a-table
      ref="table"
      size="small"
      row-key="id"
      :columns="columns"
      :data-source="dataSource"
      :pagination="pagination"
      :loading="loading"
      :scroll="{ x: 720 }"
      :row-class-name="(_record: any, index: number) => index % 2 === 1 ? 'table-row-alternate' : ''"
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
  (e: 'create'): void
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

function handleCreate() {
  emits('create')
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
/* 表格基础样式 */
:deep(.ant-table-thead > tr > th) {
  background: #fafafa;
  border-bottom: 1px solid #f0f0f0;
  padding: 8px 12px !important;
}

:deep(.ant-table-tbody > tr.table-row-alternate) {
  background-color: #fff !important;
}

:deep(.ant-table-tbody > tr > td) {
  border-bottom: 1px solid #f0f0f0;
  padding: 12px !important;
  vertical-align: top;
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

/* 属性配置区域样式 */
.property-section {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.property-main {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.property-title {
  margin: 0;
  font-size: 14px;
  font-weight: 500;
  color: #262626;
  line-height: 1.5;
}

.property-details {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

/* 配置详情区域样式 */
.config-section {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.config-primary {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.config-secondary {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.config-tertiary {
  border-top: 1px solid #f0f0f0;
  padding-top: 8px;
  margin-top: 4px;
}

/* 通用详情项样式 */
.detail-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
}

.detail-label {
  color: #666;
  white-space: nowrap;
}

.detail-code {
  background: #f5f5f5;
  border: 1px solid #d9d9d9;
  padding: 2px 4px;
  border-radius: 2px;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 11px;
  color: #262626;
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.detail-code.expression {
  color: #1890ff;
  background: #f0f7ff;
  border-color: #bae7ff;
  max-width: 300px;
}

.detail-value {
  font-size: 12px;
  color: #262626;
}

.detail-text {
  font-size: 12px;
  color: #666;
  max-width: 300px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.option-name {
  font-size: 11px;
  color: #52c41a;
  font-style: italic;
}

/* Popover 样式 */
.popover-options {
  max-width: 320px;
}

.popover-title {
  color: #262626;
  margin-bottom: 12px;
  font-size: 13px;
  font-weight: 500;
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 8px;
}

.popover-option-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.popover-option-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border-radius: 6px;
  font-size: 12px;
  background: #fafafa;
  border: 1px solid #f0f0f0;
  transition: all 0.2s ease;
}

.popover-option-item:hover {
  background: #f0f7ff;
  border-color: #bae7ff;
}

.popover-option-item.is-default {
  background: #f6ffed;
  border: 1px solid #b7eb8f;
}

.popover-option-item.is-default:hover {
  background: #ecf5ff;
  border-color: #87d068;
}

.popover-option-item .option-label {
  color: #262626;
  flex-shrink: 0;
  min-width: 60px;
  font-weight: 500;
}

.popover-option-item .option-separator {
  color: #8c8c8c;
  flex-shrink: 0;
}

.popover-option-item .option-code {
  background: #fff;
  border: 1px solid #d9d9d9;
  padding: 2px 6px;
  border-radius: 4px;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 11px;
  color: #1890ff;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.popover-option-item.is-default .option-code {
  background: #fff;
  border-color: #52c41a;
  color: #389e0d;
}

.default-tag {
  background: #52c41a;
  color: #fff;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 10px;
  flex-shrink: 0;
  font-weight: 500;
}

.popover-no-options {
  max-width: 200px;
  text-align: center;
}

.no-options-text {
  color: #8c8c8c;
  font-size: 12px;
  margin-top: 8px;
  font-style: italic;
}

.tooltip-code {
  color: #fff;
  background: transparent;
  margin: 0;
  padding: 8px;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 12px;
  line-height: 1.4;
  white-space: pre-wrap;
  word-break: break-all;
  max-width: 400px;
}

/* 备注区域样式 */
.remarks-section {
  padding: 8px 0;
}

.remarks-text {
  font-size: 12px;
  color: #666;
  line-height: 1.5;
  word-break: break-word;
}

.remarks-empty {
  font-size: 12px;
  color: #ccc;
}

/* 徽章样式 */
.required-badge {
  background: #ff4d4f;
  color: #fff;
  padding: 1px 4px;
  border-radius: 2px;
  font-size: 10px;
  white-space: nowrap;
}

/* 配置信息简化样式 */
.config-info {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
}

.config-type {
  background: #52c41a;
  color: #fff;
  padding: 2px 6px;
  border-radius: 2px;
  font-size: 11px;
  white-space: nowrap;
}

.config-type.computed {
  background: #722ed1;
}

.component-text {
  color: #1890ff;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
}

.component-text:hover {
  color: #40a9ff;
}

.options-count {
  color: #fa8c16;
  font-size: 10px;
  margin-left: 4px;
}

.engine-text {
  color: #cf1322;
  font-size: 12px;
}



/* 操作按钮样式 */
.action-buttons {
  display: flex;
  gap: 0;
  justify-content: center;
  align-items: center;
}

.action-buttons .ant-btn {
  padding: 4px 8px;
  height: auto;
  border: none;
  border-radius: 4px;
  font-size: 12px;
  line-height: 1.2;
}

.action-buttons .ant-btn:hover {
  background-color: #f0f7ff;
}

.action-buttons .ant-btn.ant-btn-dangerous:hover {
  background-color: #fff1f0;
}
</style>
