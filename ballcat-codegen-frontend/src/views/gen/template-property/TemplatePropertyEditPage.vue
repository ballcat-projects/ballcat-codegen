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

<style lang="less" scoped>
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
  background: @bg-color-container;
  border-radius: @border-radius-xl;
  padding: @spacing-xxl;
  box-shadow: @shadow-xs-05;
  border: 1px solid @slate-200;
}

/* 表单项样式 */
:deep(.ant-form-item) {
  margin-bottom: 24px;
}

:deep(.ant-form-item-label > label) {
  color: @slate-800 !important;
  font-weight: @font-weight-semibold !important;
  font-size: @font-size-2lg !important;
  line-height: @line-height-base;
}

:deep(.ant-form-item-label > label.ant-form-item-required:not(.ant-form-item-required-mark-optional)::before) {
  color: @red-600 !important;
  font-size: @font-size-2lg;
  font-weight: @font-weight-semibold;
}

/* 输入框样式 */
:deep(.ant-input) {
  border-color: @gray-300;
  border-radius: @border-radius-lg;
  padding: @spacing-sm @spacing-md;
  font-size: @font-size-base;
  line-height: @line-height-base;
  transition: @animation-base;
  min-height: auto;
}

:deep(.ant-input:hover) {
  border-color: @gray-400;
}

:deep(.ant-input:focus) {
  border-color: @blue-500;
  box-shadow: @shadow-lg-ring-blue;
}

:deep(.ant-input::placeholder) {
  color: @gray-400;
  font-size: @font-size-base;
}

/* 文本域样式 */
:deep(.ant-textarea) {
  border-color: @gray-300;
  border-radius: @border-radius-lg;
  padding: @spacing-sm @spacing-md;
  font-size: @font-size-base;
  line-height: @line-height-base;
  transition: @animation-base;
  resize: vertical;
  min-height: 80px;
}

:deep(.ant-textarea:hover) {
  border-color: @gray-400;
}

:deep(.ant-textarea:focus) {
  border-color: @blue-500;
  box-shadow: @shadow-lg-ring-blue;
}

/* 数字输入框样式 */
:deep(.ant-input-number) {
  border-color: @gray-300;
  border-radius: @border-radius-lg;
  width: 100%;
  font-size: @font-size-base;
  transition: @animation-base;
}

:deep(.ant-input-number:hover) {
  border-color: @gray-400;
}

:deep(.ant-input-number-focused) {
  border-color: @blue-500;
  box-shadow: @shadow-lg-ring-blue;
}

:deep(.ant-input-number .ant-input-number-input) {
  padding: @spacing-sm @spacing-md;
  font-size: @font-size-base;
  line-height: @line-height-base;
}

/* 单选按钮组样式 */
:deep(.ant-radio-group) {
  display: inline-flex;
}

:deep(.ant-radio-button-wrapper) {
  border-color: @gray-300;
  color: @slate-500;
  font-weight: @font-weight-medium;
  font-size: @font-size-base;
  transition: @animation-base;
  border-radius: 0;
}

:deep(.ant-radio-button-wrapper:hover) {
  color: @blue-500;
  border-color: @blue-500;
  z-index: 1;
}

:deep(.ant-radio-button-wrapper-checked) {
  background: @blue-500;
  border-color: @blue-500;
  color: @gray-1;
  box-shadow: 0 2px 4px 0 rgba(59, 130, 246, 0.2);
  z-index: 2;
}

:deep(.ant-radio-button-wrapper-checked:hover) {
  background: @blue-600;
  border-color: @blue-600;
}

:deep(.ant-radio-button-wrapper:first-child) {
  border-radius: @border-radius-lg 0 0 @border-radius-lg;
}

:deep(.ant-radio-button-wrapper:last-child) {
  border-radius: 0 @border-radius-lg @border-radius-lg 0;
}

:deep(.ant-radio-button-wrapper:only-child) {
  border-radius: @border-radius-lg;
}

/* 单选框样式 */
:deep(.ant-radio-wrapper) {
  color: @slate-500;
  font-weight: @font-weight-medium;
  font-size: @font-size-base;
  margin-right: @spacing-lg;
  line-height: @line-height-base;
}

:deep(.ant-radio-wrapper:hover .ant-radio-inner) {
  border-color: @blue-500;
}

:deep(.ant-radio-checked .ant-radio-inner) {
  background: @blue-500;
  border-color: @blue-500;
}

:deep(.ant-radio-checked::after) {
  border-color: @blue-500;
}

/* 开关样式 */
:deep(.ant-switch) {
  background: @slate-200;
}

:deep(.ant-switch-checked) {
  background: @blue-500;
}

:deep(.ant-switch:hover:not(.ant-switch-disabled)) {
  background: @slate-300;
}

:deep(.ant-switch-checked:hover:not(.ant-switch-disabled)) {
  background: @blue-600;
}

/* 按钮样式 */
:deep(.ant-btn) {
  border-radius: @border-radius-lg;
  height: 42px;
  padding: 0 @spacing-xl;
  font-weight: @font-weight-semibold;
  font-size: @font-size-base;
  line-height: @line-height-base;
  transition: @animation-base;
}

:deep(.ant-btn-primary) {
  background: @blue-500;
  border-color: @blue-500;
  box-shadow: 0 2px 4px 0 rgba(59, 130, 246, 0.2);
}

:deep(.ant-btn-primary:hover) {
  background: @blue-600;
  border-color: @blue-600;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px 0 rgba(59, 130, 246, 0.3);
}

:deep(.ant-btn-default) {
  background: @gray-1;
  border-color: @gray-300;
  color: @slate-500;
  font-weight: @font-weight-medium;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
}

:deep(.ant-btn-default:hover) {
  background: @slate-50;
  border-color: @gray-400;
  color: @gray-700;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px 0 rgba(0, 0, 0, 0.1);
}

:deep(.ant-btn-dashed) {
  border-color: @gray-400;
  color: @slate-500;
  border-style: dashed;
  background: @slate-50;
  border-radius: @border-radius-lg;
  padding: 10px @spacing-lg;
  height: auto;
  font-weight: @font-weight-medium;
  font-size: @font-size-base;
  transition: @animation-base;
}

:deep(.ant-btn-dashed:hover) {
  border-color: @blue-500;
  color: @blue-500;
  background: @gray-1;
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
  color: @red-600;
  cursor: pointer;
  font-size: 16px;
  padding: 4px;
  border-radius: 4px;
  transition: @animation-base;
}

:deep(.anticon-minus-circle:hover) {
  color: @red-700;
  background: fade(@red-600, 10%);
}

/* 添加按钮样式 */
:deep(.ant-btn-dashed .anticon) {
  margin-right: 8px;
}

/* 表单底部按钮组样式 */
:deep(.ant-form-item:last-child) {
  margin-bottom: 0;
  margin-top: @spacing-xxl;
}

:deep(.ant-form-item:last-child .ant-form-item-control-input-content) {
  display: flex;
  justify-content: center;
}

:deep(.ant-space-horizontal) {
  gap: @spacing-lg !important;
}

/* 加载状态样式 */
:deep(.ant-spin .ant-spin-dot) {
  font-size: 20px;
}

:deep(.ant-spin .ant-spin-dot-item) {
  background: @blue-500;
}

/* 验证错误样式 */
:deep(.ant-form-item-has-error .ant-input) {
  border-color: @red-600;
}

:deep(.ant-form-item-has-error .ant-input:hover) {
  border-color: @red-600;
}

:deep(.ant-form-item-has-error .ant-input:focus) {
  border-color: @red-600;
  box-shadow: 0 0 0 3px fade(@red-600, 10%);
}

:deep(.ant-form-item-explain-error) {
  color: @red-600;
  font-size: @font-size-2sm;
  font-weight: @font-weight-medium;
  margin-top: 6px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .ant-form {
  padding: @spacing-xl @spacing-lg;
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
  gap: @spacing-sm;
  }

  :deep(.ant-radio-button-wrapper) {
    text-align: center;
  border-radius: @border-radius-lg !important;
  }

  :deep(.ant-space-horizontal) {
    flex-direction: column;
  gap: @spacing-md !important;
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
