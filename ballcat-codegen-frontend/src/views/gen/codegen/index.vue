<template>
  <div class="codegen-container">
    <a-card
      :bordered="false"
      :body-style="{ minHeight: '600px', display: 'flex', flexDirection: 'column' }"
      class="codegen-card"
    >
      <!-- 页面头部 -->
      <div class="codegen-header">
        <!-- 步骤指示器 -->
        <div class="header-steps">
          <a-steps 
            :current="currentStepNumber" 
            :items="enhancedStepInfos"
            class="compact-steps"
          />
        </div>
      </div>

      <!-- 步骤内容区域 -->
      <div class="step-content-area">
        <div class="step-content">
          <div class="step-container" :key="currentStepNumber">
            <!-- 模板组选择 -->
            <template-group-select-step
              v-if="currentStepNumber === 0"
              ref="templateGroupSelectStepRef"
            />

            <!-- 模板配置 -->
            <template-config-step 
              v-if="currentStepNumber === 1" 
              ref="templateConfigStepRef" 
            />

            <!-- 数据源选择 -->
            <table-select-step 
              v-if="currentStepNumber === 2" 
              ref="tableSelectStepRef" 
            />

            <!-- 代码生成 -->
            <generate-step 
              v-if="currentStepNumber === 3" 
              ref="generateStepRef" 
            />
          </div>
        </div>
      </div>

      <!-- 操作按钮区域 -->
      <div class="step-actions">
        <a-row type="flex" justify="space-between" align="middle">
          <a-col>
            <a-space>
              <span class="step-tip">
                步骤 {{ currentStepNumber + 1 }} / {{ stepInfos.length }}
              </span>
            </a-space>
          </a-col>
          <a-col>
            <a-space :size="12">
              <a-button 
                v-if="currentStepNumber > 0" 
                @click="prev"
                :disabled="isProcessing"
              >
                <LeftOutlined />
                上一步
              </a-button>
              <a-button 
                v-if="currentStepNumber < stepInfos.length - 2" 
                type="primary" 
                @click="next"
                :loading="isValidating"
              >
                下一步
                <RightOutlined />
              </a-button>
              <a-button 
                v-if="currentStepNumber === stepInfos.length - 2" 
                type="primary" 
                @click="next"
                :loading="isValidating"
              >
                <ThunderboltOutlined />
                代码生成
              </a-button>
              <a-button
                v-if="currentStepNumber === stepInfos.length - 1"
                type="primary"
                @click="download"
                :loading="isDownloading"
              >
                <DownloadOutlined />
                打包下载
              </a-button>
            </a-space>
          </a-col>
        </a-row>
      </div>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, nextTick } from 'vue'
import { 
  LeftOutlined, 
  RightOutlined, 
  ThunderboltOutlined, 
  DownloadOutlined 
} from '@ant-design/icons-vue'
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

interface StepInfo {
  title: string
  componentRef: GenerateStepInstance | undefined
}

// 基础步骤信息
const stepInfos = computed<StepInfo[]>(() => [
  {
    title: '模板选择',
    componentRef: templateGroupSelectStepRef.value
  },
  {
    title: '模板配置',
    componentRef: templateConfigStepRef.value
  },
  {
    title: '数据源选择',
    componentRef: tableSelectStepRef.value
  },
  {
    title: '代码生成',
    componentRef: generateStepRef.value
  }
])

// 增强的步骤信息（用于步骤指示器）
const enhancedStepInfos = computed(() => [
  {
    title: '模板选择',
    description: '选择代码生成模板组'
  },
  {
    title: '模板配置', 
    description: '配置模板参数'
  },
  {
    title: '数据源选择',
    description: '选择数据表'
  },
  {
    title: '代码生成',
    description: '生成并下载代码'
  }
])

// 下一步
const next = async () => {
  const stepInfo = stepInfos.value[currentStepNumber.value]
  
  if (stepInfo.componentRef?.validate) {
    try {
      isValidating.value = true
      await stepInfo.componentRef.validate()
      enterNext(stepInfo)
    } catch (e: any) {
      message.error(e.message || '请将当前页面选项填写完整')
    } finally {
      isValidating.value = false
    }
  } else {
    enterNext(stepInfo)
  }
}

// 上一步
const prev = () => {
  if (currentStepNumber.value > 0) {
    currentStepNumber.value--
  }
}

// 进入下一步的逻辑
function enterNext(stepInfo: StepInfo) {
  stepInfo.componentRef?.next?.()
  const nextStepNumber = currentStepNumber.value + 1
  currentStepNumber.value++
  
  // 使用 nextTick 确保组件已经渲染完成后再调用 enter 方法
  nextTick(() => {
    const nextStepInfo = stepInfos.value[nextStepNumber]
    nextStepInfo.componentRef?.enter?.()
  })
}

// 下载生成的代码
const download = async () => {
  try {
    isDownloading.value = true
    const response = await generate(generatorConfigStore.dsName, generatorConfigStore.options)
    remoteFileDownload(response, 'BallCat-CodeGen.zip')
    message.success('代码生成完成！')
  } catch (error) {
    message.error('代码生成异常')
  } finally {
    isDownloading.value = false
  }
}
</script>

<style scoped lang="less">
.codegen-container {
  padding: @spacing-lg;
}

.codegen-card {
  border-radius: @border-radius-lg;
  box-shadow: @shadow-card;
  
  :deep(.ant-card-body) {
    padding: 0;
  }
}

.codegen-header {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: @spacing-xl @spacing-xl @spacing-lg;
  border-bottom: 1px solid @border-color-light;
  background: linear-gradient(135deg, #fafbfc 0%, #f5f7fa 100%);
  
  .header-steps {
    width: 100%;
    max-width: 800px;
    
    .compact-steps {
      :deep(.ant-steps-item) {
        .ant-steps-item-title {
          font-weight: 600;
          color: @text-color-primary;
          font-size: @font-size-base;
          margin-top: 8px;
        }
        
        .ant-steps-item-description {
          color: @text-color-secondary;
          font-size: @font-size-sm;
          margin-top: 4px;
          line-height: 1.4;
        }
        
        &.ant-steps-item-active {
          .ant-steps-item-title {
            color: @primary-color;
            font-weight: 700;
          }
          
          .ant-steps-item-description {
            color: @primary-color;
          }
        }
        
        &.ant-steps-item-finish {
          .ant-steps-item-title {
            color: @success-color;
          }
        }
        
        .ant-steps-item-content {
          min-height: 60px;
        }
      }
      
      :deep(.ant-steps-item-icon) {
        width: 32px;
        height: 32px;
        line-height: 32px;
        font-size: @font-size-base;
        border-width: 2px;
        
        .ant-steps-icon {
          font-size: @font-size-base;
          font-weight: 600;
        }
      }
      
      :deep(.ant-steps-item-tail) {
        &::after {
          height: 2px;
          background: @border-color-light;
        }
      }
      
      :deep(.ant-steps-item-finish .ant-steps-item-tail::after) {
        background: @success-color;
      }
    }
  }
}

.step-content-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 500px;
  background: #fff;
  
  .step-content {
    flex: 1;
    padding: @spacing-lg @spacing-lg @spacing-lg;
    overflow-y: auto;
    
    .step-container {
      width: 100%;
      animation: fadeInUp 0.4s ease-out;
    }
  }
}

.step-actions {
  padding: @spacing-xl @spacing-xl @spacing-lg;
  border-top: 1px solid @border-color-light;
  background: linear-gradient(135deg, #fafbfc 0%, #f5f7fa 100%);
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.02);
  
  .step-tip {
    color: @text-color-secondary;
    font-size: @font-size-base;
    font-weight: 500;
    display: flex;
    align-items: center;
    
    &::before {
      content: '';
      width: 4px;
      height: 16px;
      background: @primary-color;
      border-radius: 2px;
      margin-right: 8px;
    }
  }
  
  :deep(.ant-btn) {
    height: 44px;
    padding: 0 @spacing-xl;
    font-weight: 600;
    border-radius: @border-radius-lg;
    font-size: @font-size-base;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.08);
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    
    &:hover {
      transform: translateY(-1px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    }
    
    &:active {
      transform: translateY(0);
    }
    
    &.ant-btn-primary {
      background: linear-gradient(135deg, @primary-color 0%, #40a9ff 100%);
      border: none;
      
      &:hover {
        background: linear-gradient(135deg, #40a9ff 0%, @primary-color 100%);
      }
      
      &:disabled {
        background: #f5f5f5;
        color: rgba(0, 0, 0, 0.25);
        transform: none;
        box-shadow: none;
        cursor: not-allowed;
      }
    }
    
    &:not(.ant-btn-primary) {
      background: #fff;
      border: 1px solid @border-color-base;
      color: @text-color-primary;
      
      &:hover {
        border-color: @primary-color;
        color: @primary-color;
      }
    }
    
    .anticon {
      font-size: @font-size-base;
      margin-right: 4px;
      
      &:last-child {
        margin-right: 0;
        margin-left: 4px;
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .codegen-container {
    padding: @spacing-md;
  }
  
  .codegen-header {
    padding: @spacing-sm @spacing-md;
    
    .header-steps {
      max-width: 100%;
      
      .compact-steps {
        :deep(.ant-steps-item-title) {
          font-size: @font-size-xs;
        }
      }
    }
  }
  
  .step-content-area .step-content {
    padding: @spacing-md @spacing-lg;
  }
  
  .step-actions {
    padding: @spacing-md;
    
    :deep(.ant-row) {
      flex-direction: column;
      gap: @spacing-md;
    }
  }
}

// 动画效果
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.step-content {
  transition: all 0.3s ease;
}

// Loading 状态样式
:deep(.ant-btn-loading) {
  pointer-events: none;
}

// 步骤指示器增强样式
.codegen-header .header-steps .compact-steps {
  :deep(.ant-steps-item-process) {
    .ant-steps-item-icon {
      background: @primary-color;
      border-color: @primary-color;
      
      .ant-steps-icon {
        color: #fff;
      }
    }
  }
  
  :deep(.ant-steps-item-finish) {
    .ant-steps-item-icon {
      background: @success-color;
      border-color: @success-color;
      
      .anticon {
        color: #fff;
      }
    }
  }
  
  :deep(.ant-steps-item-wait) {
    .ant-steps-item-icon {
      background: #fff;
      border-color: @border-color-base;
      
      .ant-steps-icon {
        color: @text-color-secondary;
      }
    }
  }
}
</style>
