<template>
  <div class="p-6">
    <div class="space-y-4">
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">
          数据源名称 <span class="text-red-500">*</span>
        </label>
        <a-input v-model:value="local.title" placeholder="请输入数据源名称" />
      </div>

      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">
          数据源键 <span class="text-red-500">*</span>
        </label>
        <a-input v-model:value="local.dsKey" placeholder="请输入数据源键" />
      </div>

      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">
          连接地址 <span class="text-red-500">*</span>
        </label>
        <a-textarea v-model:value="local.url" placeholder="jdbc:mysql://localhost:3306/database" :rows="3"
          :auto-size="{ minRows: 2, maxRows: 4 }" />
        <div class="text-xs text-gray-500 mt-1 flex items-center gap-2" v-if="local.url">
          <a-tooltip :title="getJdbcTooltip(local.url)">
            <span>{{ getJdbcSummary(local.url) }}</span>
          </a-tooltip>
          <LockOutlined v-if="hasSsl(local.url)" class="text-gray-400" />
        </div>
      </div>

      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">
          用户名 <span class="text-red-500">*</span>
        </label>
        <a-input v-model:value="local.username" placeholder="请输入用户名" />
      </div>

      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">{{ passLabel }}</label>
        <a-input-password v-model:value="local.pass" :placeholder="passPlaceholder" />
      </div>

      <div class="pt-4 border-t border-gray-200 space-y-2">
        <button @click="onSave"
          class="w-full flex items-center justify-center px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
          :disabled="!canSave">
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none"
            stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
            class="w-4 h-4 mr-2">
            <path d="M19 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11l5 5v11a2 2 0 0 1-2 2z"></path>
            <polyline points="17,21 17,13 7,13 7,21"></polyline>
            <polyline points="7,3 7,8 15,8"></polyline>
          </svg>
          {{ saveText }}
        </button>
        <button @click="$emit('cancel')"
          class="w-full flex items-center justify-center px-4 py-2 text-gray-600 border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors">
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none"
            stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
            class="w-4 h-4 mr-2">
            <path d="M18 6 6 18"></path>
            <path d="m6 6 12 12"></path>
          </svg>
          取消
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, watch } from 'vue'
import type { DataSourceConfig } from '@/api/gen/datasource-config/types'
import { getJdbcSummary, getJdbcTooltip, hasSsl } from '@/utils/jdbc-url'
import { LockOutlined } from '@ant-design/icons-vue'

interface Props {
  mode: 'add' | 'edit'
  modelValue: DataSourceConfig
}

const props = defineProps<Props>()
const emit = defineEmits<{
  (e: 'update:modelValue', v: DataSourceConfig): void
  (e: 'save', v: DataSourceConfig): void
  (e: 'cancel'): void
}>()

// 本地副本，避免直接修改父状态
const local = reactive<DataSourceConfig>({
  id: props.modelValue?.id,
  title: props.modelValue?.title || '',
  dsKey: props.modelValue?.dsKey || '',
  url: props.modelValue?.url || '',
  username: props.modelValue?.username || '',
  pass: ''
})

watch(() => props.modelValue, (v) => {
  local.id = v?.id
  local.title = v?.title || ''
  local.dsKey = v?.dsKey || ''
  local.url = v?.url || ''
  local.username = v?.username || ''
  local.pass = ''
}, { deep: true })

watch(local, (v) => emit('update:modelValue', { ...v }), { deep: true })

const canSave = computed(() => {
  if (!local.title || !local.dsKey || !local.url || !local.username) return false
  if (props.mode === 'add') return !!local.pass
  return true
})

const passLabel = computed(() => props.mode === 'add' ? '密码 *' : '密码')
const passPlaceholder = computed(() => props.mode === 'add' ? '请输入密码' : '不修改请留空')
const saveText = computed(() => props.mode === 'add' ? '保存' : '保存修改')

function onSave() {
  emit('save', { ...local })
}
</script>

<style scoped>
</style>
