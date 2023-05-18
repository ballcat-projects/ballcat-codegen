<template>
  <component :is="AntdIcons[type]" />
</template>

<script setup lang="ts">
import * as AntdIcons from '@ant-design/icons-vue/es/icons'
defineProps<{
  type: string
}>()
</script>
