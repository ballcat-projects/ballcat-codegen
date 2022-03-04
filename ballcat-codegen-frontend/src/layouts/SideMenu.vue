<template>
  <a-menu
    v-model:openKeys="openKeys"
    v-model:selectedKeys="selectedKeys"
    mode="inline"
    theme="dark"
  >
    <template v-for="item in menuData" :key="item.path">
      <template v-if="!item.children">
        <a-menu-item :key="item.path" @click="$router.push({ path: item.path })">
          <template v-if="item.meta.icon" #icon>
            <icon :type="item.meta.icon" />
          </template>
          {{ item.meta.title }}
        </a-menu-item>
      </template>
      <template v-else>
        <sub-menu :key="item.path" :menu-info="item" />
      </template>
    </template>
  </a-menu>
</template>

<script lang="ts">
  import { RouteRecordRaw } from 'vue-router'

  const openKeysMap = new Map()

  const getMenuData = function (
    routes: Array<RouteRecordRaw> = [],
    parentKeys: Array<string> = []
  ) {
    const menuData: Array<RouteRecordRaw> = []
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
</script>

<script setup lang="ts">
  import { useRoute } from 'vue-router'
  import { ref, watchEffect } from 'vue'
  import SubMenu from './SubMenu.vue'
  import { menuRouters } from '@/router'
  import Icon from '../components/Icon/index.vue'

  const menuData = getMenuData(menuRouters)

  const route = useRoute()

  const selectedKeys = ref<string[]>()

  const openKeys = ref<string[]>(['/gen'])

  watchEffect(() => {
    const routePath: string = route.path
    selectedKeys.value = [routePath]
  })
</script>
