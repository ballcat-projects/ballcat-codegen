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
                  'template-card',
                  { 'template-card-selected': item.value === templateGroupKey }
                ]"
                @click="selectGroup(item)"
                :body-style="{ padding: 0 }"
              >
                <!-- 卡片头部 -->
                <div class="card-header">
                  <div class="card-avatar">
                    <a-avatar 
                      :size="48" 
                      :src="item.attributes?.icon"
                      :style="{ backgroundColor: item.attributes?.color || '#1890ff' }"
                    >
                      <template #icon>
                        <AppstoreOutlined />
                      </template>
                    </a-avatar>
                  </div>
                  <!-- 选中标识 -->
                  <div v-if="item.value === templateGroupKey" class="selected-indicator">
                    <CheckCircleFilled />
                  </div>
                </div>
                
                <!-- 卡片内容 -->
                <div class="card-content">
                  <div class="card-title">{{ item.name }}</div>
                  <div class="card-description">
                    {{ item.attributes?.remarks || '暂无描述' }}
                  </div>
                </div>
                
                <!-- 卡片底部 -->
                <div class="card-footer">
                  <a-tag 
                    v-if="item.attributes?.useTable" 
                    color="blue"
                    class="feature-tag"
                  >
                    <DatabaseOutlined />
                    <span>需要数据源</span>
                  </a-tag>
                  <a-tag 
                    v-else 
                    color="green"
                    class="feature-tag"
                  >
                    <CodeOutlined />
                    <span>无需数据源</span>
                  </a-tag>
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

.template-card {
  position: relative;
  height: 220px; // 减少高度，更紧凑
  transition: @animation-base;
  border-radius: @border-radius-lg;
  cursor: pointer;
  border: 2px solid transparent;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  
  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
    border-color: @primary-color;
  }
  
  &.template-card-selected {
    border-color: @primary-color;
    box-shadow: 0 4px 16px rgba(24, 144, 255, 0.2);
    
    &:hover {
      border-color: @primary-color-hover;
      box-shadow: 0 8px 24px rgba(24, 144, 255, 0.3);
    }
  }
  
  .card-header {
    position: relative;
    display: flex;
    justify-content: center;
    align-items: center;
    flex: 0 0 auto; // 不伸缩，保持内容大小
    padding: @spacing-lg 0; // 使用padding控制高度而不是固定height
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    
    .card-avatar {
      display: flex;
      justify-content: center;
      align-items: center;
      
      .ant-avatar {
        width: 48px;
        height: 48px;
        border: 3px solid rgba(255, 255, 255, 0.9);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
      }
    }
  }
  
  .card-content {
    flex: 1; // 占据剩余的所有空间
    display: flex;
    flex-direction: column;
    padding: @spacing-lg @spacing-md;
    overflow: hidden; // 防止内容溢出
    
    .card-title {
      font-size: @font-size-base;
      font-weight: @font-weight-semibold;
      color: @text-color-primary;
      margin-bottom: @spacing-md;
      line-height: 1.4;
      flex: 0 0 auto; // 标题不伸缩
      display: -webkit-box;
      -webkit-line-clamp: 1;
      line-clamp: 1;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }
    
    .card-description {
      font-size: @font-size-sm;
      color: @text-color-secondary;
      line-height: 1.6;
      flex: 1; // 描述占据内容区域的剩余空间
      display: -webkit-box;
      -webkit-line-clamp: 4;
      line-clamp: 4;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }
  }
  
  .card-footer {
    flex: 0 0 auto; // 不伸缩，保持内容大小
    padding: @spacing-md @spacing-lg;
    background-color: @background-color-light;
    border-top: 1px solid @border-color-base;
    display: flex;
    justify-content: center;
    align-items: center;
    
    .feature-tag {
      display: inline-flex;
      align-items: center;
      gap: @spacing-xs;
      font-size: @font-size-sm;
      font-weight: @font-weight-medium;
      border: none;
      border-radius: @border-radius-sm;
      padding: @spacing-xs @spacing-sm;
      
      span {
        margin: 0;
        line-height: 1;
      }
      
      .anticon {
        font-size: @font-size-sm;
      }
    }
  }
  
  .selected-indicator {
    position: absolute;
    top: @spacing-xs; // 减少距离顶部的间距
    right: @spacing-xs; // 减少距离右边的间距
    color: #fff;
    font-size: @font-size-lg; // 减小图标大小
    z-index: 2;
    background: @primary-color;
    border-radius: 50%;
    width: 24px; // 减小尺寸
    height: 24px;
    display: flex;
    justify-content: center;
    align-items: center;
    box-shadow: 0 2px 8px rgba(24, 144, 255, 0.4);
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
    height: 200px; // 调整响应式高度
    
    .card-header {
      height: 70px;
      
      .card-avatar .ant-avatar {
        width: 40px;
        height: 40px;
        font-size: @font-size-base;
      }
    }
    
    .card-content {
      padding: @spacing-md @spacing-sm; // 中等屏幕的内边距
      
      .card-title {
        font-size: @font-size-sm;
        margin-bottom: @spacing-xs; // 稍微减少间距
      }
      
      .card-description {
        font-size: @font-size-xs;
        line-height: 1.4;
        -webkit-line-clamp: 3; // 中等屏幕显示3行
        line-clamp: 3;
      }
    }
    
    .card-footer {
      padding: @spacing-sm @spacing-md; // 中等屏幕的底部内边距
      min-height: 32px;
      
      .feature-tag {
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
    height: 180px; // 小屏幕进一步减小高度
    
    .card-header {
      height: 60px; // 更小的头部
      
      .card-avatar .ant-avatar {
        width: 36px; // 更小的头像
        height: 36px;
        font-size: @font-size-sm;
      }
    }
    
    .card-content {
      padding: @spacing-xs @spacing-sm; // 更紧凑的内边距
      
      .card-title {
        font-size: @font-size-sm;
        margin-bottom: 2px; // 更小的间距
      }
      
      .card-description {
        font-size: @font-size-xs;
        -webkit-line-clamp: 2;
        line-clamp: 2;
      }
    }
    
    .card-footer {
      padding: @spacing-xs; // 最小的底部内边距
      min-height: 28px; // 更小的最小高度
      
      .feature-tag {
        font-size: 10px; // 极小的字体
        padding: 1px @spacing-xs;
        gap: 2px; // 更小的间距
        
        .anticon {
          font-size: 10px;
        }
      }
    }
    
    .selected-indicator {
      width: 20px; // 更小的选中指示器
      height: 20px;
      font-size: @font-size-sm;
    }
  }
}
</style>
