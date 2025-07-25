<template>
  <a-layout id="components-layout-side">
    <a-layout style="min-height: 100vh; display: flex; flex-direction: column;">
      <a-layout-header
        style="padding: 0; height: 64px; line-height: 64px; width: 100%; z-index: 19; background: #fff; box-shadow: 0 2px 8px rgba(0,0,0,0.04), 0 1px 2px rgba(0,0,0,0.06); flex-shrink: 0; border-bottom: 1px solid #f0f0f0;"
      >
        <basic-header theme="light" />
      </a-layout-header>
      <a-layout-content style="flex: 1; margin: 16px; background: #f5f5f5; display: flex; flex-direction: column;">
        <div class="ballcat-layout-content wide">
          <router-view />
        </div>
      </a-layout-content>
      <a-layout-footer style="padding: 0; flex-shrink: 0;">
        <BasicFooter />
      </a-layout-footer>
    </a-layout>
  </a-layout>
</template>
<script setup lang="ts">
import BasicHeader from '@/layouts/BasicHeader.vue'
import BasicFooter from '@/layouts/BasicFooter.vue'
</script>
<style lang="less">
#components-layout-side {
  min-height: 100vh;
  height: 100%;
  background: @bg-color-page;

  .ballcat-layout-content {
    &.wide {
      width: 100%;
      margin: 0 auto;
      flex: 1;
      display: flex;
      flex-direction: column;
      
      // 大屏幕优化：平衡空间利用和内容可读性
      @media (min-width: 2560px) {
        max-width: 1800px;
        padding: 0 40px;
      }
      
      @media (min-width: 1920px) and (max-width: 2559px) {
        max-width: 1600px;
        padding: 0 32px;
      }
      
      @media (min-width: 1440px) and (max-width: 1919px) {
        max-width: 1300px;
        padding: 0 28px;
      }
      
      @media (min-width: 1200px) and (max-width: 1439px) {
        max-width: 1100px;
        padding: 0 24px;
      }
      
      @media (min-width: 768px) and (max-width: 1199px) {
        max-width: 95%;
        padding: 0 20px;
      }
      
      @media (max-width: 767px) {
        max-width: 100%;
        padding: 0 16px;
      }
      
      @media (max-width: 480px) {
        padding: 0 12px;
      }
    }
  }
}

// 现代化的卡片设计
:global(.ant-card) {
  border-radius: @border-radius-lg;
  box-shadow: @shadow-card;
  border: 1px solid #f0f0f0;
  flex: 1;
  display: flex;
  flex-direction: column;
  background: @bg-color-container;
  transition: box-shadow 0.3s ease, transform 0.2s ease;
  
  &:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
    transform: translateY(-1px);
  }
  
  .ant-card-head {
    border-bottom: 1px solid #f0f0f0;
    background: linear-gradient(135deg, #fafafa 0%, #f5f5f5 100%);
    border-radius: @border-radius-lg @border-radius-lg 0 0;
    flex-shrink: 0;
    
    .ant-card-head-title {
      font-weight: 600;
      color: #262626;
      font-size: 16px;
    }
  }
  
  .ant-card-body {
    flex: 1;
    display: flex;
    flex-direction: column;
    padding: 24px;
  }
}

// 工具栏现代化设计
:global(.table-list-toolbar) {
  background: @bg-color-container;
  padding: 20px 24px;
  border-radius: @border-radius-lg @border-radius-lg 0 0;
  border: 1px solid #f0f0f0;
  border-bottom: none;
  margin-bottom: 0;
  flex-shrink: 0;
  box-shadow: @box-shadow-base;
  
  .ant-btn {
    border-radius: @border-radius-base;
    font-weight: 500;
    transition: all 0.2s ease;
    
    &:hover {
      transform: translateY(-1px);
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    }
  }
  
  .ant-input, .ant-select {
    border-radius: @border-radius-base;
    
    &:hover, &:focus {
      box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.1);
    }
  }
}

// 表格现代化样式
:global(.ant-table-wrapper) {
  background: @bg-color-container;
  border-radius: 0 0 @border-radius-lg @border-radius-lg;
  border: 1px solid #f0f0f0;
  border-top: none;
  flex: 1;
  overflow: hidden;
  box-shadow: @box-shadow-base;
  
  .ant-table {
    .ant-table-thead > tr > th {
      background: #fafafa;
      border-bottom: 1px solid #f0f0f0;
      font-weight: 600;
      color: #595959;
      font-size: 14px;
      
      &:first-child {
        border-radius: 0;
      }
      
      &:last-child {
        border-radius: 0;
      }
    }
    
    .ant-table-tbody > tr {
      transition: background-color 0.2s ease;
      
      &:hover > td {
        background: #f8f9fa !important;
      }
      
      > td {
        border-bottom: 1px solid #f5f5f5;
        transition: background-color 0.2s ease;
      }
    }
  }
  
  .ant-table-container {
    border-radius: 0 0 @border-radius-lg @border-radius-lg;
    overflow: auto;
  }
}

// 按钮系统优化
:global(.ant-btn) {
  border-radius: @border-radius-base;
  font-weight: 500;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
  
  &:hover {
    transform: translateY(-1px);
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  }
  
  &:active {
    transform: translateY(0);
  }
  
  &.ant-btn-primary {
    background: linear-gradient(135deg, @primary-color 0%, #40a9ff 100%);
    border: none;
    
    &:hover {
      background: linear-gradient(135deg, #40a9ff 0%, @primary-color 100%);
    }
  }
  
  &.ant-btn-link {
    font-weight: 500;
    padding: 4px 8px;
    
    &:hover {
      background: rgba(24, 144, 255, 0.06);
      border-radius: @border-radius-sm;
    }
  }
}

// 状态指示器
:global(.status-indicator) {
  display: inline-flex;
  align-items: center;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
  
  &.status-success {
    background: #f6ffed;
    color: @success-color;
    border: 1px solid #b7eb8f;
  }
  
  &.status-warning {
    background: #fffbe6;
    color: @warning-color;
    border: 1px solid #ffe58f;
  }
  
  &.status-error {
    background: #fff2f0;
    color: @error-color;
    border: 1px solid #ffccc7;
  }
}

// 加载状态优化
:global(.ant-spin-container) {
  transition: opacity 0.3s ease;
}

:global(.ant-spin-blur) {
  opacity: 0.6;
  filter: blur(0.5px);
}
</style>
