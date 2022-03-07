<template>
  <a-modal
    title="属性配置"
    cancel-text="取消"
    :visible="visible"
    :body-style="{ paddingBottom: '8px' }"
    :confirm-loading="submitLoading"
    :width="900"
    :centered="true"
    @ok="generateCode"
    @cancel="handleClose"
  >
    <a-form>
      <a-row :gutter="6">
        <a-col :span="10">
          <a-form-item
            label="模板组"
            :wrapper-col="wrapperCol"
            v-bind="validateInfos.templateGroupId"
          >
            <a-select v-model:value="modelRef.templateGroupId">
              <a-select-option v-for="item in templateGroupSelectData" :key="Number(item.value)">
                {{ item.name }}
              </a-select-option>
            </a-select>
          </a-form-item>
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
        <a-col :span="14">
          <a-divider orientation="left"> <b>系统属性</b> </a-divider>
          <a-form-item label="表前缀" :label-col="labelCol" :wrapper-col="wrapperCol">
            <a-input
              v-model:value="modelRef.tablePrefix"
              placeholder="填写则会将表名的前缀截取后，再生成类名"
            />
          </a-form-item>
          <a-divider orientation="left"> <b>自定义属性</b> </a-divider>
          <template v-for="item in templateProperties" :key="item.id">
            <a-form-item
              :label="item.propKey"
              :label-col="labelCol"
              :wrapper-col="wrapperCol"
              v-bind="validateInfos['genProperties.' + item.propKey]"
            >
              <a-input
                v-model:value="modelRef.genProperties[item.propKey]"
                :placeholder="'请输入' + item.title"
              />
            </a-form-item>
          </template>
        </a-col>
      </a-row>
    </a-form>

    <template #footer>
      <a-button @click="handleClose"> 取消 </a-button>
      <a-button @click="previewCode"> 预览 </a-button>
      <a-button type="primary" @click="generateCode"> 生成 </a-button>
    </template>
  </a-modal>

  <generate-preview-modal ref="previewModal" />
</template>

<script setup lang="ts">
  import { nextTick, reactive, ref, watchEffect } from 'vue'
  import { usePopup } from '@/hooks/popup'
  import { listSelectData } from '@/api/gen/template-group'
  import { listTemplateProperty } from '@/api/gen/template-property'
  import { generate, preview } from '@/api/gen/generate'
  import { doRequest } from '@/utils/axios/request'
  import { remoteFileDownload } from '@/utils/file-util'
  import { Form, message } from 'ant-design-vue'
  import GeneratePreviewModal from '@/views/gen/codegen/GeneratePreviewModal.vue'
  import type { SelectData } from '@/api/types'
  import type { GeneratorOption } from '@/api/gen/generate/types'
  import { TemplateProperty } from '@/api/gen/template-property/types'
  import { Props } from 'ant-design-vue/es/form/useForm'
  import { GenerateModalInstance, PreviewModalInstance } from '@/views/gen/codegen/types'
  import { listTemplateEntry } from '@/api/gen/template-entry'
  import { TemplateEntry } from '@/api/gen/template-entry/types'
  import { listToTree } from '@/utils/tree-util'
  import { DataNode } from 'ant-design-vue/lib/vc-tree/interface'
  import { CheckInfo } from 'ant-design-vue/es/vc-tree/props'

  const labelCol = {
    xs: { span: 12 },
    sm: { span: 24 },
    lg: { span: 6 }
  }
  const wrapperCol = {
    xs: { span: 12 },
    sm: { span: 24 },
    lg: { span: 18 }
  }

  // 弹窗属性
  const { visible, handleOpen, handleClose } = usePopup()

  // 预览弹窗
  const previewModal = ref<PreviewModalInstance>()

  // 当前使用的数据库名称
  const dsName = ref<string>('')

  // 表单提交的 loading 控制
  const submitLoading = ref<boolean>(false)

  // 表单数据
  const modelRef = reactive<GeneratorOption>({
    tableNames: [],
    templateGroupId: null,
    templateEntryIds: [],
    tablePrefix: '',
    genProperties: {}
  })
  // 校验配置
  const rulesRef = reactive<Props>({
    templateGroupId: [{ required: true, message: '必须选择一个模板组' }]
  })
  const { validate, validateInfos } = Form.useForm(modelRef, rulesRef)

  // 模板组的选择数据
  const templateGroupSelectData = ref<SelectData[]>([])
  doRequest({
    request: listSelectData(),
    onSuccess: res => {
      let data = res.data as SelectData[]
      templateGroupSelectData.value = data
      if (data && data.length > 0) {
        modelRef.templateGroupId = Number(data[0].value)
      }
    }
  })

  const templateEntryIdsState = reactive({
    indeterminate: false,
    checkAll: true,
    allIds: [] as number[],
    halfCheckedKeys: [] as number[],
    checkedKeys: [] as number[]
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
    templateEntryIdsState.halfCheckedKeys = info && (info.halfCheckedKeys as number[])
    templateEntryIdsState.indeterminate = !!checkedList.length && checkedList.length < allIds.length
    templateEntryIdsState.checkAll = checkedList.length === allIds.length
  }

  // 模板属性集合
  const templateProperties = ref<TemplateProperty[]>()
  // 模板项的树
  const templateEntryTree = ref<TemplateEntry[]>()
  // EntryTree 加载状态
  const treeLoading = ref(false)

  watchEffect(() => {
    const templateGroupId = modelRef.templateGroupId
    if (!templateGroupId) {
      return
    }
    doRequest({
      request: listTemplateProperty(templateGroupId),
      onSuccess: res => {
        templateProperties.value = res.data
        if (templateProperties.value && templateProperties.value.length > 0) {
          for (let property of templateProperties.value) {
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
      request: listTemplateEntry(templateGroupId),
      onSuccess: res => {
        templateEntryTree.value = listToTree(res.data as TemplateEntry[], 0, {
          attributeMapping(treeNode) {
            const dataNode = treeNode as unknown as DataNode
            dataNode.title = treeNode.filename
          }
        })
        // 延迟绑定，抑制控制台警告
        templateEntryIdsState.checkedKeys = []
        nextTick(() => {
          templateEntryIdsState.checkAll = true
          templateEntryIdsState.allIds = (res.data ? res.data.map(x => x.id) : []) as number[]
          templateEntryIdsState.checkedKeys = templateEntryIdsState.allIds
          templateEntryIdsState.indeterminate = false
        })
      },
      onFinally() {
        treeLoading.value = false
      }
    })
  })

  /* 代码生成 */
  const generateCode = () => {
    validate()
      .then(() => {
        submitLoading.value = true
        // 合并选中的数据和半选的数据
        modelRef.templateEntryIds = [
          ...templateEntryIdsState.halfCheckedKeys,
          ...templateEntryIdsState.checkedKeys
        ]
        generate(dsName.value, modelRef)
          .then(response => {
            remoteFileDownload(response, 'BallCat-CodeGen.zip')
          })
          .catch(() => {
            message.error('代码生成异常')
          })
          .finally(() => {
            submitLoading.value = false
          })
      })
      .catch(err => {
        console.log('error', err)
      })
  }

  /* 代码预览 */
  const previewCode = () => {
    validate()
      .then(() => {
        submitLoading.value = true
        // 合并选中的数据和半选的数据
        modelRef.templateEntryIds = [
          ...templateEntryIdsState.halfCheckedKeys,
          ...templateEntryIdsState.checkedKeys
        ]
        doRequest({
          request: preview(dsName.value, modelRef),
          onSuccess: res => {
            previewModal.value?.open(res.data)
          },
          onFinally: () => (submitLoading.value = false)
        })
      })
      .catch(err => {
        console.log('error', err)
      })
  }

  defineExpose<GenerateModalInstance>({
    open: (adsName: string, tableNames: string[]): void => {
      dsName.value = adsName
      modelRef.tableNames = tableNames
      handleOpen()
    },
    close: handleClose
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
