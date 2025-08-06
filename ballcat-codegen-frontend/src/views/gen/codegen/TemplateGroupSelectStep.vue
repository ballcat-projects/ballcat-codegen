<template>
  <div class="template-group-select">
    <!-- 加载状态 -->
    <template v-if="!initialized">
      <div class="loading-container">
        <a-row type="flex" justify="center" :gutter="[20, 20]">
          <a-col v-for="item in [1, 2, 3, 4]" :key="item" :xs="24" :sm="12" :md="8" :lg="6">
            <a-card class="template-card skeleton-card" :body-style="{ padding: '16px' }">
              <a-skeleton :avatar="{ size: 'large' }" :paragraph="{ rows: 2 }" active />
            </a-card>
          </a-col>
        </a-row>
      </div>
    </template>

    <!-- 已初始化状态 -->
    <template v-if="initialized">
      <!-- 无模板组状态 -->
      <div v-show="!hasTemplateGroup" class="empty-state">
        <a-result
          status="warning"
          title="暂无可用模板组"
          sub-title="请先创建模板组。在项目的 /template 文件夹下有预置的模板 SQL 文件，可以选择需要的模板进行初始化"
        >
          <template #extra>
            <a-space>
              <a-button type="primary" @click="goToTemplateGroupPage">
                <PlusOutlined />
                创建模板组
              </a-button>
              <a-button @click="refreshTemplateGroups">
                <ReloadOutlined />
                刷新列表
              </a-button>
            </a-space>
          </template>
        </a-result>
      </div>

      <!-- 模板组选择 -->
      <div v-show="hasTemplateGroup" class="template-selection">
        <!-- 搜索和筛选区 -->
        <div class="selection-header">
          <div class="header-info">
            <h3>选择模板组</h3>
            <p>选择一个适合的模板组来生成代码，每个模板组包含一套完整的代码模板</p>
          </div>
          <div class="header-actions">
            <a-space>
              <a-input-search
                v-model:value="searchKeyword"
                placeholder="搜索模板组..."
                style="width: 200px"
                @search="handleSearch"
              />
              <a-button @click="refreshTemplateGroups">
                <ReloadOutlined />
                刷新
              </a-button>
            </a-space>
          </div>
        </div>

        <!-- 模板组网格 -->
        <div class="template-grid">
          <a-row :gutter="[20, 20]">
            <a-col 
              v-for="item in filteredTemplateGroups" 
              :key="item.value"
              :xs="24" 
              :sm="12" 
              :md="8" 
              :lg="6"
            >
              <a-card
                hoverable
                :class="[
                  'modern-template-card',
                  { 'selected': item.value === templateGroupKey }
                ]"
                @click="selectGroup(item)"
                :body-style="{ padding: 0 }"
              >
                <div class="card-wrapper">
                  <!-- 头部区域：图标 + 选中状态 -->
                  <div class="card-header">
                    <a-avatar 
                      :size="48" 
                      :src="item.attributes?.icon"
                      :style="{ backgroundColor: item.attributes?.color || '#3b82f6' }"
                      class="template-icon"
                    >
                      <template #icon>
                        <AppstoreOutlined />
                      </template>
                    </a-avatar>
                    
                    <!-- 选中指示器 -->
                    <div v-if="item.value === templateGroupKey" class="selected-indicator">
                      <CheckCircleFilled />
                    </div>
                  </div>
                  
                  <!-- 内容区域 -->
                  <div class="card-content">
                    <h3 class="template-title" :title="item.name">
                      {{ item.name }}
                    </h3>
                    
                    <div class="description-container">
                      <p class="template-description" :title="item.attributes?.remarks || '暂无描述'">
                        {{ item.attributes?.remarks || '暂无描述' }}
                      </p>
                    </div>
                  </div>
                  
                  <!-- 底部功能区 -->
                  <div class="card-footer">
                    <div class="feature-badge">
                      <span v-if="item.attributes?.useTable" class="badge-text">
                        <DatabaseOutlined />
                        需要数据源
                      </span>
                      <span v-else class="badge-text">
                        <CodeOutlined />
                        通用模板
                      </span>
                    </div>
                  </div>
                </div>
              </a-card>
            </a-col>
          </a-row>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { 
  PlusOutlined, 
  ReloadOutlined, 
  AppstoreOutlined, 
  DatabaseOutlined, 
  CodeOutlined, 
  CheckCircleFilled 
} from '@ant-design/icons-vue'
import type { SelectData } from '@/api/types'
import { doRequest } from '@/utils/axios/request'
import { listSelectData } from '@/api/gen/template-group'
import { useGeneratorConfigStore } from '@/store'
import type { GenerateStepInstance } from '@/views/gen/codegen/types'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'

// 定义模板组属性类型
interface TemplateGroupAttributes {
  remarks?: string
  icon?: string
  color?: string
  useTable?: boolean
}

const initialized = ref(false)
const generatorConfigStore = useGeneratorConfigStore()
const router = useRouter()

// 搜索功能
const searchKeyword = ref<string>('')

// 当前选中的模板组 key
const templateGroupKey = ref<string>()

// 模板组数据
const templateGroupSelectData = ref<SelectData<TemplateGroupAttributes>[]>([])

// 计算属性
const hasTemplateGroup = computed(
  () => templateGroupSelectData.value && templateGroupSelectData.value.length > 0
)

// 筛选后的模板组
const filteredTemplateGroups = computed(() => {
  if (!searchKeyword.value) {
    return templateGroupSelectData.value
  }
  return templateGroupSelectData.value.filter(item => 
    item.name.toLowerCase().includes(searchKeyword.value.toLowerCase()) ||
    (item.attributes?.remarks && item.attributes.remarks.toLowerCase().includes(searchKeyword.value.toLowerCase()))
  )
})

// 选择模板组
function selectGroup(groupItem: SelectData<TemplateGroupAttributes>) {
  templateGroupKey.value = groupItem.value
  generatorConfigStore.useTable = groupItem.attributes?.useTable ? 1 : 0
}

// 加载模板组数据
const loadTemplateGroups = () => {
  initialized.value = false
  doRequest({
    request: listSelectData(),
    onSuccess: res => {
      initialized.value = true
      const data = res.data as SelectData<TemplateGroupAttributes>[]
      templateGroupSelectData.value = data
      if (data && data.length > 0) {
        // 如果之前有选中的，保持选中状态
        const currentSelected = data.find(item => item.value === templateGroupKey.value)
        if (!currentSelected) {
          selectGroup(data[0])
        }
      }
    },
    onError: () => {
      initialized.value = true
      message.error('加载模板组失败')
    }
  })
}

// 刷新模板组
const refreshTemplateGroups = () => {
  loadTemplateGroups()
}

// 搜索处理
const handleSearch = (value: string) => {
  searchKeyword.value = value
}

// 跳转到模板组管理页面
const goToTemplateGroupPage = () => {
  router.push({ name: 'TemplateGroup' })
}

// 初始加载
loadTemplateGroups()

// 组件实例方法
defineExpose<GenerateStepInstance>({
  validate: () => {
    return templateGroupKey.value
      ? Promise.resolve()
      : Promise.reject({ message: '请选择一个模板组' })
  },
  next: () => {
    generatorConfigStore.options.templateGroupKey = templateGroupKey.value
  }
})
</script>

<script lang="ts">
export default {
  name: 'TemplateGroupSelectStep'
}
</script>

<style scoped lang="less">
.template-group-select {
  padding: @spacing-lg 0;
}

.loading-container {
  .skeleton-card {
    min-height: 200px;
  }
}

.empty-state {
  padding: @spacing-xxl 0;
}

.template-selection {
  .selection-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: @spacing-xl;
    padding-bottom: @spacing-lg;
    border-bottom: 1px solid @border-color-light;
    
    .header-info {
      h3 {
        margin: 0 0 @spacing-xs;
        color: @text-color-primary;
        font-size: @font-size-lg;
        font-weight: 600;
      }
      
      p {
        margin: 0;
        color: @text-color-secondary;
        font-size: @font-size-md;
      }
    }
    
    .header-actions {
      flex-shrink: 0;
    }
  }
  
  .template-grid {
    margin-bottom: @spacing-xl;
  }
}

/* 现代化模板卡片设计 - 紧凑版 */
.modern-template-card {
  position: relative;
  height: 200px; /* 增加高度 */
  border-radius: 12px;
  border: 2px solid transparent;
  background: #ffffff;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  
  &:hover {
    transform: translateY(-6px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
    border-color: #e0e7ff;
  }
  
  &.selected {
    border-color: #3b82f6;
    background: linear-gradient(135deg, rgba(59, 130, 246, 0.03) 0%, rgba(99, 102, 241, 0.03) 100%);
    box-shadow: 0 4px 16px rgba(59, 130, 246, 0.15);
    
    .card-header .template-icon {
      transform: scale(1.05);
      box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
    }
    
    .selected-indicator {
      opacity: 1;
    }
  }
  
  .card-wrapper {
    height: 100%;
    display: flex;
    flex-direction: column;
    transition: all 0.3s ease;
    position: relative;
    z-index: 2;
  }
  
  /* 头部区域 - 图标和选中状态 */
  .card-header {
    position: relative;
    padding: 20px 16px 12px;
    display: flex;
    justify-content: center;
    align-items: center;
    flex: 0 0 auto;
    
    .template-icon {
      border: 3px solid rgba(255, 255, 255, 0.9);
      box-shadow: 0 3px 8px rgba(0, 0, 0, 0.1);
      transition: all 0.3s ease;
    }
    
    .selected-indicator {
      position: absolute;
      top: 12px;
      right: 12px;
      color: #3b82f6;
      font-size: 18px;
      opacity: 0;
      transition: all 0.3s ease;
      background: #ffffff;
      border-radius: 50%;
      width: 24px;
      height: 24px;
      display: flex;
      align-items: center;
      justify-content: center;
      box-shadow: 0 2px 6px rgba(0, 0, 0, 0.15);
    }
  }
  
  /* 内容区域 */
  .card-content {
    flex: 1;
    padding: 0 16px;
    display: flex;
    flex-direction: column;
    min-height: 0; /* 允许子元素收缩 */
    
    .template-title {
      font-size: 15px;
      font-weight: 700;
      color: #1f2937;
      margin: 0 0 10px 0;
      line-height: 1.3;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
    
    .description-container {
      position: relative;
      flex: 1;
      overflow: hidden;
      
      .template-description {
        font-size: 13px;
        color: #6b7280;
        line-height: 1.5;
        margin: 0;
        overflow: hidden;
        display: -webkit-box;
        -webkit-line-clamp: 3;
        line-clamp: 3;
        -webkit-box-orient: vertical;
        word-break: break-word;
      }
    }
  }
  
  /* 底部功能区 */
  .card-footer {
    padding: 12px 16px 16px;
    flex: 0 0 auto;
    display: flex;
    justify-content: center;
    align-items: center;
    
    .feature-badge {
      .badge-text {
        display: inline-flex;
        align-items: center;
        gap: 4px;
        background: rgba(59, 130, 246, 0.08);
        color: #3b82f6;
        font-size: 11px;
        font-weight: 600;
        padding: 6px 12px;
        border-radius: 6px;
        border: 1px solid rgba(59, 130, 246, 0.15);
        
        .anticon {
          font-size: 10px;
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: @screen-md) {
  .template-selection {
    .selection-header {
      flex-direction: column;
      gap: @spacing-md;
      align-items: stretch;
    }
  }
  
  .template-card {
    height: 180px; // 中等屏幕稍小的高度
    
    .card-header {
      height: 60px; // 稍小的头部
      flex: 0 0 60px;
      
      .card-avatar .ant-avatar {
        width: 36px;
        height: 36px;
        font-size: @font-size-base;
      }
    }
    
    .card-content {
      height: 76px; // 相应调整内容高度
      flex: 0 0 76px;
      padding: 10px 12px;
      
      .card-title {
        font-size: 13px;
        height: 20px;
        line-height: 20px;
        margin-bottom: 6px;
      }
      
      .card-description {
        height: 40px; // 2行，每行20px
        font-size: 11px;
        line-height: 20px;
        -webkit-line-clamp: 2;
        line-clamp: 2;
      }
    }
    
    .card-footer {
      height: 44px; // 保持底部高度
      flex: 0 0 44px;
      padding: 8px 12px;
      
      .feature-tag {
        font-size: 11px;
        padding: 3px 6px;
        height: 22px;
        font-size: @font-size-xs;
        padding: @spacing-xs; // 稍微减小padding
      }
    }
  }
}

@media (max-width: @screen-sm) {
  .template-group-select {
    padding: @spacing-md 0;
  }
  
  .template-card {
    height: 160px; // 小屏幕更紧凑的高度
    
    .card-header {
      height: 50px; // 更小的头部
      flex: 0 0 50px;
      
      .card-avatar .ant-avatar {
        width: 32px; // 更小的头像
        height: 32px;
        font-size: @font-size-sm;
      }
    }
    
    .card-content {
      height: 66px; // 相应调整内容高度
      flex: 0 0 66px;
      padding: 8px 10px;
      
      .card-title {
        font-size: 12px;
        height: 18px;
        line-height: 18px;
        margin-bottom: 4px;
      }
      
      .card-description {
        height: 36px; // 2行，每行18px
        font-size: 10px;
        line-height: 18px;
        -webkit-line-clamp: 2;
        line-clamp: 2;
      }
    }
    
    .card-footer {
      height: 44px; // 保持底部高度不变
      flex: 0 0 44px;
      padding: 6px 10px;
      
      .feature-tag {
        font-size: 10px;
        padding: 2px 4px;
        height: 20px;
      }
    }
  }
}
</style>
