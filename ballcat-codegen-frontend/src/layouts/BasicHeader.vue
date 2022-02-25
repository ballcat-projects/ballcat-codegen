<template>
  <div class="header">
    <a href="http://www.ballcat.cn" target="_blank">
      <span>
        <GithubOutlined />
        Github
      </span>
    </a>
  </div>
</template>

<script setup lang="ts">
  import { GithubOutlined } from '@ant-design/icons-vue'
</script>

<style scoped>
  .header {
    float: right;
    padding-right: 20px;
    font-size: 18px;
    font-weight: 600;
  }
</style>
