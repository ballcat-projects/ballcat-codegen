<template>
  <div class="template-config-step">
    <!-- 页面头部 -->
    <div class="step-header">
      <div class="header-info">
        <h3>模板配置</h3>
        <p>配置代码生成参数和选择需要生成的文件</p>
      </div>
      <div class="header-actions">
        <a-space>
          <a-button @click="resetToDefaults" type="default">
            <ReloadOutlined />
            重置默认值
          </a-button>
          <a-button @click="previewConfig" type="default">
            <EyeOutlined />
            预览配置
          </a-button>
        </a-space>
      </div>
    </div>

    <a-form layout="vertical" class="config-form">
      <a-row :gutter="32">
        <!-- 左侧：文件选择 -->
        <a-col :xs="24" :lg="10">
          <div class="section-card">
            <div class="section-header">
              <h4>
                <FileTextOutlined />
                生成文件选择
              </h4>
              <a-tooltip title="选择需要生成的模板文件">
                <QuestionCircleOutlined class="help-icon" />
              </a-tooltip>
            </div>
            
            <a-form-item>
              <a-spin :spinning="treeLoading">
                <div class="file-selection-header">
                  <a-checkbox
                    :indeterminate="templateEntryIdsState.indeterminate"
                    :checked="templateEntryIdsState.checkAll"
                    @change="onCheckAllChange"
                  >
                    <span class="select-all-text">全选文件</span>
                  </a-checkbox>
                  <a-tag color="blue" v-if="selectedFilesCount > 0">
                    已选择 {{ selectedFilesCount }} 个文件
                  </a-tag>
                </div>
                
                <div class="file-tree-container">
                  <a-directory-tree
                    v-model:checkedKeys="templateEntryIdsState.checkedKeys"
                    :tree-data="templateEntryTree"
                    :checkable="true"
                    @check="onTemplateEntryIdsChange"
                    class="custom-tree"
                  />
                </div>
              </a-spin>
            </a-form-item>
          </div>
        </a-col>

        <!-- 右侧：配置参数 -->
        <a-col :xs="24" :lg="14">
          <div class="section-card">
            <div class="section-header">
              <h4>
                <SettingOutlined />
                配置参数
              </h4>
              <a-tooltip title="配置代码生成的相关参数">
                <QuestionCircleOutlined class="help-icon" />
              </a-tooltip>
            </div>

            <!-- 表前缀配置 -->
            <a-form-item 
              v-if="generatorConfigStore.isUseTable" 
              label="表前缀截取"
              class="form-item-enhanced"
            >
              <template #extra>
                <div class="form-extra">
                  例如：表名 <code>sys_user</code> 配置前缀 <code>sys_</code> 后，生成的类名为 <code>User</code>
                </div>
              </template>
              <a-input
                v-model:value="modelRef.tablePrefix"
                placeholder="填写则会将表名的前缀截取后，再生成类名"
                class="enhanced-input"
              />
            </a-form-item>

            <!-- 动态配置属性 -->
            <div v-if="templateProperties && templateProperties.length > 0" class="properties-section">
              <a-divider>
                <span class="divider-text">模板属性配置</span>
              </a-divider>
              
              <template v-for="item in templateProperties" :key="item.id">
                <a-form-item 
                  v-bind="validateInfos['genProperties.' + item.propKey]"
                  class="form-item-enhanced"
                >
                  <template #label>
                    <div class="property-label">
                      <span class="property-title">{{ item.title }}</span>
                      <a-tag size="small" color="default">{{ item.propKey }}</a-tag>
                      <a-tooltip v-if="item.remarks" placement="topLeft">
                        <template #title>
                          <span>{{ item.remarks }}</span>
                        </template>
                        <QuestionCircleOutlined class="help-icon" />
                      </a-tooltip>
                    </div>
                  </template>

                  <!-- 输入框 -->
                  <a-input
                    v-if="item.componentType === ComponentType.INPUT"
                    v-model:value="modelRef.genProperties[item.propKey]"
                    :placeholder="'请输入' + item.title"
                    class="enhanced-input"
                  />

                  <!-- 数字输入框 -->
                  <a-input-number
                    v-else-if="item.componentType === ComponentType.INPUT_NUMBER"
                    v-model:value="modelRef.genProperties[item.propKey]"
                    :placeholder="'请输入' + item.title"
                    style="width: 100%"
                    class="enhanced-input"
                  />

                  <!-- 下拉选择 -->
                  <a-select
                    v-else-if="item.componentType === ComponentType.SELECT"
                    v-model:value="modelRef.genProperties[item.propKey]"
                    :placeholder="'请选择' + item.title"
                    class="enhanced-input"
                  >
                    <a-select-option
                      v-for="option in item.componentOptions"
                      :key="option.value"
                      :value="option.value"
                    >
                      {{ option.name }}
                    </a-select-option>
                  </a-select>

                  <!-- 单选框组 -->
                  <a-radio-group
                    v-else-if="item.componentType === ComponentType.RADIO"
                    v-model:value="modelRef.genProperties[item.propKey]"
                    class="enhanced-radio-group"
                  >
                    <a-row :gutter="[12, 4]">
                      <a-col 
                        v-for="option in item.componentOptions"
                        :key="option.value"
                        :xs="24"
                        :sm="(item.componentOptions?.length || 0) <= 2 ? 12 : (item.componentOptions?.length || 0) <= 3 ? 12 : 24"
                        :md="(item.componentOptions?.length || 0) <= 3 ? 8 : (item.componentOptions?.length || 0) <= 6 ? 6 : 4"
                        :lg="(item.componentOptions?.length || 0) <= 4 ? 6 : (item.componentOptions?.length || 0) <= 8 ? 4 : 3"
                      >
                        <a-radio
                          :value="option.value"
                          class="enhanced-radio"
                        >
                          {{ option.name }}
                        </a-radio>
                      </a-col>
                    </a-row>
                  </a-radio-group>
                </a-form-item>
              </template>
            </div>

            <!-- 空状态 -->
            <a-empty 
              v-else-if="!treeLoading"
              description="当前模板组无配置参数"
              :image="Empty.PRESENTED_IMAGE_SIMPLE"
            />
          </div>
        </a-col>
      </a-row>
    </a-form>

    <!-- 配置预览模态框 -->
    <a-modal
      v-model:open="previewModalVisible"
      title="配置预览"
      :footer="null"
      width="600px"
    >
      <div class="config-preview">
        <a-descriptions :column="1" bordered size="small">
          <a-descriptions-item label="表前缀" v-if="generatorConfigStore.isUseTable">
            <code>{{ modelRef.tablePrefix || '无' }}</code>
          </a-descriptions-item>
          <a-descriptions-item label="选中文件数">
            <a-tag color="blue">{{ selectedFilesCount }} 个文件</a-tag>
          </a-descriptions-item>
          <a-descriptions-item 
            v-for="(value, key) in modelRef.genProperties" 
            :key="key"
            :label="getPropertyTitle(String(key))"
          >
            <code>{{ value || '未设置' }}</code>
          </a-descriptions-item>
        </a-descriptions>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { nextTick, reactive, ref, computed, onMounted } from 'vue'
import { 
  ReloadOutlined, 
  EyeOutlined, 
  FileTextOutlined, 
  SettingOutlined, 
  QuestionCircleOutlined 
} from '@ant-design/icons-vue'
import { Empty, message } from 'ant-design-vue'
import { listTemplateProperty } from '@/api/gen/template-property'
import { doRequest } from '@/utils/axios/request'
import { Form } from 'ant-design-vue'
import { listTemplateEntry } from '@/api/gen/template-entry'
import type { GeneratorOption } from '@/api/gen/generate/types'
import type { Props } from 'ant-design-vue/es/form/useForm'
import type { GenerateStepInstance } from '@/views/gen/codegen/types'
import type { TemplateEntry } from '@/api/gen/template-entry/types'
import { listToTree } from '@/utils/tree-util'
import type { DataNode } from 'ant-design-vue/es/vc-tree/interface'
import type { CheckInfo } from 'ant-design-vue/es/vc-tree/props'
import { useGeneratorConfigStore } from '@/store'
import { ComponentType, PropType, type TemplateProperty } from "@/api/gen/template-property/types";

// 代码生成配置信息
const generatorConfigStore = useGeneratorConfigStore()

// 表单数据
const modelRef = reactive<
  Pick<GeneratorOption, 'templateEntryIds' | 'genProperties' | 'tablePrefix'>
>({
  templateEntryIds: [],
  tablePrefix: '',
  genProperties: {}
})

// 额外状态
const previewModalVisible = ref(false)

const rulesRef = reactive<Props>({})
const { validate, validateInfos } = Form.useForm(modelRef, rulesRef)

// 文件选择状态
const templateEntryIdsState = reactive({
  indeterminate: false,
  checkAll: true,
  allIds: [] as string[],
  halfCheckedKeys: [] as string[],
  checkedKeys: [] as string[]
})

// 计算属性
const selectedFilesCount = computed(() => {
  return templateEntryIdsState.checkedKeys.length + templateEntryIdsState.halfCheckedKeys.length
})

// 模板属性集合
const templateProperties = ref<TemplateProperty[]>()
// 模板项的树
const templateEntryTree = ref<TemplateEntry[]>()
// EntryTree 加载状态
const treeLoading = ref(false)

// 全选/取消全选
const onCheckAllChange = (e: any): void => {
  templateEntryIdsState.indeterminate = false
  templateEntryIdsState.checkAll = e.target.checked
  templateEntryIdsState.checkedKeys = templateEntryIdsState.checkAll
    ? templateEntryIdsState.allIds
    : []
}

// 文件选择变化
const onTemplateEntryIdsChange = (checkedList: number[], info: CheckInfo): void => {
  const allIds = templateEntryIdsState.allIds
  templateEntryIdsState.halfCheckedKeys = info && (info.halfCheckedKeys as string[])
  templateEntryIdsState.indeterminate = !!checkedList.length && checkedList.length < allIds.length
  templateEntryIdsState.checkAll = checkedList.length === allIds.length
}

// 重置为默认值
const resetToDefaults = () => {
  if (templateProperties.value) {
    for (const property of templateProperties.value) {
      modelRef.genProperties[property.propKey] = property.defaultValue
    }
    message.success('已重置为默认配置')
  }
}

// 预览配置
const previewConfig = () => {
  previewModalVisible.value = true
}

// 获取属性标题
const getPropertyTitle = (propKey: string): string => {
  const property = templateProperties.value?.find(p => p.propKey === propKey)
  return property?.title || propKey
}

// 初始化页面
const initPage = () => {
  const templateGroupKey = generatorConfigStore.options.templateGroupKey
  if (!templateGroupKey) {
    return
  }
  
  // 加载模板属性
  doRequest({
    request: listTemplateProperty(templateGroupKey, PropType.CONFIG),
    onSuccess: res => {
      templateProperties.value = res.data
      modelRef.genProperties = {}
      rulesRef.value = reactive<Props>({})
      
      if (templateProperties.value && templateProperties.value.length > 0) {
        for (const property of templateProperties.value) {
          modelRef.genProperties[property.propKey] = property.defaultValue
          rulesRef['genProperties.' + property.propKey] = [
            {
              required: property.required === 1,
              message: property.title + '不能为空'
            }
          ]
        }
      }
    },
    onError: () => {
      message.error('加载模板属性失败')
    }
  })
  
  // 加载模板文件树
  treeLoading.value = true
  doRequest({
    request: listTemplateEntry(templateGroupKey),
    onSuccess: res => {
      const list = res.data as TemplateEntry[]
      list.sort((a, b) =>
        // @ts-ignore
        a.type === b.type ? a.filename.localeCompare(b.filename) : a.type - b.type
      )
      templateEntryTree.value = listToTree(list, '0', {
        attributeMapping(treeNode) {
          const dataNode = treeNode as unknown as DataNode
          dataNode.title = treeNode.filename
        }
      })
      
      // 延迟绑定，抑制控制台警告
      templateEntryIdsState.checkedKeys = []
      nextTick(() => {
        templateEntryIdsState.checkAll = true
        templateEntryIdsState.allIds = (res.data ? res.data.map(x => x.id) : []) as string[]
        templateEntryIdsState.checkedKeys = templateEntryIdsState.allIds
        templateEntryIdsState.indeterminate = false
      })
    },
    onError: () => {
      message.error('加载模板文件失败')
    },
    onFinally() {
      treeLoading.value = false
    }
  })
}

// 组件挂载时初始化
onMounted(() => {
  // 如果已经有选中的模板组，则初始化页面
  if (generatorConfigStore.options.templateGroupKey) {
    initPage()
  }
})

// 组件暴露的方法
defineExpose<GenerateStepInstance>({
  enter: initPage,
  validate: () => {
    const selectedEntries = [
      ...templateEntryIdsState.halfCheckedKeys,
      ...templateEntryIdsState.checkedKeys
    ]
    if (selectedEntries.length == 0) {
      return Promise.reject({ message: '请至少选择一个文件' })
    }
    return validate()
  },
  next: () => {
    generatorConfigStore.options.tablePrefix = modelRef.tablePrefix
    generatorConfigStore.options.genProperties = modelRef.genProperties
    generatorConfigStore.options.templateEntryIds = [
      ...templateEntryIdsState.halfCheckedKeys,
      ...templateEntryIdsState.checkedKeys
    ]
  }
})
</script>

<style scoped lang="less">
.template-config-step {
  padding: @spacing-lg 0;
}

.step-header {
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

.config-form {
  .section-card {
    background: @bg-color-container;
    border: 1px solid @border-color-light;
    border-radius: @border-radius-lg;
    padding: @spacing-xl;
    height: 100%;
    
    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: @spacing-lg;
      
      h4 {
        margin: 0;
        color: @text-color-primary;
        font-size: @font-size-lg;
        font-weight: 600;
        display: flex;
        align-items: center;
        gap: @spacing-sm;
      }
      
      .help-icon {
        color: @text-color-secondary;
      }
    }
  }
}

.file-selection-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: @spacing-md 0;
  border-bottom: 1px solid @border-color-light;
  margin-bottom: @spacing-md;
  
  .select-all-text {
    font-weight: 500;
  }
}

.file-tree-container {
  max-height: 400px;
  overflow-y: auto;
  border: 1px solid @border-color;
  border-radius: @border-radius-md;
  padding: @spacing-md;
  
  .custom-tree {
    :deep(.ant-tree-treenode) {
      padding: @spacing-xs 0;
      
      &:hover {
        background: @bg-color-hover;
      }
      
      &.ant-tree-treenode-selected {
        background: #f6ffed;
      }
    }
    
    :deep(.ant-tree-checkbox) {
      margin-right: @spacing-sm;
    }
  }
}

.form-item-enhanced {
  margin-bottom: @spacing-lg;
  
  .property-label {
    display: flex;
    align-items: center;
    gap: @spacing-sm;
    
    .property-title {
      font-weight: 500;
      color: @text-color-primary;
    }
    
    .help-icon {
      color: @text-color-secondary;
    }
  }
  
  .form-extra {
    color: @text-color-secondary;
    font-size: @font-size-sm;
    margin-top: @spacing-xs;
    
    code {
      background: @gray-2;
      padding: 2px 4px;
      border-radius: @border-radius-sm;
      font-size: @font-size-sm;
    }
  }
  
  .enhanced-input {
    transition: @animation-base;
    
    &:hover {
      border-color: @primary-color-hover;
    }
    
    &:focus {
      border-color: @primary-color;
      box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
    }
  }
  
  .enhanced-radio-group {
    width: 100%;
    
    .enhanced-radio {
      display: flex;
      align-items: center;
      margin-bottom: 2px;
      padding: 2px 0;
      width: 100%;
      
      &:last-child {
        margin-bottom: 0;
      }
      
      :deep(.ant-radio) {
        margin-right: @spacing-xs;
      }
      
      :deep(.ant-radio-wrapper) {
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
        font-size: @font-size-sm;
        line-height: 1.3;
      }
    }
  }
}

.properties-section {
  .divider-text {
    color: @text-color-primary;
    font-weight: 500;
  }
}

.config-preview {
  .ant-descriptions {
    :deep(.ant-descriptions-item-label) {
      font-weight: 500;
      background: @gray-2;
    }
    
    :deep(.ant-descriptions-item-content) {
      code {
        background: @gray-2;
        padding: 2px 6px;
        border-radius: @border-radius-sm;
        font-size: @font-size-sm;
      }
    }
  }
}

// 响应式设计
@media (max-width: @screen-lg) {
  .step-header {
    flex-direction: column;
    gap: @spacing-md;
    align-items: stretch;
  }
}

@media (max-width: @screen-md) {
  .template-config-step {
    padding: @spacing-md 0;
  }
  
  .config-form {
    .section-card {
      padding: @spacing-lg;
      margin-bottom: @spacing-lg;
    }
  }
  
  .file-tree-container {
    max-height: 300px;
  }
}

// 原有样式保持（用于向下兼容）
:deep(.ant-tree.ant-tree-directory .ant-tree-treenode-selected:hover::before) {
  background: @primary-color-hover;
}

:deep(.ant-tree.ant-tree-directory .ant-tree-treenode-selected::before) {
  background: @primary-color;
}
</style>
