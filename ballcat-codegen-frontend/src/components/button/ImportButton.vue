<template>
  <a-button class="ant-btn-import">
    <template #icon>
      <upload-outlined />
    </template>
    导入
  </a-button>
</template>

<script setup>
import { UploadOutlined } from '@ant-design/icons-vue'
</script>

<style lang="less">
.ant-btn.ant-btn-import {
  color: #fff;
  border-color: #fdcf4e;
  background: #fdcf4e;
  text-shadow: 0 -1px 0 rgb(0 0 0 / 12%);
  box-shadow: 0 2px 0 rgb(0 0 0 / 5%);
}

.ant-btn.ant-btn-import:hover,
.ant-btn.ant-btn-import:focus {
  color: #fff;
  border-color: #ffe088;
  background: #ffe088;
}
</style>
