<template>
  <a-sub-menu :key="menuInfo.path">
    <template v-if="menuInfo.meta && menuInfo.meta.icon" #icon>
      <icon :type="menuInfo.meta?.icon" />
    </template>
    <template #title>{{ menuInfo.meta?.title }}</template>
    <template v-for="item in menuInfo.children" :key="item.path">
      <template v-if="!item.children">
        <a-menu-item :key="item.path" @click="$router.push({ path: item.path })">
          <template v-if="item.meta && item.meta.icon" #icon>
            <icon :type="item.meta.icon" />
          </template>
          {{ item.meta?.title }}
        </a-menu-item>
      </template>
      <template v-else>
        <sub-menu :key="item.path" :menu-info="item" />
      </template>
    </template>
  </a-sub-menu>
</template>

<script setup lang="ts">
  import { BallcatRouteRecordRaw } from '@/router/types'

  defineProps<{
    menuInfo: BallcatRouteRecordRaw
  }>()
</script>

<style scoped></style>
