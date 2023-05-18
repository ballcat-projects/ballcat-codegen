<template>
  <a-menu-item :key="menuInfo.path" @click="jump(menuInfo.path)">
    <template v-if="iconType" #icon>
      <Icon :type="iconType" />
    </template>
    {{ title }}
  </a-menu-item>
</template>

<script setup lang="ts">
import type { BallcatRouteRecordRaw } from '@/router/types'
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import Icon from '@/components/Icon/index.vue'

const props = defineProps<{
  menuInfo: BallcatRouteRecordRaw
}>()

const iconType = computed(() => props.menuInfo.meta?.icon || '')

const title = computed(() => props.menuInfo.meta?.title || '')

const router = useRouter()
function jump(path: string) {
  router.push({ path })
}
</script>

<style scoped></style>
