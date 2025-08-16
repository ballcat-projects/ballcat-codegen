<template>
  <div
    class="layout-container"
    :class="{
      'grid-sider-expanded': !layout.isOverlay && !layout.effectiveCollapsed,
      'grid-sider-collapsed': !layout.isOverlay && layout.effectiveCollapsed,
      'grid-sider-overlay': layout.isOverlay
    }"
    :style="{ '--header-h': headerHeight + 'px' }"
  >
    <!-- Header -->
  <header
      ref="headerRef"
      class="bg-white shadow-sm border-b border-gray-200"
      :class="{ 'compact-header': compactHeader }"
    >
      <div class="layout-header-inner px-6 py-4">
        <div class="flex items-center justify-between">
          <!-- Left: Sidebar Toggle + Logo + Brand Title -->
          <div class="flex items-center space-x-3">
            <!-- Sidebar Toggle (Hamburger) only visible in overlay mode -->
            <button v-if="layout.isOverlay"
              class="flex items-center justify-center w-9 h-9 rounded-md text-gray-600 hover:text-blue-600 hover:bg-blue-50 transition-colors"
              :aria-expanded="layout.isOverlay ? layout.drawerOpen : !layout.effectiveCollapsed"
              aria-controls="sidebar"
              aria-label="切换侧边栏"
              @click="layout.toggleCollapsed()">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
              </svg>
            </button>
            <div
              class="header-logo w-10 h-10 bg-gradient-to-r from-blue-500 to-purple-600 rounded-lg flex items-center justify-center">
              <span class="text-white font-bold text-lg">BC</span>
            </div>
            <div>
              <h1 class="product-title text-xl font-bold text-gray-900">Ballcat Code Generator</h1>
              <p class="header-subtitle text-sm text-gray-500">v1.5.0</p>
            </div>
          </div>

          <!-- Header Actions (Right) -->
      <div class="flex items-center space-x-2 header-actions">
            <!-- Wide Mode Toggle (outward/inward arrows) -->
            <button
              class="flex items-center space-x-2 px-3 py-2 text-gray-600 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-colors"
              :aria-pressed="layout.wideMode"
              aria-label="切换宽屏模式"
              @click="layout.toggleWide()"
              :title="layout.wideMode ? '恢复默认宽度' : '宽屏模式'">
              <ArrowsAltOutlined v-if="!layout.wideMode" class="text-base" />
              <ShrinkOutlined v-else class="text-base" />
              <span class="action-label text-sm font-medium">{{ layout.wideMode ? '恢复默认' : '宽屏模式' }}</span>
            </button>

            <a href="https://v2.ballcat.cn/codegen/" target="_blank" rel="noopener noreferrer"
              class="flex items-center space-x-2 px-3 py-2 text-gray-600 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-colors">
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C20.832 18.477 19.247 18 17.5 18c-1.746 0-3.332.477-4.5 1.253" />
              </svg>
              <span class="action-label text-sm font-medium">文档</span>
            </a>
            <a href="https://github.com/ballcat-projects/ballcat-codegen" target="_blank" rel="noopener noreferrer"
              class="flex items-center space-x-2 px-3 py-2 text-gray-600 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-colors">
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M9 19c-5 1.5-5-2.5-7-3m14 6v-3.87a3.37 3.37 0 0 0-.94-2.61c3.14-.35 6.44-1.54 6.44-7A5.44 5.44 0 0 0 20 4.77 5.07 5.07 0 0 0 19.91 1S18.73.65 16 2.48a13.38 13.38 0 0 0-7 0C6.27.65 5.09 1 5.09 1A5.07 5.07 0 0 0 5 4.77a5.44 5.44 0 0 0-1.5 3.78c0 5.42 3.3 6.61 6.44 7A3.37 3.37 0 0 0 9 18.13V22" />
              </svg>
              <span class="action-label text-sm font-medium">GitHub</span>
            </a>
            <a href="https://gitee.com/ballcat-projects/ballcat-codegen" target="_blank" rel="noopener noreferrer"
              class="flex items-center space-x-2 px-3 py-2 text-gray-600 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-colors">
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M9 12l2 2 4-4M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              <span class="action-label text-sm font-medium">Gitee</span>
            </a>
          </div>
        </div>
      </div>
    </header>

    <!-- Main Content Area -->
  <div class="layout-body">
      <!-- Sidebar -->
      <aside
        id="sidebar"
        class="layout-sidebar"
        :class="{ collapsed: layout.effectiveCollapsed && !layout.isOverlay, overlay: layout.isOverlay, open: layout.isOverlay && layout.drawerOpen }"
        role="navigation"
        :aria-hidden="layout.isOverlay ? !layout.drawerOpen : false"
      >
        <div class="p-6">
          <RouterMenu :collapsed="layout.effectiveCollapsed && !layout.isOverlay" />
        </div>
        <!-- Sidebar bottom footer toggle (non-overlay only) -->
        <div v-if="!layout.isOverlay" class="sider-footer px-4 py-3 border-t border-gray-100/80">
          <button
            class="w-full flex items-center gap-2 px-3 py-2 rounded-lg text-gray-600 hover:text-blue-600 hover:bg-blue-50 transition-colors whitespace-nowrap overflow-hidden"
            :class="layout.effectiveCollapsed ? 'justify-center' : 'justify-center'"
            :aria-pressed="layout.effectiveCollapsed"
            :title="layout.effectiveCollapsed ? '展开菜单' : '收起菜单'"
            @click="layout.toggleCollapsed()"
          >
            <!-- Expanded state: left chevron + label -->
            <LeftOutlined v-if="!layout.effectiveCollapsed" class="text-base" />
            <span v-if="!layout.effectiveCollapsed" class="text-sm font-medium truncate">收起菜单</span>

            <!-- Collapsed state: plain icon (no custom border/size) -->
            <span v-else class="inline-flex items-center"><RightOutlined class="text-base" /><span class="sr-only">展开菜单</span></span>
          </button>
        </div>
      </aside>

      <!-- Overlay Mask -->
      <div
        v-if="layout.isOverlay && layout.drawerOpen"
        class="sidebar-mask"
        aria-hidden="true"
        @click="layout.toggleDrawer(false)"
      />

      <!-- Main Content -->
      <main class="layout-main">
  <div class="layout-content" :class="{ 'wide-mode': layout.wideMode }">
          <router-view />
        </div>
      </main>
    </div>

    <!-- Footer -->
  <footer>
      <div class="layout-footer-inner px-6 py-4">
        <div class="content-footer" :class="{ 'wide-mode': layout.wideMode }">
          <div class="flex items-center justify-center">
            <p class="text-sm text-gray-600 flex items-center">
              <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              © All rights reserved. Powered by Ballcat Projects.
            </p>
          </div>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { onMounted, onBeforeUnmount, ref, nextTick } from 'vue'
import RouterMenu from '@/components/menu/RouterMenu.vue'
import { useLayoutStore } from '@/store'
import { ArrowsAltOutlined, ShrinkOutlined, LeftOutlined, RightOutlined } from '@ant-design/icons-vue'
// no page title in global header per design

const layout = useLayoutStore()

const headerRef = ref<HTMLElement | null>(null)
const headerHeight = ref(0)
const compactHeader = ref(false)

// 保留品牌标题，页面标题不在全局 header 显示

function computeHeaderHeight() {
  nextTick(() => {
    headerHeight.value = headerRef.value?.offsetHeight || 0
  })
}

function handleResize() {
  layout.applyResize()
  compactHeader.value = window.innerHeight <= 768
  computeHeaderHeight()
}

onMounted(() => {
  handleResize()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
})

// no sidebar title; nothing to sync on expand/collapse
</script>

<style scoped lang="less">
/* 网格布局：侧栏跨越 内容+页脚 两行，确保铺到底 */
.layout-container {
  display: grid;
  grid-template-rows: auto 1fr auto; /* header / content / footer */
  grid-template-columns: @layout-sidebar-width 1fr; /* 默认侧栏宽度 */
  grid-template-areas:
    'header header'
    'sider  main'
    'sider  footer';
  min-height: 100vh;
  background: linear-gradient(to bottom right, @layout-gradient-start, @layout-gradient-end);
}

/* 根据状态切换侧栏列宽 */
.layout-container.grid-sider-collapsed {
  grid-template-columns: @layout-sidebar-width-collapsed 1fr;
}
.layout-container.grid-sider-expanded {
  grid-template-columns: @layout-sidebar-width 1fr;
}
.layout-container.grid-sider-overlay {
  grid-template-columns: 0 1fr; /* 抽屉模式时不占水平空间 */
}

/* 将 wrapper 变为内容传播容器，让 aside/main 成为网格项 */
.layout-body {
  display: contents;
}

/* 头部与页脚内边距：clamp 自适应 */
.layout-container > header {
  grid-area: header;
  position: sticky;
  top: 0;
  z-index: 900; /* 低于 overlay(1000)，高于主体 */
}
.layout-header-inner {
  padding: clamp(12px, 2vw, 16px) clamp(16px, 3vw, 24px);
}
.layout-footer-inner {
  padding: clamp(10px, 2vw, 16px) clamp(16px, 3vw, 24px);
}

/* 紧凑头部模式（低垂直分辨率时启用） */
.compact-header .layout-header-inner {
  padding: clamp(6px, 1.2vw, 10px) clamp(12px, 2vw, 16px);
}
.compact-header .header-logo {
  width: 32px !important;
  height: 32px !important;
}
.compact-header .product-title {
  font-size: 1rem; /* ~ text-base */
}
.compact-header .header-subtitle {
  display: none;
}
.compact-header .header-actions .icon-btn {
  width: 32px !important;
  height: 32px !important;
}
.compact-header .header-actions .action-label {
  display: none;
}

/* 侧边栏 - 跟随内容高度 */
.layout-sidebar {
  grid-area: sider;
  width: @layout-sidebar-width;
  /* w-72 = 18rem = 288px */
  background: @bg-color-container;
  box-shadow: @sidebar-shadow;
  border-right: 1px solid @border-color-200;
  flex-shrink: 0;
  /* 防止宽度收缩 */
  /* 在网格中按行自动拉伸，高度由 grid 决定（跨两行） */
  display: flex;
  flex-direction: column;
  transition: width 0.2s ease;
  position: relative;
}

/* 侧栏在滚动时固定在视口内（非抽屉模式） */
.layout-sidebar:not(.overlay) {
  position: sticky;
  top: var(--header-h, 0px);
  height: calc(100vh - var(--header-h, 0px));
}

.layout-sidebar > .p-6 {
  /* 让内部内容填充并支持滚动 */
  flex: 1 1 auto;
  min-height: 0;
  overflow: auto;
}

.layout-sidebar.collapsed {
  width: @layout-sidebar-width-collapsed;
}

.layout-sidebar.collapsed > .p-6 {
  padding: 8px;
}

.layout-sidebar.collapsed h2 {
  display: none;
}

/* 折叠时隐藏菜单标题，仅保留图标 */
.layout-sidebar.collapsed :deep(.ant-menu-title-content) {
  display: none;
}

/* 抽屉模式：侧边栏覆盖 */
.layout-sidebar.overlay {
  position: fixed;
  top: 0;
  left: 0;
  bottom: 0;
  z-index: 1100;
  transform: translateX(-100%);
  transition: transform 0.2s ease;
}

.layout-sidebar.overlay.open {
  transform: translateX(0);
}

.sidebar-mask {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.35);
  backdrop-filter: blur(1px);
  z-index: 1000;
}

/* 主内容区域 - 自然高度 */
.layout-main {
  grid-area: main;
  flex: 1;
  /* 占据剩余宽度 */
  display: flex;
  flex-direction: column;
  min-width: 0;
  /* 允许内容收缩 */
}

/* 内容容器 - 自然高度，内容过多时可滚动 */
.layout-content {
  flex: 1;
  padding: clamp(12px, 2vw, 24px);
  /* p-6 默认 */
  max-width: 80rem;
  /* max-w-7xl */
  margin: 0 auto;
  width: 100%;
  /* 移除 overflow-y: auto，让页面整体滚动 */
}

.layout-content.wide-mode {
  max-width: clamp(72rem, 80vw, 100rem);
}

/* 当内容过多时，整个页面可滚动 */
body {
  overflow-x: hidden;
  /* 防止水平滚动 */
}

/* 优化滚动条样式（应用到整个页面） */
::-webkit-scrollbar {
  width: 6px;
}

::-webkit-scrollbar-track {
  background: @slate-100;
}

::-webkit-scrollbar-thumb {
  background: @slate-300;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: @slate-400;
}

/* 侧栏底部区域 */
.sider-footer {
  flex: 0 0 auto;
  background: fade(@bg-color-container, 98%);
}

/* Footer 透明且与内容同宽 */
footer {
  grid-area: footer;
  background: transparent;
  border-top: none;
  /* grid 下无需偏移，宽度自动按右侧列 */
}
.content-footer {
  max-width: 80rem;
  margin: 0 auto;
}
.content-footer.wide-mode {
  max-width: clamp(72rem, 80vw, 100rem);
}

/* 网格布局下，footer 自动在右侧列，无需额外样式 */

/* 小屏隐藏头部文字标签 */
@media (max-width: @bp-md) {
  .action-label {
    display: none;
  }
}

/* 低视窗高度时收紧垂直间距 */
@media (max-height: 700px) {
  .layout-header-inner {
    padding-top: 10px;
    padding-bottom: 10px;
  }
  .layout-footer-inner {
    padding-top: 10px;
    padding-bottom: 10px;
  }
}

.header-title-block {
  display: flex;
  flex-direction: column;
}

.header-title-row {
  display: flex;
  align-items: baseline;
  gap: 10px;
}
</style>