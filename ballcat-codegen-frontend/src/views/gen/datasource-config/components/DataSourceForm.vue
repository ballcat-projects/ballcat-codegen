<template>
  <div class="p-6 space-y-4">
    <a-form layout="vertical" :model="modelRef">
      <!-- 基础信息 -->
      <div class="space-y-3">
        <a-form-item 
          label="数据源名称" 
          v-bind="validateInfos.title"
          :required="true"
        >
          <a-input 
            v-model:value="modelRef.title" 
            placeholder="请输入数据源名称"
          />
        </a-form-item>

        <a-form-item 
          label="数据源键" 
          v-bind="validateInfos.dsKey"
          :required="true"
        >
          <a-input 
            v-model:value="modelRef.dsKey" 
            placeholder="数据源唯一标识"
          >
            <template #prefix>
              <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none"
                stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
                class="text-gray-400">
                <path d="M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A1.994 1.994 0 013 12V7a4 4 0 014-4z"></path>
              </svg>
            </template>
          </a-input>
        </a-form-item>

        <a-form-item 
          label="连接地址" 
          v-bind="validateInfos.url"
          :required="true"
        >
          <a-textarea 
            v-model:value="modelRef.url" 
            placeholder="jdbc:mysql://localhost:3306/database" 
            :rows="3"
            :auto-size="{ minRows: 2, maxRows: 4 }" 
          />
          <template #extra v-if="urlValidationResult?.valid">
            <div class="text-xs mt-1 flex items-center gap-2">
              <span class="text-green-600 flex items-center">
                <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 24 24" fill="none"
                  stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="mr-1">
                  <polyline points="20 6 9 17 4 12"></polyline>
                </svg>
                {{ urlValidationResult.summary }}
              </span>
              <LockOutlined v-if="hasSsl(modelRef.url)" class="text-green-600 text-xs" />
            </div>
          </template>
        </a-form-item>

        <a-form-item 
          label="用户名" 
          v-bind="validateInfos.username"
          :required="true"
        >
          <a-input 
            v-model:value="modelRef.username" 
            placeholder="请输入用户名"
          >
            <template #prefix>
              <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none"
                stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
                class="text-gray-400">
                <path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"></path>
                <circle cx="12" cy="7" r="4"></circle>
              </svg>
            </template>
          </a-input>
        </a-form-item>

        <a-form-item 
          label="密码" 
          v-bind="validateInfos.pass"
          :required="formMode === 'add'"
        >
          <a-input-password 
            v-model:value="modelRef.pass" 
            :placeholder="formMode === 'add' ? '请输入密码' : '不修改请留空'"
          >
            <template #prefix>
              <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none"
                stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
                class="text-gray-400">
                <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
                <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
              </svg>
            </template>
          </a-input-password>
        </a-form-item>
      </div>

      <!-- 操作按钮 -->
      <div class="pt-4 border-t border-gray-200 space-y-2">
        <a-button 
          type="primary" 
          block
          :loading="submitLoading"
          @click="handleSubmit"
        >
          <template #icon>
            <SaveOutlined />
          </template>
          {{ formMode === 'add' ? '保存' : '保存修改' }}
        </a-button>
        <a-button 
          block
          @click="handleCancel"
        >
          <template #icon>
            <CloseOutlined />
          </template>
          取消
        </a-button>
      </div>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import { useForm } from 'ant-design-vue/es/form'
import type { DataSourceConfig } from '@/api/gen/datasource-config/types'
import type { DataSourceFormInstance } from '../types'
import { validateJdbcUrl, hasSsl, type JdbcValidationResult } from '@/utils/jdbc-url'
import { LockOutlined, SaveOutlined, CloseOutlined } from '@ant-design/icons-vue'
import { addDatasourceConfig, updateDatasourceConfig } from '@/api/gen/datasource-config'
import { doRequest } from '@/utils/axios/request'

const emit = defineEmits<{
  (e: 'done'): void
  (e: 'cancel'): void
}>()

// 表单模式
const formMode = ref<'add' | 'edit'>('add')

// 表单数据模型
const modelRef = reactive<DataSourceConfig>({
  id: undefined,
  title: '',
  dsKey: '',
  url: '',
  username: '',
  pass: ''
})

// 自定义 JDBC URL 验证器
const validateJdbcUrlRule = async (_rule: any, value: string) => {
  if (!value) {
    return Promise.reject('请输入连接地址')
  }
  const result = validateJdbcUrl(value)
  if (!result.valid) {
    return Promise.reject(result.error || 'JDBC URL 格式不正确')
  }
  return Promise.resolve()
}

// 验证规则
const rulesRef = computed(() => ({
  title: [
    { required: true, message: '请输入数据源名称' },
    { min: 2, max: 50, message: '长度在 1 到 50 个字符' }
  ],
  dsKey: [
    { required: true, message: '请输入数据源键' },
    { min: 1, max: 50, message: '长度在 1 到 50 个字符' }
  ],
  url: [
    { required: true, message: '请输入连接地址' },
    { validator: validateJdbcUrlRule, trigger: 'blur' }
  ],
  username: [
    { required: true, message: '请输入用户名' },
    { min: 1, max: 100, message: '长度在 1 到 100 个字符' }
  ],
  pass: [
    { required: formMode.value === 'add', message: '请输入密码' }
  ]
}))

// 使用 useForm Hook
const { validate, validateInfos, resetFields } = useForm(modelRef, rulesRef)

// 提交加载状态
const submitLoading = ref(false)

// URL 验证结果（用于实时显示摘要或错误）
const urlValidationResult = ref<JdbcValidationResult | null>(null)

// 监听 URL 变化，实时显示验证结果
watch(() => modelRef.url, (newUrl) => {
  if (newUrl) {
    urlValidationResult.value = validateJdbcUrl(newUrl)
  } else {
    urlValidationResult.value = null
  }
}, { immediate: true })

// 提交表单
function handleSubmit() {
  validate().then(() => {
    submitLoading.value = true
    
    if (formMode.value === 'add') {
      // 添加新数据源
      doRequest<void>({
        request: addDatasourceConfig({
          title: modelRef.title,
          dsKey: modelRef.dsKey,
          url: modelRef.url,
          username: modelRef.username,
          pass: modelRef.pass || ''
        }),
        successMessage: '添加成功！',
        onSuccess() {
          emit('done')
          resetFields()
        },
        onFinally: () => (submitLoading.value = false)
      })
    } else {
      // 更新数据源
      const updateData: any = {
        id: modelRef.id,
        title: modelRef.title,
        dsKey: modelRef.dsKey,
        url: modelRef.url,
        username: modelRef.username
      }
      
      // 只有输入了密码才更新密码
      if (modelRef.pass) {
        updateData.pass = modelRef.pass
      }
      
      doRequest<void>({
        request: updateDatasourceConfig(updateData),
        successMessage: '更新成功！',
        onSuccess() {
          emit('done')
          resetFields()
        },
        onFinally: () => (submitLoading.value = false)
      })
    }
  }).catch(err => {
    console.error('表单验证失败:', err)
  })
}

// 取消操作
function handleCancel() {
  emit('cancel')
}

// 暴露给父组件的方法
defineExpose<DataSourceFormInstance>({
  add() {
    formMode.value = 'add'
    resetFields()
    modelRef.id = undefined
    modelRef.title = ''
    modelRef.dsKey = ''
    modelRef.url = ''
    modelRef.username = ''
    modelRef.pass = ''
    urlValidationResult.value = null
  },
  edit(record: DataSourceConfig) {
    formMode.value = 'edit'
    resetFields()
    modelRef.id = record.id
    modelRef.title = record.title || ''
    modelRef.dsKey = record.dsKey || ''
    modelRef.url = record.url || ''
    modelRef.username = record.username || ''
    modelRef.pass = '' // 密码不显示，留空表示不修改
    urlValidationResult.value = validateJdbcUrl(modelRef.url)
  }
})
</script>

<style scoped>
</style>