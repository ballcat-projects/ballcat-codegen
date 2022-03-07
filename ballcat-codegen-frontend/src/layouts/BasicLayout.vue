<template>
  <a-layout id="components-layout-side">
    <a-layout>
      <a-layout-header
        style="padding: 0; height: 48px; line-height: 48px; width: 100%; z-index: 19"
      >
        <basic-header theme="light" />
      </a-layout-header>
      <a-layout-content style="margin: 16px 16px 8px">
        <div class="ballcat-layout-content wide">
          <router-view />
        </div>
      </a-layout-content>
      <a-layout-footer style="padding: 0 50px 8px">
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

    .ballcat-layout-content {
      &.wide {
        max-width: 80%;
        margin: 0 auto;
      }
    }
  }
</style>
