<template>
  <a-sub-menu :key="menuInfo.path">
    <MenuItemContent :menu-info="menuInfo" />
    <template v-for="item in menuInfo.children" :key="item.path">
      <template v-if="!item.children">
        <MenuItemContent :menu-info="item" />
      </template>
      <template v-else>
        <sub-menu :key="item.path" :menu-info="item" />
      </template>
    </template>
  </a-sub-menu>
</template>

<script setup lang="ts">
  import { BallcatRouteRecordRaw } from '@/router/types'
  import MenuItemContent from '@/components/menu/MenuItemContent.vue'

  defineProps<{
    menuInfo: BallcatRouteRecordRaw
  }>()
</script>

<style scoped></style>
