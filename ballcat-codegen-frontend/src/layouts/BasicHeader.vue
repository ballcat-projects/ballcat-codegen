<template>
  <div :class="['ballcat-top-nav-header', theme]">
    <div class="ballcat-top-nav-header-main">
      <div class="ballcat-top-nav-header-main-left">
        <div class="ballcat-top-nav-header-logo">
          <a>
            <img src="../assets/logo.png" alt="logo" />
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
            <svg class="gitee-icon" viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg" width="14" height="14">
              <path d="M512 1024C230.4 1024 0 793.6 0 512S230.4 0 512 0s512 230.4 512 512-230.4 512-512 512z m259.2-569.6H480c-12.8 0-25.6 12.8-25.6 25.6v64c0 12.8 12.8 25.6 25.6 25.6h176c12.8 0 25.6 12.8 25.6 25.6v12.8c0 41.6-35.2 76.8-76.8 76.8h-240c-12.8 0-25.6-12.8-25.6-25.6V416c0-41.6 35.2-76.8 76.8-76.8h355.2c12.8 0 25.6-12.8 25.6-25.6v-64c0-12.8-12.8-25.6-25.6-25.6H416c-105.6 0-188.8 86.4-188.8 188.8V768c0 12.8 12.8 25.6 25.6 25.6h374.4c92.8 0 169.6-76.8 169.6-169.6v-144c0-12.8-12.8-25.6-25.6-25.6z" fill="currentColor"/>
            </svg>
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
    background-color: #fff;
    .@{top-nav-header-prefix-cls}-logo {
      h1 {
        color: @primary-color;
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

  &-left {
    display: flex;
    min-width: 192px;
  }

  .anticon {
    color: @primary-color;
  }

  &-logo {
    position: relative;
    min-width: 200px;
    height: 100%;
    overflow: hidden;

    img,
    a > svg {
      display: inline-block;
      height: 32px;
      vertical-align: middle;
      margin: 0 6px 4px 0;
    }

    h1 {
      display: inline-block;
      margin: 0 0 0 12px;
      color: #fff;
      font-size: 20px;
      vertical-align: top;
      font-weight: 600;
      letter-spacing: 0.5px;
    }
  }

  &-menu {
    min-width: 0;
    .@{ant-prefix}-menu.@{ant-prefix}-menu-horizontal {
      height: 100%;
      border: none;
    }
  }

  &-right-content {
    display: flex;
    float: right;
    height: 64px;
    margin-left: auto;
    overflow: hidden;
    .action {
      color: @primary-color;
      display: flex;
      align-items: center;
      height: 64px;
      padding: 0 16px;
      cursor: pointer;
      transition: all 0.3s;
      border-radius: 6px;
      margin: 0 4px;
      
      .gitee-icon {
        width: 14px;
        height: 14px;
        fill: currentColor;
      }
      
      > span {
        vertical-align: middle;
        font-weight: 500;
        padding-left: 6px;
        font-size: 14px;
      }
      
      &:hover {
        background: @pro-header-hover-bg;
        transform: translateY(-1px);
      }
      
      &:global(.opened) {
        background: @pro-header-hover-bg;
      }
    }
  }
}
</style>
