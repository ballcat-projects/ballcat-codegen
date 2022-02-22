<template>
  <a-layout id="components-layout-side">
    <a-layout-sider v-model:collapsed="collapsed" :trigger="null" collapsible :collapsed-width="48">
      <div class="logo">
        <img src="../assets/logo.svg" class="logo" alt="logo" style="width: 32px; height: auto" />
        <span v-show="!collapsed" class="project-name"> Code Generator </span>
      </div>
      <side-menu />
    </a-layout-sider>
    <a-layout>
      <a-layout-header style="background: #fff; padding: 0">
        <menu-unfold-outlined v-if="collapsed" class="trigger" @click="toggleCollapsed" />
        <menu-fold-outlined v-else class="trigger" @click="toggleCollapsed" />
        <BasicHeader />
      </a-layout-header>
      <a-layout-content style="margin: 16px 16px 8px">
        <router-view />
      </a-layout-content>
      <a-layout-footer style="padding: 0 50px 8px">
        <BasicFooter />
      </a-layout-footer>
    </a-layout>
  </a-layout>
</template>
<script setup lang="ts">
  import { MenuUnfoldOutlined, MenuFoldOutlined } from '@ant-design/icons-vue'
  import { ref } from 'vue'
  import BasicHeader from '@/layouts/BasicHeader.vue'
  import BasicFooter from '@/layouts/BasicFooter.vue'
  import SideMenu from '@/layouts/SideMenu.vue'

  const collapsed = ref<boolean>(false)
  const toggleCollapsed = () => (collapsed.value = !collapsed.value)
</script>
<style lang="less">
  #components-layout-side {
    min-height: 100vh;
    height: 100%;

    .trigger {
      font-size: 18px;
      line-height: 48px;
      padding: 0 24px;
      cursor: pointer;
      transition: color 0.3s;

      &:hover {
        color: #1890ff;
      }
    }

    .logo {
      line-height: 64px;
      height: 64px;
      text-align: center;
      overflow: hidden;

      .project-name {
        color: #fff;
        display: inline-block;
        margin-left: 8px;
        font-weight: 600;
        font-size: 18px;
        line-height: 32px;
        vertical-align: middle;
        animation: fade-in;
        animation-duration: 0.2s;
      }
    }

    .ant-layout-header {
      height: 48px;
      line-height: 48px;
    }
  }
</style>
