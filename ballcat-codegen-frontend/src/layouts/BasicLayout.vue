<template>
  <div class="flex flex-col min-h-screen bg-gradient-to-br from-gray-50 to-gray-100">
    <!-- Header -->
    <header class="bg-white shadow-sm border-b border-gray-200">
      <div class="px-6 py-4">
        <div class="flex items-center justify-between">
          <!-- Logo and Title -->
          <div class="flex items-center space-x-3">
            <div class="w-10 h-10 bg-gradient-to-r from-blue-500 to-purple-600 rounded-lg flex items-center justify-center">
              <span class="text-white font-bold text-lg">CG</span>
            </div>
            <div>
              <h1 class="text-xl font-bold text-gray-900">Code Generator</h1>
              <p class="text-sm text-gray-500">v2.1.0</p>
            </div>
          </div>
          
          <!-- Header Actions -->
          <div class="flex items-center space-x-4">
            <a 
              href="https://docs.codegen.com/" 
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
              href="https://github.com/codegen/codegen" 
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
              href="https://gitee.com/codegen/codegen" 
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
    <div class="flex flex-1">
      <!-- Sidebar -->
      <aside class="w-72 bg-white shadow-sm border-r border-gray-200">
        <div class="p-6">
          <h2 class="text-lg font-semibold text-gray-900 mb-6">功能菜单</h2>
          <RouterMenu />
        </div>
      </aside>

      <!-- Main Content -->
      <main class="flex-1 p-6 overflow-auto">
        <div class="max-w-7xl mx-auto">
          <router-view />
        </div>
      </main>
    </div>

    <!-- Footer -->
    <footer class="bg-white border-t border-gray-200 mt-auto">
      <div class="px-6 py-4">
        <div class="flex items-center justify-center">
          <p class="text-sm text-gray-600 flex items-center">
            <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            © 2024 Code Generator. All rights reserved. Powered by Modern Tech Stack.
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
/* 保持原有的背景渐变效果 */
.min-h-screen {
  background: linear-gradient(to bottom right, #f9fafb, #f3f4f6);
}
</style>