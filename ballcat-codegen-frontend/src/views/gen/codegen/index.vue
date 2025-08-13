<template>
  <div
    class="h-[calc(100vh-200px)] bg-gradient-to-br from-slate-50 via-blue-50 to-indigo-50 flex flex-col overflow-hidden">
    <div class="flex-1 flex flex-col min-h-0">
      <!-- Compact Header -->
      <div class="relative mb-6">
        <div class="bg-white/80 backdrop-blur-sm rounded-xl shadow-lg border border-white/20 p-4">
          <div class="flex items-center">
            <div
              class="inline-flex items-center justify-center w-10 h-10 bg-gradient-to-r from-blue-600 to-indigo-600 rounded-lg mr-3 shadow-md">
              <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none"
                stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
                class="text-white">
                <polyline points="16 18 22 12 16 6"></polyline>
                <polyline points="8 6 2 12 8 18"></polyline>
              </svg>
            </div>
            <div>
              <h1 class="text-xl font-bold bg-gradient-to-r from-gray-900 to-gray-700 bg-clip-text text-transparent">
                代码生成器
              </h1>
              <p class="text-gray-600 text-sm">选择模板，配置参数，一键生成项目代码</p>
            </div>
          </div>
        </div>
      </div>

      <!-- Main Content Card -->
      <div
        class="bg-white/80 backdrop-blur-sm rounded-2xl shadow-xl border border-white/20 overflow-hidden flex-1 flex flex-col min-h-0">
        <!-- Steps Navigation -->
        <div class="bg-gradient-to-r from-blue-50 to-indigo-50 p-6 border-b border-blue-100">
          <div>
            <a-steps :current="currentStepNumber" :items="enhancedStepInfos" class="elegant-steps" />
          </div>
        </div>

        <!-- Content Area -->
        <div class="p-6 flex-1 overflow-y-auto min-h-0">
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
        <div class="bg-gradient-to-r from-gray-50 to-slate-50 border-t border-gray-100 p-6 flex-shrink-0">
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
                  class="group relative flex items-center px-5 py-2.5 text-gray-700 bg-white border border-gray-200 rounded-xl hover:bg-gray-50 hover:border-gray-300 hover:shadow-md transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed">
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none"
                    stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
                    class="mr-2 group-hover:-translate-x-0.5 transition-transform">
                    <path d="M19 12H5"></path>
                    <path d="M12 19l-7-7 7-7"></path>
                  </svg>
                  上一步
                </button>

                <button v-if="currentStepNumber < stepInfos.length - 2" @click="next" :disabled="isValidating"
                  class="group relative flex items-center px-6 py-2.5 bg-gradient-to-r from-blue-600 to-indigo-600 text-white rounded-xl hover:from-blue-700 hover:to-indigo-700 hover:shadow-lg transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed">
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
                  class="group relative flex items-center px-6 py-2.5 bg-gradient-to-r from-green-600 to-emerald-600 text-white rounded-xl hover:from-green-700 hover:to-emerald-700 hover:shadow-lg transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed">
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none"
                    stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
                    class="mr-2 group-hover:scale-110 transition-transform">
                    <polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"></polygon>
                  </svg>
                  <span v-if="!isValidating">开始生成</span>
                  <span v-else>验证中...</span>
                </button>

                <button v-if="currentStepNumber === stepInfos.length - 1" @click="download" :disabled="isDownloading"
                  class="group relative flex items-center px-6 py-2.5 bg-gradient-to-r from-blue-600 to-indigo-600 text-white rounded-xl hover:from-blue-700 hover:to-indigo-700 hover:shadow-lg transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed">
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none"
                    stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
                    class="mr-2 group-hover:translate-y-0.5 transition-transform">
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
import { computed, ref, nextTick, watch } from 'vue'
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

<style scoped>
/* Elegant Steps 样式 */
.elegant-steps :deep(.ant-steps-item) {
  .ant-steps-item-title {
    font-weight: 600;
    color: #1f2937;
    font-size: 15px;
    margin-top: 8px;
    line-height: 1.4;
  }

  .ant-steps-item-description {
    color: #6b7280;
    font-size: 13px;
    margin-top: 4px;
    line-height: 1.5;
  }

  &.ant-steps-item-active {
    .ant-steps-item-title {
      background: linear-gradient(135deg, #3b82f6 0%, #6366f1 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
      font-weight: 700;
    }

    .ant-steps-item-description {
      color: #3b82f6;
      font-weight: 500;
    }
  }

  &.ant-steps-item-finish {
    .ant-steps-item-title {
      color: #059669;
      font-weight: 600;
    }

    .ant-steps-item-description {
      color: #10b981;
    }
  }

  .ant-steps-item-content {
    min-height: 60px;
    padding-top: 6px;
  }
}

.elegant-steps :deep(.ant-steps-item-icon) {
  width: 36px;
  height: 36px;
  line-height: 36px;
  font-size: 14px;
  border-width: 2px;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);

  .ant-steps-icon {
    font-size: 14px;
    font-weight: 700;
  }
}

.elegant-steps :deep(.ant-steps-item-tail) {
  &::after {
    height: 3px;
    background: linear-gradient(90deg, #e5e7eb 0%, #d1d5db 100%);
    border-radius: 2px;
    top: 16px;
  }
}

.elegant-steps :deep(.ant-steps-item-finish .ant-steps-item-tail::after) {
  background: linear-gradient(90deg, #10b981 0%, #059669 100%);
}

.elegant-steps :deep(.ant-steps-item-process .ant-steps-item-icon) {
  background: linear-gradient(135deg, #3b82f6 0%, #6366f1 100%);
  border-color: #3b82f6;
  transform: scale(1.05);
  box-shadow: 0 4px 16px rgba(59, 130, 246, 0.3);

  .ant-steps-icon {
    color: white;
  }
}

.elegant-steps :deep(.ant-steps-item-finish .ant-steps-item-icon) {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  border-color: #10b981;
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);

  .anticon {
    color: white;
  }
}

.elegant-steps :deep(.ant-steps-item-wait .ant-steps-item-icon) {
  background: linear-gradient(135deg, #f9fafb 0%, #f3f4f6 100%);
  border-color: #d1d5db;

  .ant-steps-icon {
    color: #9ca3af;
  }
}

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
    radial-gradient(circle at 20% 80%, rgba(59, 130, 246, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, rgba(99, 102, 241, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 40% 40%, rgba(16, 185, 129, 0.05) 0%, transparent 50%);
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
</style>
