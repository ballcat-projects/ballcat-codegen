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
    // 主色系
    colorPrimary: '#1890ff',
    colorSuccess: '#52c41a',
    colorWarning: '#faad14',
    colorError: '#ff4d4f',
    colorInfo: '#1890ff',
    
    // 圆角系统
    borderRadius: 6,
    borderRadiusLG: 8,
    borderRadiusSM: 4,
    
    // 字体系统
    fontSize: 14,
    fontSizeLG: 16,
    fontSizeSM: 12,
    fontWeightStrong: 600,
    
    // 间距系统
    paddingXS: 4,
    paddingSM: 8,
    padding: 12,
    paddingMD: 16,
    paddingLG: 24,
    paddingXL: 32,
    
    // 阴影系统
    boxShadow: '0 1px 2px rgba(0, 0, 0, 0.03), 0 1px 6px -1px rgba(0, 0, 0, 0.02), 0 2px 4px rgba(0, 0, 0, 0.02)',
    boxShadowSecondary: '0 6px 16px rgba(0, 0, 0, 0.08), 0 3px 6px -4px rgba(0, 0, 0, 0.12), 0 9px 28px 8px rgba(0, 0, 0, 0.05)',
  },
  components: {
    // 按钮组件定制
    Button: {
      borderRadius: 6,
    },
    // 卡片组件定制
    Card: {
      borderRadiusLG: 8,
    },
    // 表格组件定制
    Table: {
      borderRadiusLG: 8,
    },
  },
}
</script>

<style lang="less">
@import 'styles/index.less';
</style>
