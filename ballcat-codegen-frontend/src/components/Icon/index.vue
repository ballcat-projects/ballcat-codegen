<template>
  <component :is="AntdIcons[type]" />
</template>

<script setup lang="ts">
  import * as AntdIcons from '@ant-design/icons-vue/lib/icons'
  defineProps<{
    type: string
  }>()
</script>
