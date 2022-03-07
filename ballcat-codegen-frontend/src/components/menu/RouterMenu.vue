<template>
  <a-menu
    v-model:openKeys="openKeys"
    v-model:selectedKeys="selectedKeys"
    :mode="mode"
    :theme="theme"
  >
    <template v-for="item in menuData" :key="item.path">
      <template v-if="!item.children">
        <MenuItemContent :menu-info="item" />
      </template>
      <template v-else>
        <sub-menu :key="item.path" :menu-info="item" />
      </template>
    </template>
  </a-menu>
</template>

<script setup lang="ts">
  import { useRoute } from 'vue-router'
  import { ref, watchEffect } from 'vue'
  import SubMenu from './SubMenu.vue'
  import { menuRouters } from '@/router'
  import { BallcatRouteRecordRaw } from '@/router/types'

  defineProps<{
    mode?: 'inline' | 'vertical' | 'horizontal'
    theme?: 'dark' | 'light'
  }>()

  const openKeysMap = new Map()

  const getMenuData = function (
    routes: Array<BallcatRouteRecordRaw> = [],
    parentKeys: Array<string> = []
  ) {
    const menuData: Array<BallcatRouteRecordRaw> = []
    routes.forEach(item => {
      if (item.meta && !item.meta.hiddenInMenu) {
        const newItem = { ...item }
        delete newItem.children
        openKeysMap.set(item.path, parentKeys)
        if (item.children && !item.meta.hiddenInMenu) {
          newItem.children = getMenuData(item.children, [...parentKeys, item.path])
        }
        menuData.push(newItem)
      }
    })
    return menuData
  }

  const menuData = getMenuData(menuRouters)

  const route = useRoute()

  const selectedKeys = ref<string[]>()

  const openKeys = ref<string[]>(['/gen'])

  watchEffect(() => {
    const routePath: string = route.path
    selectedKeys.value = [routePath]
  })
</script>
