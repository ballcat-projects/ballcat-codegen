<template>
  <a-form layout="vertical">
    <a-row :gutter="24" justify="center">
      <a-col :sm="24" :md="8">
        <a-form-item>
          <a-spin :spinning="treeLoading">
            <div :style="{ borderBottom: '1px solid #E9E9E9', marginBottom: '5px' }">
              <a-checkbox
                :indeterminate="templateEntryIdsState.indeterminate"
                :checked="templateEntryIdsState.checkAll"
                @change="onCheckAllChange"
              >
                <b>生成文件选择</b>
              </a-checkbox>
            </div>
            <a-directory-tree
              v-model:checkedKeys="templateEntryIdsState.checkedKeys"
              :tree-data="templateEntryTree"
              :checkable="true"
              @check="onTemplateEntryIdsChange"
            ></a-directory-tree>
          </a-spin>
        </a-form-item>
      </a-col>
      <a-col :sm="24" :md="12">
        <a-form-item v-if="generatorConfigStore.isUseTable" label="表前缀截取">
          <a-input
            v-model:value="modelRef.tablePrefix"
            placeholder="填写则会将表名的前缀截取后，再生成类名"
          />
        </a-form-item>
        <template v-for="item in templateProperties" :key="item.id">
          <a-form-item v-bind="validateInfos['genProperties.' + item.propKey]">
            <template #label>
              <a-space>
                {{ item.title }}
                <span style="color: rgba(0, 0, 0, 0.45)">{{ '(' + item.propKey + ')' }}</span>
                <a-tooltip v-if="item.remarks" placement="topLeft">
                  <template #title>
                    <span>{{ item.remarks }}</span>
                  </template>
                  <QuestionCircleOutlined style="color: rgba(0, 0, 0, 0.45)" />
                </a-tooltip>
              </a-space>
            </template>
            <a-input
              v-if="item.componentType === ComponentType.INPUT"
              v-model:value="modelRef.genProperties[item.propKey]"
              :placeholder="'请输入' + item.title"
            />
            <a-input-number
              v-else-if="item.componentType === ComponentType.INPUT_NUMBER"
              v-model:value="modelRef.genProperties[item.propKey]"
              :placeholder="'请输入' + item.title"
            />
            <a-select
              v-else-if="item.componentType === ComponentType.SELECT"
              v-model:value="modelRef.genProperties[item.propKey]"
              :placeholder="'请输入' + item.title"
            >
              <a-select-option
                v-for="option in item.componentOptions"
                :key="option.value"
                :value="option.value"
              >
                {{ option.name }}
              </a-select-option>
            </a-select>
            <a-radio-group
              v-else-if="item.componentType === ComponentType.RADIO"
              v-model:value="modelRef.genProperties[item.propKey]"
              :placeholder="'请输入' + item.title"
            >
              <a-radio
                v-for="option in item.componentOptions"
                :key="option.value"
                :value="option.value"
              >
                {{ option.name }}
              </a-radio>
            </a-radio-group>
          </a-form-item>
        </template>
      </a-col>
    </a-row>
  </a-form>
</template>

<script setup lang="ts">
import { nextTick, reactive, ref } from 'vue'
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
import { QuestionCircleOutlined } from '@ant-design/icons-vue'
import { useGeneratorConfigStore } from '@/store'
import { ComponentType, type TemplateProperty } from '@/api/gen/template-property/types'

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

const rulesRef = reactive<Props>({})
const { validate, validateInfos } = Form.useForm(modelRef, rulesRef)

const templateEntryIdsState = reactive({
  indeterminate: false,
  checkAll: true,
  allIds: [] as string[],
  halfCheckedKeys: [] as string[],
  checkedKeys: [] as string[]
})
const onCheckAllChange = (e: any): void => {
  templateEntryIdsState.indeterminate = false
  templateEntryIdsState.checkAll = e.target.checked
  templateEntryIdsState.checkedKeys = templateEntryIdsState.checkAll
    ? templateEntryIdsState.allIds
    : []
}
const onTemplateEntryIdsChange = (checkedList: number[], info: CheckInfo): void => {
  const allIds = templateEntryIdsState.allIds
  templateEntryIdsState.halfCheckedKeys = info && (info.halfCheckedKeys as string[])
  templateEntryIdsState.indeterminate = !!checkedList.length && checkedList.length < allIds.length
  templateEntryIdsState.checkAll = checkedList.length === allIds.length
}

// 模板属性集合
const templateProperties = ref<TemplateProperty[]>()
// 模板项的树
const templateEntryTree = ref<TemplateEntry[]>()
// EntryTree 加载状态
const treeLoading = ref(false)

const initPage = () => {
  const templateGroupKey = generatorConfigStore.options.templateGroupKey
  if (!templateGroupKey) {
    return
  }
  doRequest({
    request: listTemplateProperty(templateGroupKey),
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
    }
  })
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
    onFinally() {
      treeLoading.value = false
    }
  })
}

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
// 修改树选择后的背景色
:deep(.ant-tree.ant-tree-directory .ant-tree-treenode-selected:hover::before) {
  background: #ceb7ed;
}
:deep(.ant-tree.ant-tree-directory .ant-tree-treenode-selected::before) {
  background: #ceb7ed;
}
</style>
