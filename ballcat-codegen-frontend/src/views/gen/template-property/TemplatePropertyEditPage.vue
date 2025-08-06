<template>
  <a-spin :spinning="loading">
    <a-form
      ref="formRef"
      :label-col="{ span: 6 }"
      :wrapper-col="{ span: 12 }"
      :model="modelRef"
      :loading="loading"
      @finish="submitForm"
    >
      <a-form-item v-if="isUpdate" name="id" style="display: none">
        <a-input v-model:value="modelRef.id" />
      </a-form-item>
      <a-form-item name="groupKey" style="display: none">
        <a-input v-model:value="modelRef.groupKey" />
      </a-form-item>
      <a-form-item label="属性标题" name="title">
        <a-input v-model:value="modelRef.title" placeholder="请输入" />
      </a-form-item>
      <a-form-item label="属性键" name="propKey">
        <a-input v-model:value="modelRef.propKey" placeholder="请输入" />
      </a-form-item>
      <a-form-item label="属性类型" name="propType">
        <a-radio-group v-model:value="modelRef.propType">
          <a-radio-button :value="1">配置属性</a-radio-button>
          <a-radio-button :value="2">计算属性</a-radio-button>
        </a-radio-group>
      </a-form-item>

      <template v-if="modelRef.propType == PropType.CONFIG">
        <a-form-item label="默认值" name="defaultValue">
          <a-input v-model:value="modelRef.defaultValue" placeholder="请输入" />
        </a-form-item>
        <a-form-item label="属性控件" name="componentType">
          <a-radio-group v-model:value="modelRef.componentType">
            <a-radio :value="ComponentType.INPUT">Input</a-radio>
            <a-radio :value="ComponentType.INPUT_NUMBER">Input Number</a-radio>
            <a-radio :value="ComponentType.SELECT">Select</a-radio>
            <a-radio :value="ComponentType.RADIO">Radio</a-radio>
          </a-radio-group>
        </a-form-item>
        <a-form-item
          v-if="
          modelRef.componentType === ComponentType.SELECT ||
          modelRef.componentType === ComponentType.RADIO
        "
          label="选项配置"
        >
          <a-space
            v-for="(option, index) in modelRef.componentOptions"
            :key="option.value"
            style="display: flex; margin-bottom: 8px"
            align="baseline"
          >
            <a-form-item
              :name="['componentOptions', index, 'name']"
              :rules="{
              required: true,
              message: '请输入选项的名称'
            }"
            >
              <a-input v-model:value="option.name" placeholder="Name" />
            </a-form-item>
            <a-form-item
              :name="['componentOptions', index, 'value']"
              :rules="{
              required: true,
              message: '请输入选项的值'
            }"
            >
              <a-input v-model:value="option.value" placeholder="Value" />
            </a-form-item>
            <MinusCircleOutlined @click="removeOption(option)" />
          </a-space>
          <a-button type="dashed" block @click="addOption">
            <PlusOutlined />
            添加选项
          </a-button>
        </a-form-item>
      </template>
      <template v-else-if="modelRef.propType == PropType.COMPUTED">
        <a-form-item label="表达式" name="expression">
          <a-input v-model:value="modelRef.expression" placeholder="请输入" />
        </a-form-item>
        <a-form-item label="模板引擎" name="engineType">
          <a-radio-group v-model:value="modelRef.engineType">
            <a-radio :value="1">Velocity</a-radio>
            <a-radio :value="2">Freemarker</a-radio>
          </a-radio-group>
        </a-form-item>
      </template>

      <a-form-item label="排序值" name="orderValue">
        <a-input-number v-model:value="modelRef.orderValue" placeholder="请输入" />
      </a-form-item>
      <a-form-item label="是否必填" name="required">
        <a-switch
          v-model:checked="modelRef.required"
          :checked-value="1"
          :un-checked-value="0"
          checked-children="是"
          un-checked-children="否"
        />
      </a-form-item>
      <a-form-item label="备注信息" name="remarks">
        <a-textarea v-model:value="modelRef.remarks" placeholder="请输入" />
      </a-form-item>
      <a-form-item label=" " :colon="false">
        <a-space>
          <a-button type="primary" html-type="submit" :loading="loading">保存</a-button>
          <a-button @click="handleCancel">取消</a-button>
        </a-space>
      </a-form-item>
    </a-form>
  </a-spin>
</template>

<script setup lang="ts">
import { MinusCircleOutlined, PlusOutlined } from '@ant-design/icons-vue'
import { computed, reactive, ref, toRaw, watch } from 'vue'
import type { FormInstance } from 'ant-design-vue'
import { PropType, type TemplateProperty } from "@/api/gen/template-property/types";
import { ComponentType } from '@/api/gen/template-property/types'
import { copyProperties } from '@/utils/bean-util'
import { addTemplateProperty, updateTemplateProperty } from '@/api/gen/template-property'
import { doRequest } from '@/utils/axios/request'

const emits = defineEmits<{
  (e: 'back-page', reloadTable: boolean): void
}>()

interface Option {
  name: string
  value: string
}

const loading = ref(false)

const formRef = ref<FormInstance>()

const formAction = ref('CREATE')
const isCreate = computed(() => formAction.value === 'CREATE')
const isUpdate = computed(() => formAction.value === 'UPDATE')

const modelRef = reactive<TemplateProperty>({
  id: undefined,
  groupKey: '',
  title: '',
  propKey: '',
  propType: 1,
  expression: '',
  engineType: 1, // 默认使用 Velocity
  defaultValue: '',
  required: 0,
  componentOptions: [],
  componentType: ComponentType.INPUT,
  orderValue: 0,
  remarks: ''
})

// 监听属性类型变化，确保计算属性使用正确的引擎类型
watch(() => modelRef.propType, (newType) => {
  if (newType === PropType.COMPUTED && (modelRef.engineType === 0 || !modelRef.engineType)) {
    modelRef.engineType = 1 // 切换到计算属性时，默认使用 Velocity
  }
})

const removeOption = (item: Option) => {
  const index = modelRef.componentOptions?.indexOf(item)
  if (index && index !== -1) {
    modelRef.componentOptions?.splice(index, 1)
  }
}

const addOption = () => {
  modelRef.componentOptions?.push({
    name: '',
    value: ''
  })
}

function resetForm() {
  formRef.value?.resetFields()
  modelRef.componentOptions = []
}

function submitForm() {
  loading.value = true
  const request = isCreate.value
    ? addTemplateProperty(toRaw(modelRef))
    : updateTemplateProperty(toRaw(modelRef))
  doRequest({
    request: request,
    successMessage: isCreate.value ? '新建成功！' : '修改成功！',
    onSuccess: () => backToPage(true),
    onFinally: () => (loading.value = false)
  })
}

function handleCancel() {
  backToPage(false)
}

function backToPage(reloadTable: boolean) {
  emits('back-page', reloadTable)
}

defineExpose({
  create(groupKey: string) {
    formAction.value = 'CREATE'
    resetForm()
    modelRef.id = undefined
    modelRef.groupKey = groupKey
  },
  update: (record: TemplateProperty) => {
    formAction.value = 'UPDATE'
    resetForm()
    copyProperties(modelRef, record)
  }
})
</script>

<script lang="ts">
export default {
  name: 'TemplatePropertyEditPage'
}
</script>

<style scoped>
/* 编辑页面整体样式 */
.ant-spin-nested-loading {
  height: 100%;
}

.ant-spin-container {
  height: 100%;
}

/* 表单容器样式 */
.ant-form {
  height: 100%;
  background: white;
  border-radius: 12px;
  padding: 32px;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
  border: 1px solid #e2e8f0;
}

/* 表单项样式 */
:deep(.ant-form-item) {
  margin-bottom: 24px;
}

:deep(.ant-form-item-label > label) {
  color: #1e293b !important;
  font-weight: 600 !important;
  font-size: 15px !important;
  line-height: 1.5;
}

:deep(.ant-form-item-label > label.ant-form-item-required:not(.ant-form-item-required-mark-optional)::before) {
  color: #dc2626 !important;
  font-size: 15px;
  font-weight: 600;
}

/* 输入框样式 */
:deep(.ant-input) {
  border-color: #d1d5db;
  border-radius: 8px;
  padding: 8px 12px;
  font-size: 14px;
  line-height: 1.5;
  transition: all 0.2s ease;
  min-height: auto;
}

:deep(.ant-input:hover) {
  border-color: #9ca3af;
}

:deep(.ant-input:focus) {
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

:deep(.ant-input::placeholder) {
  color: #9ca3af;
  font-size: 14px;
}

/* 文本域样式 */
:deep(.ant-textarea) {
  border-color: #d1d5db;
  border-radius: 8px;
  padding: 8px 12px;
  font-size: 14px;
  line-height: 1.5;
  transition: all 0.2s ease;
  resize: vertical;
  min-height: 80px;
}

:deep(.ant-textarea:hover) {
  border-color: #9ca3af;
}

:deep(.ant-textarea:focus) {
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

/* 数字输入框样式 */
:deep(.ant-input-number) {
  border-color: #d1d5db;
  border-radius: 8px;
  width: 100%;
  font-size: 14px;
  transition: all 0.2s ease;
}

:deep(.ant-input-number:hover) {
  border-color: #9ca3af;
}

:deep(.ant-input-number-focused) {
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

:deep(.ant-input-number .ant-input-number-input) {
  padding: 8px 12px;
  font-size: 14px;
  line-height: 1.5;
}

/* 单选按钮组样式 */
:deep(.ant-radio-group) {
  display: inline-flex;
}

:deep(.ant-radio-button-wrapper) {
  border-color: #d1d5db;
  color: #64748b;
  font-weight: 500;
  font-size: 14px;
  transition: all 0.2s ease;
  border-radius: 0;
}

:deep(.ant-radio-button-wrapper:hover) {
  color: #3b82f6;
  border-color: #3b82f6;
  z-index: 1;
}

:deep(.ant-radio-button-wrapper-checked) {
  background: #3b82f6;
  border-color: #3b82f6;
  color: white;
  box-shadow: 0 2px 4px 0 rgba(59, 130, 246, 0.2);
  z-index: 2;
}

:deep(.ant-radio-button-wrapper-checked:hover) {
  background: #2563eb;
  border-color: #2563eb;
}

:deep(.ant-radio-button-wrapper:first-child) {
  border-radius: 6px 0 0 6px;
}

:deep(.ant-radio-button-wrapper:last-child) {
  border-radius: 0 6px 6px 0;
}

:deep(.ant-radio-button-wrapper:only-child) {
  border-radius: 6px;
}

/* 单选框样式 */
:deep(.ant-radio-wrapper) {
  color: #64748b;
  font-weight: 500;
  font-size: 14px;
  margin-right: 16px;
  line-height: 1.5;
}

:deep(.ant-radio-wrapper:hover .ant-radio-inner) {
  border-color: #3b82f6;
}

:deep(.ant-radio-checked .ant-radio-inner) {
  background: #3b82f6;
  border-color: #3b82f6;
}

:deep(.ant-radio-checked::after) {
  border-color: #3b82f6;
}

/* 开关样式 */
:deep(.ant-switch) {
  background: #e2e8f0;
}

:deep(.ant-switch-checked) {
  background: #3b82f6;
}

:deep(.ant-switch:hover:not(.ant-switch-disabled)) {
  background: #cbd5e1;
}

:deep(.ant-switch-checked:hover:not(.ant-switch-disabled)) {
  background: #2563eb;
}

/* 按钮样式 */
:deep(.ant-btn) {
  border-radius: 8px;
  height: 42px;
  padding: 0 24px;
  font-weight: 600;
  font-size: 14px;
  line-height: 1.5;
  transition: all 0.2s ease;
}

:deep(.ant-btn-primary) {
  background: #3b82f6;
  border-color: #3b82f6;
  box-shadow: 0 2px 4px 0 rgba(59, 130, 246, 0.2);
}

:deep(.ant-btn-primary:hover) {
  background: #2563eb;
  border-color: #2563eb;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px 0 rgba(59, 130, 246, 0.3);
}

:deep(.ant-btn-default) {
  background: white;
  border-color: #d1d5db;
  color: #64748b;
  font-weight: 500;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
}

:deep(.ant-btn-default:hover) {
  background: #f8fafc;
  border-color: #9ca3af;
  color: #374151;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px 0 rgba(0, 0, 0, 0.1);
}

:deep(.ant-btn-dashed) {
  border-color: #9ca3af;
  color: #64748b;
  border-style: dashed;
  background: #f8fafc;
  border-radius: 8px;
  padding: 10px 18px;
  height: auto;
  font-weight: 500;
  font-size: 14px;
  transition: all 0.2s ease;
}

:deep(.ant-btn-dashed:hover) {
  border-color: #3b82f6;
  color: #3b82f6;
  background: white;
  transform: translateY(-1px);
  box-shadow: 0 2px 4px 0 rgba(0, 0, 0, 0.1);
}

/* 选项配置区域样式 */
:deep(.ant-space) {
  width: 100%;
}

:deep(.ant-space-item) {
  flex: 1;
}

:deep(.ant-space-item:last-child) {
  flex: none;
}

/* 删除按钮样式 */
:deep(.anticon-minus-circle) {
  color: #dc2626;
  cursor: pointer;
  font-size: 16px;
  padding: 4px;
  border-radius: 4px;
  transition: all 0.2s ease;
}

:deep(.anticon-minus-circle:hover) {
  color: #b91c1c;
  background: rgba(220, 38, 38, 0.1);
}

/* 添加按钮样式 */
:deep(.ant-btn-dashed .anticon) {
  margin-right: 8px;
}

/* 表单底部按钮组样式 */
:deep(.ant-form-item:last-child) {
  margin-bottom: 0;
  margin-top: 32px;
}

:deep(.ant-form-item:last-child .ant-form-item-control-input-content) {
  display: flex;
  justify-content: center;
}

:deep(.ant-space-horizontal) {
  gap: 16px !important;
}

/* 加载状态样式 */
:deep(.ant-spin .ant-spin-dot) {
  font-size: 20px;
}

:deep(.ant-spin .ant-spin-dot-item) {
  background: #3b82f6;
}

/* 验证错误样式 */
:deep(.ant-form-item-has-error .ant-input) {
  border-color: #dc2626;
}

:deep(.ant-form-item-has-error .ant-input:hover) {
  border-color: #dc2626;
}

:deep(.ant-form-item-has-error .ant-input:focus) {
  border-color: #dc2626;
  box-shadow: 0 0 0 3px rgba(220, 38, 38, 0.1);
}

:deep(.ant-form-item-explain-error) {
  color: #dc2626;
  font-size: 13px;
  font-weight: 500;
  margin-top: 6px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .ant-form {
    padding: 24px 16px;
  }

  :deep(.ant-form-item-label) {
    text-align: left !important;
  }

  :deep(.ant-col-6) {
    flex: 0 0 100% !important;
    max-width: 100% !important;
  }

  :deep(.ant-col-12) {
    flex: 0 0 100% !important;
    max-width: 100% !important;
  }

  :deep(.ant-radio-group) {
    flex-direction: column;
    gap: 8px;
  }

  :deep(.ant-radio-button-wrapper) {
    text-align: center;
    border-radius: 8px !important;
  }

  :deep(.ant-space-horizontal) {
    flex-direction: column;
    gap: 12px !important;
  }

  :deep(.ant-btn) {
    width: 100%;
    justify-content: center;
  }
}

@media (max-width: 1200px) {
  :deep(.ant-col-6) {
    flex: 0 0 25% !important;
    max-width: 25% !important;
  }

  :deep(.ant-col-12) {
    flex: 0 0 75% !important;
    max-width: 75% !important;
  }
}
</style>
