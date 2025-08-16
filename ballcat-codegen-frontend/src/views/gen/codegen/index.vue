<template>
  <div
    class="codegen-page flex flex-col min-h-0">
  <div class="flex-1 flex flex-col min-h-0">

  <!-- Breadcrumb outside the card -->
  <PageBreadcrumb />

      <!-- Main Content Card -->
      <div
        class="codegen-card bg-white/80 backdrop-blur-sm rounded-2xl shadow-xl border border-white/20 overflow-hidden flex-1 flex flex-col min-h-0">
  <!-- Steps Bar -->
  <div class="card-toolbar steps-bar contrast" :class="[isCompact ? 'p-3 dot-mode' : 'p-5']">
          <div class="steps-scroll overflow-x-auto w-full">
            <a-steps :current="currentStepNumber" :items="enhancedStepInfos" class="steps-ghost" :size="isCompact ? 'small' : 'default'" :progressDot="isCompact" />
          </div>
        </div>

        <!-- Content Area -->
  <div class="page-content flex-1 min-h-0">
          <div class="h-full">
            <div class="step-container h-full" :key="currentStepNumber">
              <!-- 模板组选择 -->
              <template-group-select-step v-if="currentStepNumber === 0" ref="templateGroupSelectStepRef" />

              <!-- 模板配置 -->
              <template-config-step v-if="currentStepNumber === 1" ref="templateConfigStepRef" />

              <!-- 数据源选择 -->
              <table-select-step v-if="currentStepNumber === 2" ref="tableSelectStepRef" />

              <!-- 代码生成 -->
              <generate-step v-if="currentStepNumber === 3" ref="generateStepRef" />
            </div>
          </div>
        </div>

    <!-- Action Bar -->
  <div class="page-action-bar bg-gradient-to-r from-gray-50 to-slate-50 border-t border-gray-100 flex-shrink-0 sticky bottom-0 z-10" :class="[isCompact ? 'p-3' : 'p-5']">
          <div>
            <div class="flex items-center justify-between">
              <div class="flex items-center space-x-4">
                <div class="flex items-center space-x-2">
                  <div class="w-3 h-3 bg-gradient-to-r from-blue-500 to-indigo-500 rounded-full shadow-sm"></div>
                  <span class="text-sm font-semibold text-gray-800">
                    步骤 {{ currentStepNumber + 1 }} / {{ stepInfos.length }}
                  </span>
                </div>
                <div class="h-4 w-px bg-gray-300"></div>
                <span class="text-sm text-gray-600 font-medium">{{ enhancedStepInfos[currentStepNumber]?.title }}</span>
              </div>

              <div class="flex items-center space-x-4">
                <button v-if="currentStepNumber > 0" @click="prev" :disabled="isProcessing"
                  class="group relative flex items-center text-gray-700 bg-white border border-gray-200 rounded-xl hover:bg-gray-50 hover:border-gray-300 hover:shadow-md transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
                  :class="[isCompact ? 'px-4 py-2' : 'px-5 py-2.5']">
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none"
                    stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
                    class="mr-2 group-hover:-translate-x-0.5 transition-transform">
                    <path d="M19 12H5"></path>
                    <path d="M12 19l-7-7 7-7"></path>
                  </svg>
                  上一步
                </button>

                <button v-if="currentStepNumber < stepInfos.length - 2" @click="next" :disabled="isValidating"
                  class="group relative flex items-center bg-gradient-to-r from-blue-600 to-indigo-600 text-white rounded-xl hover:from-blue-700 hover:to-indigo-700 hover:shadow-lg transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
                  :class="[isCompact ? 'px-4 py-2' : 'px-6 py-2.5']">
                  <span v-if="!isValidating">下一步</span>
                  <span v-else>验证中...</span>
                  <svg v-if="!isValidating" xmlns="http://www.w3.org/2000/svg" width="16" height="16"
                    viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"
                    stroke-linejoin="round" class="ml-2 group-hover:translate-x-0.5 transition-transform">
                    <path d="M5 12h14"></path>
                    <path d="M12 5l7 7-7 7"></path>
                  </svg>
                </button>

                <button v-if="currentStepNumber === stepInfos.length - 2" @click="next" :disabled="isValidating"
                  class="group relative flex items-center bg-gradient-to-r from-green-600 to-emerald-600 text-white rounded-xl hover:from-green-700 hover:to-emerald-700 hover:shadow-lg transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
                  :class="[isCompact ? 'px-4 py-2' : 'px-6 py-2.5']">
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none"
                    stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
                    class="mr-2 group-hover:scale-110 transition-transform">
                    <polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"></polygon>
                  </svg>
                  <span v-if="!isValidating">开始生成</span>
                  <span v-else>验证中...</span>
                </button>

        <button v-if="currentStepNumber === stepInfos.length - 1" @click="download" :disabled="isDownloading"
                  class="group relative flex items-center bg-gradient-to-r from-blue-600 to-indigo-600 text-white rounded-xl hover:from-blue-700 hover:to-indigo-700 hover:shadow-lg transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
                  :class="[isCompact ? 'px-4 py-2' : 'px-6 py-2.5']">
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none"
                    stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
          class="mr-2 transition-transform">
                    <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path>
                    <polyline points="7,10 12,15 17,10"></polyline>
                    <line x1="12" y1="15" x2="12" y2="3"></line>
                  </svg>
                  <span v-if="!isDownloading">打包下载</span>
                  <span v-else>下载中...</span>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, nextTick, watch, onMounted, onBeforeUnmount } from 'vue'
import PageBreadcrumb from '@/components/breadcrumb/PageBreadcrumb.vue'
import TemplateConfigStep from './TemplateConfigStep.vue'
import type { GenerateStepInstance } from '@/views/gen/codegen/types'
import TableSelectStep from '@/views/gen/codegen/TableSelectStep.vue'
import GenerateStep from '@/views/gen/codegen/GenerateStep.vue'
import { useGeneratorConfigStore } from '@/store'
import TemplateGroupSelectStep from '@/views/gen/codegen/TemplateGroupSelectStep.vue'
import { message } from 'ant-design-vue'
import { generate } from '@/api/gen/generate'
import { remoteFileDownload } from '@/utils/file-util'

// 进页面的时候先重置下，代码生成的配置
const generatorConfigStore = useGeneratorConfigStore()
generatorConfigStore.$reset()

// 组件引用
const templateGroupSelectStepRef = ref<GenerateStepInstance>()
const templateConfigStepRef = ref<GenerateStepInstance>()
const tableSelectStepRef = ref<GenerateStepInstance>()
const generateStepRef = ref<GenerateStepInstance>()

// 状态管理
const currentStepNumber = ref<number>(0)
const isValidating = ref<boolean>(false)
const isDownloading = ref<boolean>(false)
const isProcessing = ref<boolean>(false)
const isCompact = ref<boolean>(false)

const applyCompact = () => {
  isCompact.value = window.innerHeight <= 768
}

// 步骤信息
const stepInfos = [
  {
    title: '选择模板组',
    description: '选择代码生成的模板组',
  },
  {
    title: '模板配置',
    description: '配置模板的基础信息',
  },
  {
    title: '选择数据源',
    description: '选择生成代码的数据表',
  },
  {
    title: '生成代码',
    description: '查看生成结果并下载',
  },
]

// 计算属性：增强的步骤信息
const enhancedStepInfos = computed(() => {
  return stepInfos.map((step, index) => ({
    ...step,
    status: index < currentStepNumber.value ? 'finish' : index === currentStepNumber.value ? 'process' : 'wait'
  }))
})

// 监听步骤变化，确保组件准备就绪后调用 enter 方法
watch(currentStepNumber, async (newStep, oldStep) => {
  if (newStep > oldStep) {
    // 等待组件渲染完成
    await nextTick()
    await nextTick()

    const refMap = {
      0: templateGroupSelectStepRef.value,
      1: templateConfigStepRef.value,
      2: tableSelectStepRef.value,
      3: generateStepRef.value,
    }

    const currentRef = refMap[newStep as keyof typeof refMap]
    console.log(`Step ${newStep} ref:`, currentRef)

    if (currentRef?.enter) {
      console.log(`Calling enter method for step ${newStep}`)
      currentRef.enter()
    }
  }
}, { flush: 'post' })

onMounted(() => {
  applyCompact()
  window.addEventListener('resize', applyCompact)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', applyCompact)
})

// 验证当前步骤
const validate = async (): Promise<boolean> => {
  const refMap = {
    0: templateGroupSelectStepRef.value,
    1: templateConfigStepRef.value,
    2: tableSelectStepRef.value,
    3: generateStepRef.value,
  }

  const currentRef = refMap[currentStepNumber.value as keyof typeof refMap]

  if (currentRef?.validate) {
    try {
      await currentRef.validate()
      return true
    } catch (error: any) {
      console.error('Validation error:', error)
      // 如果错误对象有 message 属性，显示具体错误信息
      if (error?.message) {
        message.error(error.message)
      } else {
        message.error('请完成当前步骤的必填项')
      }
      return false
    }
  }

  return true
}

// 下一步
const next = async () => {
  isValidating.value = true

  try {
    const isValid = await validate()

    if (!isValid) {
      return
    }

    // 调用当前步骤的 next 方法
    const refMap = {
      0: templateGroupSelectStepRef.value,
      1: templateConfigStepRef.value,
      2: tableSelectStepRef.value,
      3: generateStepRef.value,
    }

    const currentRef = refMap[currentStepNumber.value as keyof typeof refMap]
    if (currentRef?.next) {
      currentRef.next()
    }

    if (currentStepNumber.value < stepInfos.length - 1) {
      currentStepNumber.value++
      // watcher 会自动调用新步骤的 enter 方法
    }
  } catch (error) {
    console.error('Next step error:', error)
    message.error('步骤验证失败，请重试')
  } finally {
    isValidating.value = false
  }
}

// 上一步
const prev = async () => {
  if (currentStepNumber.value > 0) {
    currentStepNumber.value--
    // watcher 会自动调用新步骤的 enter 方法
  }
}

// 下载生成的代码
const download = async () => {
  isDownloading.value = true

  try {
    const response = await generate(generatorConfigStore.dsName, generatorConfigStore.options)

    // 创建下载链接
    const blob = new Blob([response.data])
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = 'generated-code.zip'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)

    message.success('代码生成成功')
  } catch (error) {
    console.error('Download error:', error)
    message.error('代码下载失败，请重试')
  } finally {
    isDownloading.value = false
  }
}
</script>

<style scoped lang="less">
/* Elegant Steps 样式改为全局复用（见 components.less 的 .elegant-steps） */

/* Step Container 动画增强 */
.step-container {
  animation: elegantFadeIn 0.6s cubic-bezier(0.4, 0, 0.2, 1);
}

@keyframes elegantFadeIn {
  from {
    opacity: 0;
    transform: translateY(24px) scale(0.98);
  }

  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

/* 按钮hover效果增强 */
button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

button:active:not(:disabled) {
  transform: translateY(-1px);
  transition-duration: 0.1s;
}

/* 加载状态 */
button:disabled {
  transform: none !important;
  box-shadow: none !important;
}

/* 背景装饰 */
.min-h-screen::before {
  content: '';
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background:
  radial-gradient(circle at 20% 80%, fade(@blue-500, 10%) 0%, transparent 50%),
  radial-gradient(circle at 80% 20%, rgba(99, 102, 241, 0.1) 0%, transparent 50%),
  radial-gradient(circle at 40% 40%, fade(@green-600, 5%) 0%, transparent 50%);
  pointer-events: none;
  z-index: -1;
}

/* 卡片悬浮效果 */
.bg-white\/80 {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.bg-white\/80:hover {
  transform: translateY(-1px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
}

/* Steps 横向滚动容器最小宽度以避免换行增高 */
.steps-scroll :deep(.ant-steps) {
  min-width: max-content;
}

/* 小屏紧凑样式（辅助，主要由动态类控制） */
@media (max-height: 768px) {
  .logo-badge {
    width: 32px !important;
    height: 32px !important;
    margin-right: 8px !important;
  }
  .header-title {
    font-size: 1rem !important;
  }
  .header-subtitle {
    display: none !important;
  }
}

/* Contrast Header + card min-height + icon alignment */
.codegen-card {
  /* min height strategy: clamp + header var + footer reserve */
  min-height: clamp(640px, calc(100vh - var(--header-h, 64px) - 140px), 88vh);
}

.steps-bar.contrast {
  background: #f7faff; /* blue-50 like */
  border-bottom: 1px solid #dbeafe; /* blue-100 */
  /* subtle inner separation */
  box-shadow: inset 0 -1px 0 rgba(30, 58, 138, 0.04);
}
/* dot mode safe padding (isCompact => dot) */
.steps-bar.dot-mode { padding-top: 14px !important; } /* +2px over ~p-3 */

/* Fix dot mode vertical clipping in steps-ghost */
:deep(.steps-ghost) {
  .ant-steps-item-icon { margin-top: 2px; }
  .ant-steps-item-tail::after { top: 16px; }
}

/* Icon baseline alignment in action buttons */
.page-action-bar svg { display: block; }
.page-action-bar button { line-height: 1; }
</style>
