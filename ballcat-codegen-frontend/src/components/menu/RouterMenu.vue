<template>
  <nav :class="collapsed ? 'mini-rail' : 'space-y-2'">
    <template v-for="item in menuData" :key="item.path">
    <router-link
        :to="item.path"
        :title="collapsed ? getCollapsedTitle(item) : undefined"
        :aria-label="collapsed ? getTitlePlain(item) : undefined"
        :class="[
          'w-full flex items-center rounded-lg text-left transition-colors duration-200 group border relative min-h-14 py-3 min-w-0',
      collapsed ? 'justify-center px-0' : 'space-x-3 px-4',
          $route.path.startsWith(item.path) 
            ? 'bg-blue-50 text-blue-700 border-blue-200' 
            : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900 border-transparent'
        ]"
      >
        <!-- 使用与示例 HTML 相同的 Lucide 图标 -->
        <svg 
          v-if="getIconName(item.meta?.title) === 'code'"
          xmlns="http://www.w3.org/2000/svg" 
          width="20" 
          height="20" 
          viewBox="0 0 24 24" 
          fill="none" 
          stroke="currentColor" 
          stroke-width="2" 
          stroke-linecap="round" 
          stroke-linejoin="round" 
          :class="[
            'lucide lucide-code w-5 h-5 shrink-0',
            $route.path.startsWith(item.path) ? 'text-blue-600' : 'text-gray-400 group-hover:text-gray-600'
          ]"
        >
          <path d="m16 18 6-6-6-6"></path>
          <path d="m8 6-6 6 6 6"></path>
        </svg>
        
        <svg 
          v-else-if="getIconName(item.meta?.title) === 'folder'"
          xmlns="http://www.w3.org/2000/svg" 
          width="20" 
          height="20" 
          viewBox="0 0 24 24" 
          fill="none" 
          stroke="currentColor" 
          stroke-width="2" 
          stroke-linecap="round" 
          stroke-linejoin="round" 
          :class="[
            'lucide lucide-folder w-5 h-5 shrink-0',
            $route.path.startsWith(item.path) ? 'text-blue-600' : 'text-gray-400 group-hover:text-gray-600'
          ]"
        >
          <path d="M20 20a2 2 0 0 0 2-2V8a2 2 0 0 0-2-2h-7.9a2 2 0 0 1-1.69-.9L9.6 3.9A2 2 0 0 0 7.93 3H4a2 2 0 0 0-2 2v13a2 2 0 0 0 2 2Z"></path>
        </svg>
        
        <svg 
          v-else-if="getIconName(item.meta?.title) === 'database'"
          xmlns="http://www.w3.org/2000/svg" 
          width="20" 
          height="20" 
          viewBox="0 0 24 24" 
          fill="none" 
          stroke="currentColor" 
          stroke-width="2" 
          stroke-linecap="round" 
          stroke-linejoin="round" 
          :class="[
            'lucide lucide-database w-5 h-5 shrink-0',
            $route.path.startsWith(item.path) ? 'text-blue-600' : 'text-gray-400 group-hover:text-gray-600'
          ]"
        >
          <ellipse cx="12" cy="5" rx="9" ry="3"></ellipse>
          <path d="M3 5V19A9 3 0 0 0 21 19V5"></path>
          <path d="M3 12A9 3 0 0 0 21 12"></path>
        </svg>
        
        <svg 
          v-else-if="getIconName(item.meta?.title) === 'arrow-right-left'"
          xmlns="http://www.w3.org/2000/svg" 
          width="20" 
          height="20" 
          viewBox="0 0 24 24" 
          fill="none" 
          stroke="currentColor" 
          stroke-width="2" 
          stroke-linecap="round" 
          stroke-linejoin="round" 
          :class="[
            'lucide lucide-arrow-right-left w-5 h-5 shrink-0',
            $route.path.startsWith(item.path) ? 'text-blue-600' : 'text-gray-400 group-hover:text-gray-600'
          ]"
        >
          <path d="m16 3 4 4-4 4"></path>
          <path d="M20 7H4"></path>
          <path d="m8 21-4-4 4-4"></path>
          <path d="M4 17h16"></path>
        </svg>
        
        <!-- 默认图标 -->
        <svg 
          v-else
          xmlns="http://www.w3.org/2000/svg" 
          width="20" 
          height="20" 
          viewBox="0 0 24 24" 
          fill="none" 
          stroke="currentColor" 
          stroke-width="2" 
          stroke-linecap="round" 
          stroke-linejoin="round" 
          :class="[
            'lucide w-5 h-5 shrink-0',
            $route.path.startsWith(item.path) ? 'text-blue-600' : 'text-gray-400 group-hover:text-gray-600'
          ]"
        >
          <path d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
        </svg>
        
  <!-- Content -->
  <template v-if="!collapsed">
          <div class="flex-1 min-w-0 overflow-hidden">
            <div :class="[
              'font-medium truncate',
              $route.path.startsWith(item.path) ? 'text-blue-700' : 'text-gray-900'
            ]">
              {{ item.meta?.title || item.name }}
            </div>
            <div class="text-xs text-gray-500 mt-1 truncate">
              {{ getSubtitle(item.meta?.title) }}
            </div>
          </div>
          <!-- Arrow -->
          <svg 
            xmlns="http://www.w3.org/2000/svg" 
            width="24" 
            height="24" 
            viewBox="0 0 24 24" 
            fill="none" 
            stroke="currentColor" 
            stroke-width="2" 
            stroke-linecap="round" 
            stroke-linejoin="round" 
        :class="[
          'lucide lucide-chevron-right w-4 h-4 transition-transform shrink-0',
              $route.path.startsWith(item.path) ? 'text-blue-600 rotate-90' : 'text-gray-400 group-hover:text-gray-600'
            ]"
          >
            <path d="m9 18 6-6-6-6"></path>
          </svg>
        </template>
      </router-link>
    </template>
  </nav>
</template>

<script setup lang="ts">
defineProps<{ collapsed?: boolean }>()
import { useRoute } from 'vue-router'
import { menuRouters } from '@/router'
import type { BallcatRouteRecordRaw } from '@/router/types'

const getMenuData = function (
  routes: Array<BallcatRouteRecordRaw> = [],
  parentKeys: Array<string> = []
) {
  const menuData: Array<BallcatRouteRecordRaw> = []
  routes.forEach(item => {
    // 修复菜单过滤逻辑：如果没有meta或者meta.hiddenInMenu不为true，则显示菜单
    if (!item.meta?.hiddenInMenu) {
      const newItem = { ...item }
      delete newItem.children
      if (item.children && !item.meta?.hiddenInMenu) {
        newItem.children = getMenuData(item.children, [...parentKeys, item.path])
      }
      menuData.push(newItem)
    }
  })
  return menuData
}

const menuData = getMenuData(menuRouters)

const route = useRoute()

// 安全的标题获取，避免 symbol 转换问题
const asText = (v: unknown): string => (typeof v === 'string' ? v : v == null ? '' : String(v))
const getTitlePlain = (item: BallcatRouteRecordRaw) => asText((item.meta as any)?.title ?? (item as any).name)
const getCollapsedTitle = (item: BallcatRouteRecordRaw) => `${getTitlePlain(item)}｜${getSubtitle((item.meta as any)?.title)}`

// 根据菜单标题生成副标题
const getSubtitle = (title: string | undefined) => {
  const subtitleMap: Record<string, string> = {
    '代码生成': '智能代码生成工具',
    '模板管理': '管理代码模板组',
    '数据类型管理': '管理数据字段映射',
    '数据源管理': '配置数据源连接'
  }
  return subtitleMap[title || ''] || '功能管理'
}

// 根据菜单标题映射到对应的图标名称
const getIconName = (title: string | undefined) => {
  const iconMap: Record<string, string> = {
    '代码生成': 'code',
    '模板管理': 'folder',
    '模板组管理': 'folder',
    '数据源管理': 'database',
    '数据类型管理': 'arrow-right-left',
    '数据映射管理': 'arrow-right-left'
  }
  return iconMap[title || ''] || 'file'
}
</script>

<style scoped>
/* 确保选中状态的边框效果更明显 */
.menu-item-active {
  background-color: rgb(239 246 255) !important;
  color: rgb(29 78 216) !important;
  border: 1px solid rgb(191 219 254) !important;
  box-shadow: 0 1px 2px 0 rgb(0 0 0 / 0.05) !important;
}

/* 折叠态 mini rail 布局 */
.mini-rail {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

/* 折叠态不再展示左侧选中竖条，保留背景/文字高亮 */
</style>
