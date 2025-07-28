<template>
  <a-button 
    type="primary" 
    :class="['modern-btn', customClass]"
    v-bind="$attrs"
  >
    <template #icon>
      <plus-outlined />
    </template>
    新建
  </a-button>
</template>

<script setup>
import { PlusOutlined } from '@ant-design/icons-vue'

defineProps({
  customClass: {
    type: String,
    default: ''
  }
})
</script>

<style scoped>
:deep(.modern-btn) {
  border-radius: 8px !important;
  box-shadow: 0 1px 2px 0 rgb(0 0 0 / 0.05) !important;
  border: none !important;
  font-weight: 500 !important;
  transition: all 0.2s !important;
}

:deep(.modern-btn:hover) {
  transform: translateY(-1px) !important;
  box-shadow: 0 4px 8px 0 rgb(0 0 0 / 0.12) !important;
}
</style>
