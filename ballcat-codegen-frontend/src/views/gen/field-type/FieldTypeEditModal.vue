<template>
  <a-modal
    v-model:open="visible"
    :title="title"
    :mask-closable="false"
    :confirm-loading="submitLoading"
    :width="720"
    :ok-text="isUpdate ? '更新' : '创建'"
    cancel-text="取消"
    @ok="handleSubmit"
    @cancel="handleClose"
  >
    <template #title>
      <div class="flex items-center">
        <a-avatar :size="40" class="mr-3" style="background-color: #1890ff;">
          <template #icon>
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <ellipse cx="12" cy="5" rx="9" ry="3"></ellipse>
              <path d="M3 5V19A9 3 0 0 0 21 19V5"></path>
              <path d="M3 12A9 3 0 0 0 21 12"></path>
            </svg>
          </template>
        </a-avatar>
        <div>
          <div class="text-lg font-semibold">{{ title }}</div>
          <div class="text-sm text-gray-500">{{ isUpdate ? '修改已有的数据类型映射配置' : '创建新的数据类型映射配置' }}</div>
        </div>
      </div>
    </template>

    <a-form layout="vertical" :model="modelRef">
      <a-form-item v-if="isUpdate" style="display: none">
        <a-input v-model:value="modelRef.id" />
      </a-form-item>

      <!-- 基础信息卡片 -->
      <a-card size="small" title="基础信息" class="mb-4">
        <template #extra>
          <a-tag color="blue">必填</a-tag>
        </template>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item 
              label="数据库类型" 
              v-bind="validateInfos.dbType"
              :required="true"
            >
              <a-select 
                v-model:value="modelRef.dbType" 
                placeholder="请选择数据库类型"
                size="large"
                show-search
              >
                <a-select-option v-for="(value, key) in DbType" :key="key" :value="value">
                  <a-tag color="geekblue">{{ key }}</a-tag>
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item
              label="模板组标识"
              v-bind="validateInfos.groupKey"
              :required="true"
            >
              <a-input 
                v-model:value="modelRef.groupKey" 
                placeholder="请输入模板组标识" 
                size="large"
              >
                <template #prefix>
                  <span style="color: #1890ff">🏷️</span>
                </template>
              </a-input>
              <template #extra>
                <a-typography-text type="secondary" :style="{ fontSize: '12px' }">
                  用户自定义的模板组标识
                </a-typography-text>
              </template>
            </a-form-item>
          </a-col>
        </a-row>
      </a-card>

      <!-- 类型映射配置卡片 -->
      <a-card size="small" title="类型映射配置" class="mb-4">
        <template #extra>
          <a-tag color="orange">映射</a-tag>
        </template>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item 
              label="数据库字段类型" 
              v-bind="validateInfos.columnKey"
              :required="true"
            >
              <a-input 
                v-model:value="modelRef.columnKey" 
                placeholder="如：VARCHAR, INT, DATETIME" 
                size="large"
              >
                <template #prefix>
                  <span style="color: #52c41a">🗄️</span>
                </template>
              </a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item 
              label="Java属性类型" 
              v-bind="validateInfos.columnValue"
              :required="true"
            >
              <a-input 
                v-model:value="modelRef.columnValue" 
                placeholder="如：String, Integer, LocalDateTime" 
                size="large"
              >
                <template #prefix>
                  <span style="color: #fa8c16">☕</span>
                </template>
              </a-input>
            </a-form-item>
          </a-col>
        </a-row>
        <a-alert
          message="类型映射说明"
          description="数据库字段类型将映射为对应的Java属性类型，请确保类型兼容性"
          type="info"
          show-icon
          class="mt-2"
        />
      </a-card>

      <!-- 包路径配置卡片 -->
      <a-card size="small" title="包路径配置">
        <template #extra>
          <a-tag color="purple">可选</a-tag>
        </template>
        <a-form-item 
          label="属性包路径" 
          v-bind="validateInfos.packageName"
        >
          <a-input 
            v-model:value="modelRef.packageName" 
            placeholder="如：java.lang.String, java.time.LocalDateTime" 
            size="large"
          >
            <template #prefix>
              <span style="color: #722ed1">📦</span>
            </template>
          </a-input>
          <template #extra>
            <a-typography-text type="secondary" :style="{ fontSize: '12px' }">
              完整的包路径+类名，留空则使用默认包路径
            </a-typography-text>
          </template>
        </a-form-item>
      </a-card>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
import { computed, reactive, ref, h } from 'vue'
import { addFieldType, updateFieldType } from '@/api/gen/field-type'
import { copyProperties } from '@/utils/bean-util'
import { usePopup } from '@/hooks/popup'
// 类型导入
import type { FieldType } from '@/api/gen/field-type/types'
import type { FieldTypeEditModalInstance } from './types'
import { useForm } from 'ant-design-vue/es/form'
import { doRequest } from '@/utils/axios/request'
import { DbType } from '@/api/gen/field-type/types'

// 定义事件
const emits = defineEmits<{
  // 提交完成事件
  (e: 'done'): void
}>()

const title = ref('')

// 是否是更新
const isUpdate = ref(false)

// 弹窗显示隐藏
const { visible, handleOpen, handleClose } = usePopup()

// 表单的布局设置
const labelCol = {
  xs: { span: 12 },
  sm: { span: 24 },
  lg: { span: 5 }
}
const wrapperCol = {
  xs: { span: 12 },
  sm: { span: 24 },
  lg: { span: 18 }
}

// 表单绑定数据
const modelRef = reactive<FieldType>({
  id: undefined,
  groupKey: '',
  columnKey: '',
  dbType: '',
  columnValue: '',
  packageName: ''
})

// 表单校验规则
const rulesRef = computed(() => {
  return {
    groupKey: [{required: true, message: '请输入模板组标识！'}],
    columnKey: [{required: true, message: '请输入DB属性类型!'}],
    dbType: [{required: true, message: '请选择数据库类型!'}],
    columnValue: [{required: true, message: '请输入Java对应类型!'}],
    packageName: [{validator: validRule}]
  }
})

const validRule = (rule: any, value: string) => {
  if (value) {
    if (!/^[a-zA-Z.]+$/.test(value)) {
      return Promise.reject('只能输入字母和点号')
    }
  }
  return Promise.resolve()
}

// 提交按钮的 loading 状态控制
const submitLoading = ref<boolean>(false)

const { validate, validateInfos, resetFields } = useForm(modelRef, rulesRef)

/* 表单提交 */
function handleSubmit() {
  validate().then(() => {
    const reqFunction = isUpdate.value ? updateFieldType : addFieldType
    if (!isUpdate.value) {
      delete modelRef.id
    }
    doRequest({
      request: reqFunction(modelRef),
      successMessage: '保存成功！',
      onSuccess() {
        emits('done')
        handleClose()
      }
    })
  })
}

const add = () => {
  isUpdate.value = false
  title.value = '新建数据类型'
  resetFields()
  handleOpen()
}
const update = (record: FieldType) => {
  isUpdate.value = true
  title.value = '修改数据类型'
  resetFields()
  handleOpen()
  copyProperties(modelRef, record)
}

defineExpose<FieldTypeEditModalInstance>({
  add,
  update
})
</script>

<style scoped lang="less">
/* 简化样式，主要使用 Ant Design 原生样式 */
.mb-4 {
  margin-bottom: @spacing-xl;
}

.mt-2 {
  margin-top: @spacing-md;
}
</style>
