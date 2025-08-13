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
        <a-result status="warning" title="暂无可用模板组" sub-title="请先创建模板组。在项目的 /template 文件夹下有预置的模板 SQL 文件，可以选择需要的模板进行初始化">
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
              <a-input-search v-model:value="searchKeyword" placeholder="搜索模板组..." style="width: 200px"
                @search="handleSearch" />
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
            <a-col v-for="item in filteredTemplateGroups" :key="item.value" :xs="24" :sm="12" :md="8" :lg="6">
              <a-card hoverable :class="[
                'modern-template-card',
                { 'selected': item.value === templateGroupKey }
              ]" @click="selectGroup(item)" :body-style="{ padding: 0 }">
                <div class="card-wrapper">
                  <!-- Logo区域 -->
                  <div class="logo-section">
                    <div class="template-logo" :class="{ 'default-logo': !item.attributes?.icon }">
                      <img v-if="item.attributes?.icon" :src="item.attributes.icon" :alt="item.name" />
                      <AppstoreOutlined v-else />
                    </div>

                    <!-- 选中状态指示器 -->
                    <div v-if="item.value === templateGroupKey" class="selected-check">
                      <CheckOutlined style="font-size: 12px;" />
                    </div>
                  </div>

                  <!-- 内容区域 -->
                  <div class="content-section">
                    <!-- 标题区域 -->
                    <div class="title-area">
                      <h3 class="template-title" :title="item.name">
                        {{ item.name }}
                      </h3>
                    </div>

                    <!-- 描述区域 -->
                    <div class="description-area">
                      <p v-if="item.attributes?.remarks" class="template-description" :title="item.attributes.remarks">
                        {{ item.attributes.remarks }}
                      </p>
                      <p v-else class="empty-description">
                        暂无描述信息
                      </p>
                    </div>
                  </div>

                  <!-- 数据源指示器区域 -->
                  <div class="indicator-section">
                    <div class="datasource-indicator" :class="{
                      'needs-datasource': item.attributes?.useTable,
                      'no-datasource': !item.attributes?.useTable
                    }">
                      <DatabaseOutlined v-if="item.attributes?.useTable" />
                      <CodeOutlined v-else />
                      <span>{{ item.attributes?.useTable ? '需要数据源' : '无需数据源' }}</span>
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
  CheckOutlined
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

/* 全新UI设计 - 信息卡片式布局 */
.modern-template-card {
  position: relative;
  height: 240px;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  background: #ffffff;
  cursor: pointer;
  transition: all 0.25s ease;
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
    border-color: #d1d5db;
  }

  &.selected {
    border-color: #3b82f6;
    box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1), 0 4px 12px rgba(59, 130, 246, 0.15);

    .logo-section {
      background: linear-gradient(135deg, #eff6ff 0%, #dbeafe 100%);

      .template-logo {
        transform: scale(1.05);
      }
    }



    .datasource-indicator {
      &.needs-datasource {
        background: linear-gradient(135deg, #64748b 0%, #475569 100%);
        color: white;
        box-shadow: 0 2px 8px rgba(100, 116, 139, 0.3);
      }

      &.no-datasource {
        background: linear-gradient(135deg, #94a3b8 0%, #64748b 100%);
        color: white;
        box-shadow: 0 2px 8px rgba(148, 163, 184, 0.3);
      }
    }
  }

  .card-wrapper {
    height: 100%;
    display: flex;
    flex-direction: column;
    position: relative;
  }

  /* Logo区域 - 60px高度 */
  .logo-section {
    height: 60px;
    flex: 0 0 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #f9fafb;
    border-bottom: 1px solid #f3f4f6;
    position: relative;
    transition: all 0.25s ease;

    .template-logo {
      width: 40px;
      height: 40px;
      border-radius: 8px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 18px;
      font-weight: 600;
      color: white;
      background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
      box-shadow: 0 2px 8px rgba(99, 102, 241, 0.2);
      transition: all 0.25s ease;

      /* 默认Logo样式 */
      &.default-logo {
        background: linear-gradient(135deg, #64748b 0%, #475569 100%);

        .anticon {
          font-size: 20px;
        }
      }

      /* 自定义Logo */
      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
        border-radius: 8px;
      }
    }

    /* 选中状态指示器 */
    .selected-check {
      position: absolute;
      top: 8px;
      right: 8px;
      width: 20px;
      height: 20px;
      background: #3b82f6;
      border-radius: 50%;
      display: flex !important;
      align-items: center;
      justify-content: center;
      color: white;
      font-size: 12px;
      font-weight: bold;
      transition: all 0.25s ease;
      box-shadow: 0 2px 4px rgba(59, 130, 246, 0.3);
      z-index: 10;
    }
  }

  /* 内容区域 - 140px高度 */
  .content-section {
    height: 140px;
    flex: 0 0 140px;
    padding: 16px;
    display: flex;
    flex-direction: column;
    gap: 8px;

    /* 标题区域 - 固定24px高度 */
    .title-area {
      height: 24px;
      flex: 0 0 24px;

      .template-title {
        font-size: 16px;
        font-weight: 600;
        color: #111827;
        line-height: 24px;
        margin: 0;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        display: block;
      }
    }

    /* 描述区域 - 剩余空间，最多4行 */
    .description-area {
      flex: 1;
      min-height: 0;

      .template-description {
        font-size: 14px;
        color: #6b7280;
        line-height: 20px;
        margin: 0;
        overflow: hidden;
        display: -webkit-box;
        -webkit-line-clamp: 4;
        line-clamp: 4;
        -webkit-box-orient: vertical;
        word-break: break-word;
        height: 80px;
        /* 4行 × 20px行高 */
      }
    }

    /* 空描述占位 */
    .empty-description {
      color: #9ca3af;
      font-style: italic;
      font-size: 14px;
      line-height: 20px;
    }
  }

  /* 数据源指示器区域 - 40px高度 */
  .indicator-section {
    height: 40px;
    flex: 0 0 40px;
    padding: 8px 16px;
    border-top: 1px solid #f3f4f6;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #fafafa;

    .datasource-indicator {
      display: inline-flex;
      align-items: center;
      gap: 6px;
      padding: 6px 12px;
      border-radius: 20px;
      font-size: 12px;
      font-weight: 500;
      transition: all 0.25s ease;

      .anticon {
        font-size: 12px;
      }

      /* 需要数据源 */
      &.needs-datasource {
        background: #f1f5f9;
        color: #475569;
        border: 1px solid #cbd5e1;
      }

      /* 不需要数据源 */
      &.no-datasource {
        background: #f8fafc;
        color: #64748b;
        border: 1px solid #e2e8f0;
      }
    }
  }



  /* 悬浮时的微妙效果 */
  &:hover:not(.selected) {
    .logo-section {
      background: #f3f4f6;
    }

    .template-logo {
      transform: scale(1.02);
    }

    .datasource-indicator {
      transform: scale(1.02);
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
