<template>
  <div class="layout-container">
    <!-- Header -->
    <header class="bg-white shadow-sm border-b border-gray-200">
      <div class="px-6 py-4">
        <div class="flex items-center justify-between">
          <!-- Logo and Title -->
          <div class="flex items-center space-x-3">
            <div class="w-10 h-10 bg-gradient-to-r from-blue-500 to-purple-600 rounded-lg flex items-center justify-center">
              <span class="text-white font-bold text-lg">BC</span>
            </div>
            <div>
              <h1 class="text-xl font-bold text-gray-900">Ballcat Code Generator</h1>
              <p class="text-sm text-gray-500">v1.5.0</p>
            </div>
          </div>
          
          <!-- Header Actions -->
          <div class="flex items-center space-x-4">
            <a 
              href="https://v2.ballcat.cn/codegen/" 
              target="_blank" 
              rel="noopener noreferrer" 
              class="flex items-center space-x-2 px-3 py-2 text-gray-600 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-colors"
            >
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C20.832 18.477 19.247 18 17.5 18c-1.746 0-3.332.477-4.5 1.253" />
              </svg>
              <span class="text-sm font-medium">文档</span>
            </a>
            <a 
              href="https://github.com/ballcat-projects/ballcat-codegen" 
              target="_blank" 
              rel="noopener noreferrer" 
              class="flex items-center space-x-2 px-3 py-2 text-gray-600 hover:text-gray-900 hover:bg-gray-50 rounded-lg transition-colors"
            >
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19c-5 1.5-5-2.5-7-3m14 6v-3.87a3.37 3.37 0 0 0-.94-2.61c3.14-.35 6.44-1.54 6.44-7A5.44 5.44 0 0 0 20 4.77 5.07 5.07 0 0 0 19.91 1S18.73.65 16 2.48a13.38 13.38 0 0 0-7 0C6.27.65 5.09 1 5.09 1A5.07 5.07 0 0 0 5 4.77a5.44 5.44 0 0 0-1.5 3.78c0 5.42 3.3 6.61 6.44 7A3.37 3.37 0 0 0 9 18.13V22" />
              </svg>
              <span class="text-sm font-medium">GitHub</span>
            </a>
            <a 
              href="https://gitee.com/ballcat-projects/ballcat-codegen" 
              target="_blank" 
              rel="noopener noreferrer" 
              class="flex items-center space-x-2 px-3 py-2 text-gray-600 hover:text-orange-600 hover:bg-orange-50 rounded-lg transition-colors"
            >
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              <span class="text-sm font-medium">Gitee</span>
            </a>
          </div>
        </div>
      </div>
    </header>

    <!-- Main Content Area -->
    <div class="layout-body">
      <!-- Sidebar -->
      <aside class="layout-sidebar">
        <div class="p-6">
          <h2 class="text-lg font-semibold text-gray-900 mb-6">功能菜单</h2>
          <RouterMenu />
        </div>
      </aside>

      <!-- Main Content -->
      <main class="layout-main">
        <div class="layout-content">
          <router-view />
        </div>
      </main>
    </div>

    <!-- Footer -->
    <footer class="bg-white border-t border-gray-200">
      <div class="px-6 py-4">
        <div class="flex items-center justify-center">
          <p class="text-sm text-gray-600 flex items-center">
            <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            © All rights reserved. Powered by Ballcat Projects.
          </p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import RouterMenu from '@/components/menu/RouterMenu.vue'
</script>

<style scoped>
/* 弹性高度布局系统 - 侧边栏跟随内容 */
.layout-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh; /* 最小视窗高度，允许内容撑开 */
  background: linear-gradient(to bottom right, #f9fafb, #f3f4f6);
}

/* 主体内容区域 - 弹性增长 */
.layout-body {
  display: flex;
  flex: 1; /* 占据头部和底部之间的所有空间 */
}

/* 侧边栏 - 跟随内容高度 */
.layout-sidebar {
  width: 288px; /* w-72 = 18rem = 288px */
  background: white;
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.1);
  border-right: 1px solid #e5e7eb;
  flex-shrink: 0; /* 防止宽度收缩 */
  /* 不设置固定高度，让它跟随内容区域 */
}

/* 主内容区域 - 自然高度 */
.layout-main {
  flex: 1; /* 占据剩余宽度 */
  display: flex;
  flex-direction: column;
  min-width: 0; /* 允许内容收缩 */
}

/* 内容容器 - 自然高度，内容过多时可滚动 */
.layout-content {
  flex: 1;
  padding: 1.5rem; /* p-6 */
  max-width: 80rem; /* max-w-7xl */
  margin: 0 auto;
  width: 100%;
  /* 移除 overflow-y: auto，让页面整体滚动 */
}

/* 当内容过多时，整个页面可滚动 */
body {
  overflow-x: hidden; /* 防止水平滚动 */
}

/* 优化滚动条样式（应用到整个页面） */
::-webkit-scrollbar {
  width: 6px;
}

::-webkit-scrollbar-track {
  background: #f1f5f9;
}

::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}
</style>