<template>
  <div :class="['ballcat-top-nav-header', theme]">
    <div class="ballcat-top-nav-header-main">
      <div class="ballcat-top-nav-header-main-left">
        <div class="ballcat-top-nav-header-logo">
          <a>
            <img src="../assets/logo.svg" alt="logo" />
            <h1>Ballcat Generator</h1>
          </a>
        </div>
      </div>
      <div :style="{ flex: 1 }" class="ballcat-top-nav-header-menu">
        <router-menu mode="horizontal" />
      </div>

      <div class="ballcat-top-nav-header-right-content" style="padding-right: 6px">
        <a-space class="right" :size="0">
          <span class="action" @click="jumpToDoc">
            <QuestionCircleOutlined />
            <span>文档</span>
          </span>
          <span class="action" @click="jumpToGithub">
            <GithubOutlined />
            <span>Github</span>
          </span>
          <span class="action" @click="jumpToGitee">
            <img src="../assets/gitee.svg" alt="gitee" />
            <span>Gitee</span>
          </span>
        </a-space>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  import RouterMenu from '@/components/menu'
  import { GithubOutlined, QuestionCircleOutlined } from '@ant-design/icons-vue'

  defineProps<{
    theme: 'dark' | 'light'
  }>()

  const jumpToGithub = () => {
    window.open('https://github.com/ballcat-projects/ballcat-codegen')
  }

  const jumpToGitee = () => {
    window.open('https://gitee.com/ballcat-projects/ballcat-codegen')
  }

  const jumpToDoc = () => {
    window.open('http://www.ballcat.cn/codegen/#%E5%89%8D%E8%A8%80')
  }
</script>

<style scoped lang="less">
  @import (reference) 'ant-design-vue/es/style/themes/index.less';
  @ant-prefix: ~'ballcat';
  @top-nav-header-prefix-cls: ~'@{ant-prefix}-top-nav-header';
  @pro-header-hover-bg: rgba(0, 0, 0, 0.025);

  .@{top-nav-header-prefix-cls} {
    position: relative;
    width: 100%;
    height: 100%;
    box-shadow: 0 1px 4px 0 rgba(0, 21, 41, 0.12);
    transition: background 0.3s, width 0.2s;

    .@{ant-prefix}-menu {
      background: transparent;
    }

    &.light {
      background-color: @component-background;
      .@{top-nav-header-prefix-cls}-logo {
        h1 {
          color: #9f6ae0;
        }
      }
      .anticon {
        color: inherit;
      }
    }

    &-main {
      display: flex;
      height: 100%;
      padding-left: 16px;
      &-left {
        display: flex;
        min-width: 208px;
      }
    }

    .anticon {
      color: #9f6ae0;
    }

    &-logo {
      position: relative;
      min-width: 165px;
      height: 100%;
      overflow: hidden;

      img,
      a > svg {
        display: inline-block;
        height: 32px;
        vertical-align: middle;
        margin-bottom: 4px;
      }

      h1 {
        display: inline-block;
        margin: 0 0 0 8px;
        color: @btn-primary-color;
        font-size: 18px;
        vertical-align: top;
      }
    }

    &-menu {
      min-width: 0;
      .@{ant-prefix}-menu.@{ant-prefix}-menu-horizontal {
        height: 100%;
        border: none;
      }
    }
  }

  .right {
    display: flex;
    float: right;
    height: 48px;
    margin-left: auto;
    overflow: hidden;
    .action {
      color: #9f6ae0;
      display: flex;
      align-items: center;
      height: 48px;
      padding: 0 12px;
      cursor: pointer;
      transition: all 0.3s;
      > span {
        vertical-align: middle;
        font-weight: 600;
        padding-left: 5px;
      }
      &:hover {
        background: @pro-header-hover-bg;
      }
      &:global(.opened) {
        background: @pro-header-hover-bg;
      }
    }
  }
</style>
