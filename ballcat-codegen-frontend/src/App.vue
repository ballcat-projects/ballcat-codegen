<template>
  <a-config-provider :theme="theme" :locale="zhCN">
    <router-view />
  </a-config-provider>
</template>

<script setup lang="ts">
import zhCN from 'ant-design-vue/es/locale/zh_CN'
import type { ThemeConfig } from 'ant-design-vue/es/config-provider/context'

const theme: ThemeConfig = {
  token: {
    borderRadius: 2,
    colorPrimary: '#9f6ae0',
    colorLink: '#9f6ae0',
    fontSizeHeading2: 60
  }
}
</script>

<style lang="less">
@import 'styles/index.less';
</style>
