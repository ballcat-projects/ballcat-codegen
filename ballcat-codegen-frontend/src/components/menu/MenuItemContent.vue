<template>
  <a-menu-item :key="menuInfo.path" @click="$router.push({ path: menuInfo.path })">
    <template v-if="icon" #icon>
      <Icon :type="icon" />
    </template>
    {{ title }}
  </a-menu-item>
</template>

<script setup lang="ts">
  import { BallcatRouteRecordRaw } from '@/router/types'
  import { computed } from 'vue'
  import { ComputedRef } from '@vue/reactivity'
  import Icon from '@/components/Icon/index.vue'

  const props = defineProps<{
    menuInfo: BallcatRouteRecordRaw
  }>()

  const icon: ComputedRef<string> = computed(() => {
    return props.menuInfo.meta?.icon || ''
  })

  const title: ComputedRef<string> = computed(() => {
    return props.menuInfo.meta?.title || ''
  })
</script>

<style scoped></style>
