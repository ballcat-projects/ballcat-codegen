<template>
  <nav class="breadcrumb-bar" aria-label="面包屑导航">
    <ol class="trail" role="list">
      <!-- Home -->
      <li class="crumb">
        <router-link to="/codegen" class="home-link" aria-label="返回首页">
          <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l9-9 9 9" />
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 21V9h6v12" />
          </svg>
          <span class="text">首页</span>
        </router-link>
      </li>

      <!-- Separator -->
      <li v-if="currentTitle" class="sep" aria-hidden="true">
        <svg class="chev" viewBox="0 0 24 24" fill="none" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
        </svg>
      </li>

      <!-- Current -->
      <li v-if="currentTitle" class="crumb current" aria-current="page">
        <span class="current-chip">
          <span class="label">{{ currentTitle }}</span>
        </span>
      </li>
    </ol>
  </nav>
  
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const currentTitle = computed(() => (route.meta?.title as string) || '')
</script>

<style scoped lang="less">
.breadcrumb-bar {
  margin-bottom: clamp(8px, 1.2vw, 12px);
}
.trail { display: flex; align-items: center; flex-wrap: wrap; gap: 6px; }
.crumb { display: inline-flex; align-items: center; }
.home-link {
  display: inline-flex; align-items: center; gap: 6px;
  color: @slate-600; text-decoration: none;
  padding: 4px 8px; border-radius: 9999px;
  border: 1px solid transparent;
  transition: @animation-base;
}
.home-link:hover { color: @blue-600; background: @slate-50; border-color: @border-color-200; }
.home-link .icon { width: 16px; height: 16px; }
.home-link .text { font-size: @font-size-sm; }

.sep { color: @slate-400; display: inline-flex; padding: 0 2px; }
.sep .chev { width: 16px; height: 16px; }

.current .current-chip {
  display: inline-flex; align-items: center; gap: 6px;
  padding: 4px 10px; border-radius: 9999px;
  background: @gray-1; border: 1px solid @border-color-200;
  color: @text-color-primary;
  box-shadow: 0 2px 6px rgba(0,0,0,0.04);
}
.current .label { font-size: @font-size-sm; font-weight: @font-weight-medium; }

/* 小屏更简洁 */
@media (max-width: @screen-sm) {
  .home-link .text { display: none; }
}
</style>
