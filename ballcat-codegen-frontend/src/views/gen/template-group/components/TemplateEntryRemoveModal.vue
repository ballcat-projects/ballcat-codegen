<template>
  <a-modal
    title="节点删除"
    :visible="visible"
    :confirm-loading="submitLoading"
    @ok="handleSubmit"
    @cancel="handleClose"
  >
    <a-form :label-col="labelCol" :wrapper-col="wrapperCol">
      <a-form-item label="删除" v-bind="validateInfos.mode">
        <a-radio-group v-model:value="modelRef.mode">
          <a-radio :value="TemplateEntryRemoveModeEnum.ONLY_ITSELF">
            删除本身(子节点向上移动)
          </a-radio>
          <a-radio :value="TemplateEntryRemoveModeEnum.ITSELF_AND_CHILD"> 删除本身及子节点</a-radio>
        </a-radio-group>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
  import { reactive, ref } from 'vue'
  import { usePopup } from '@/hooks/popup'
  import { removeTemplateEntry } from '@/api/gen/template-entry'
  import { TemplateEntryRemoveModalInstance } from '@/views/gen/template-group/components/types'
  import { useForm } from 'ant-design-vue/es/form'
  import { doRequest } from '@/utils/axios/request'
  import { TemplateEntryRemoveModeEnum } from '@/api/gen/template-entry/types'

  const emits = defineEmits<{
    (e: 'done'): void
  }>()

  const labelCol = {
    sm: { span: 24 },
    md: { span: 3 }
  }
  const wrapperCol = {
    sm: { span: 24 },
    md: { span: 20 }
  }

  // 模板目录项 id
  const templateEntryId = ref<number>()

  // 弹窗相关
  const { visible, handleOpen, handleClose } = usePopup()

  // 表单的数据类型
  const modelRef = reactive({
    mode: TemplateEntryRemoveModeEnum.ONLY_ITSELF
  })

  const rulesRef = reactive({
    mode: [{ required: true, message: '必填内容' }]
  })

  // 提交按钮的 loading 状态控制
  const submitLoading = ref<boolean>(false)

  const { validate, validateInfos } = useForm(modelRef, rulesRef)

  function handleSubmit() {
    validate().then(() => {
      doRequest({
        request: removeTemplateEntry(templateEntryId.value, modelRef.mode),
        successMessage: '删除成功！',
        onSuccess() {
          emits('done')
          handleClose()
        }
      })
    })
  }

  defineExpose<TemplateEntryRemoveModalInstance>({
    open(currentTemplateEntryId: number) {
      templateEntryId.value = currentTemplateEntryId
      handleOpen()
    }
  })
</script>
