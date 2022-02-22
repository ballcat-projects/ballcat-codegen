<template>
  <component :is="AntdIcon[type]" />
</template>

<script setup lang="ts">
  import * as AntdIcon from '@ant-design/icons-vue'
  defineProps<{
    type: string
  }>()
</script>
