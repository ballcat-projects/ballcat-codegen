<template>
  <div class="p-6 space-y-4">
    <div>
      <h4 class="text-lg font-semibold text-gray-900">{{ record.title }}</h4>
    </div>

    <div class="space-y-3">
      <div v-if="record.url">
        <label class="text-sm font-medium text-gray-700">连接摘要</label>
        <div class="text-sm text-gray-900 mt-1 flex items-center gap-2">
          <span>{{ getJdbcSummary(record.url) }}</span>
          <LockOutlined v-if="hasSsl(record.url)" class="text-gray-400 text-xs align-middle" />
        </div>
      </div>

      <div v-if="record.url">
        <label class="text-sm font-medium text-gray-700">完整 JDBC URL</label>
        <p class="text-sm text-gray-900 mt-1 break-all">{{ record.url }}</p>
      </div>

      <div v-if="record.username">
        <label class="text-sm font-medium text-gray-700">用户名</label>
        <p class="text-sm text-gray-900 mt-1">{{ record.username }}</p>
      </div>

      <div v-if="record.dsKey">
        <label class="text-sm font-medium text-gray-700">数据源键</label>
        <p class="text-sm text-gray-900 mt-1">{{ record.dsKey }}</p>
      </div>

      <div v-if="record.createTime">
        <label class="text-sm font-medium text-gray-700">创建时间</label>
        <p class="text-sm text-gray-900 mt-1">{{ record.createTime }}</p>
      </div>
    </div>

    <div class="pt-4 border-t border-gray-200 space-y-2">
      <button @click="$emit('edit')"
        class="w-full flex items-center justify-center px-4 py-2 text-blue-600 border border-blue-200 rounded-lg hover:bg-blue-50 transition-colors">
        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none"
          stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
          class="lucide lucide-square-pen w-4 h-4 mr-2">
          <path d="M12 3H5a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
          <path
            d="M18.375 2.625a1 1 0 0 1 3 3l-9.013 9.014a2 2 0 0 1-.853.505l-2.873.84a .5.5 0 0 1-.62-.62l.84-2.873a2 2 0 0 1 .506-.852z">
          </path>
        </svg>
        编辑配置
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { DataSourceConfig } from '@/api/gen/datasource-config/types'
import { getJdbcSummary, hasSsl } from '@/utils/jdbc-url'
import { LockOutlined } from '@ant-design/icons-vue'

defineProps<{ record: DataSourceConfig }>()
</script>

<style scoped>
</style>
