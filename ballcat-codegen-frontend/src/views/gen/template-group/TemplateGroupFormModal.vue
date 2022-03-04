<template>
  <a-modal
    :title="title"
    :visible="visible"
    :mask-closable="false"
    :body-style="{ paddingBottom: '8px' }"
    :confirm-loading="submitLoading"
    :width="350"
    @ok="handleSubmit"
    @cancel="handleClose"
  >
    <a-form layout="vertical" :label-col="{ span: 24 }" :wrapper-col="{ span: 24 }">
      <a-form-item v-if="isUpdate" style="display: none">
        <a-input v-model:value="modelRef.id" />
      </a-form-item>
      <div v-if="isCopy" style="margin-bottom: 24px">
        <h3><span style="margin-right: 12px">源模板组:</span> {{ resourceGroupName }}</h3>
      </div>
      <a-form-item label="名称" v-bind="validateInfos.name">
        <a-input v-model:value="modelRef.name" placeholder="请输入模板组名称" />
      </a-form-item>
      <a-form-item label="备注信息">
        <a-textarea v-model:value="modelRef.remarks" placeholder="请输入模板组备注信息" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
  import { computed, reactive, ref } from 'vue'
  import { usePopup } from '@/hooks/popup'
  import { copyProperties } from '@/utils/bean-util'
  import {
    addTemplateGroup,
    updateTemplateGroup,
    copyTemplateGroup
  } from '@/api/gen/template-group'

  // 类型引入
  import type { AxiosResponse } from 'axios'
  import type { R } from '@/utils/axios/types'
  import type { TemplateGroup } from '@/api/gen/template-group/types'
  import { TemplateGroupFormModalInstance } from '@/views/gen/template-group/types'
  import { useForm } from 'ant-design-vue/es/form'
  import { doRequest } from '@/utils/axios/request'

  // 定义事件
  let emits = defineEmits<{
    (e: 'done'): void // 提交完成事件
  }>()

  let { visible, handleOpen, handleClose } = usePopup()

  let resourceGroupId = ref<number>()
  const resourceGroupName = ref<string>()

  const title = ref<string>('')

  let reqFunction: (data: TemplateGroup) => Promise<AxiosResponse<R>>

  const formAction = ref('ADD')
  const isUpdate = computed(() => formAction.value === 'UPDATE')
  const isCopy = computed(() => formAction.value === 'COPY')

  const modelRef = reactive<TemplateGroup>({
    id: undefined,
    name: '',
    remarks: ''
  })

  const rulesRef = reactive({
    name: [{ required: true, message: '模板组名称不能为空!' }]
  })

  // 提交按钮的 loading 状态控制
  const submitLoading = ref<boolean>(false)

  const { validate, validateInfos, resetFields } = useForm(modelRef, rulesRef)

  function handleSubmit() {
    validate().then(() => {
      doRequest({
        request: reqFunction(modelRef),
        successMessage: '保存成功！',
        onSuccess() {
          emits('done')
          handleClose()
        }
      })
    })
  }

  defineExpose<TemplateGroupFormModalInstance>({
    add() {
      title.value = '新建模板组'
      formAction.value = 'ADD'
      resetFields()
      handleOpen()
      reqFunction = addTemplateGroup
    },
    update(record: TemplateGroup) {
      title.value = '编辑模板组'
      formAction.value = 'UPDATE'
      resetFields()
      copyProperties(modelRef, record)
      handleOpen()
      reqFunction = updateTemplateGroup
    },
    copy(record: TemplateGroup) {
      title.value = '复制模板组'
      formAction.value = 'COPY'
      resetFields()
      handleOpen()
      resourceGroupId.value = record.id
      resourceGroupName.value = record.name
      reqFunction = (record: TemplateGroup) => {
        return copyTemplateGroup(resourceGroupId.value as number, record)
      }
    }
  })
</script>
