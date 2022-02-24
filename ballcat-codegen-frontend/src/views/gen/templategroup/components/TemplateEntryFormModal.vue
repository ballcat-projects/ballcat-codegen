<template>
  <a-modal
    :title="title"
    :visible="visible"
    :confirm-loading="submitLoading"
    @ok="handleSubmit"
    @cancel="handleClose"
  >
    <a-form :label-col="labelCol" :wrapper-col="wrapperCol">
      <a-form-item v-if="isCreate" label="父目录">
        <span> {{ parentFileName }}</span>
      </a-form-item>
      <a-form-item label="文件名">
        <a-input v-model:value="modelRef.fileName" placeholder="请输入文件名" />
      </a-form-item>
      <!-- 模板文件需要以下额外属性 -->
      <template v-if="modelRef.type === 2">
        <a-form-item label="引擎">
          <a-select v-model:value="modelRef.engineType">
            <a-select-option :value="1">velocity</a-select-option>
          </a-select>
        </a-form-item>
      </template>
      <a-form-item label="备注">
        <a-textarea v-model:value="modelRef.remarks" placeholder="备注" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
  import { reactive, ref, toRaw } from 'vue'
  import { usePopup } from '@/hooks/popupHooks'
  import { doRequest } from '@/utils/axios/request'
  import { useForm } from 'ant-design-vue/es/form'
  import { addTemplateEntry, updateTemplateEntry } from '@/api/gen/templateentry'
  import { pick } from 'lodash-es'
  import {
    TemplateEntry,
    TemplateEntryDTO,
    TemplateEntryTypeEnum
  } from '@/api/gen/model/templateEntry'
  import type { TemplateEntryFormModalInstance } from '@/views/gen/templategroup/components/types'

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

  // 表单类型是否是新建
  const isCreate = ref<boolean>(false)

  // 弹窗标题
  const title = ref<string>('')

  // 当前新建目录项的父文件名
  const parentFileName = ref<string>('')

  //  弹窗相关
  const { visible, handleOpen, handleClose } = usePopup()

  const modelRef = reactive<TemplateEntryDTO>({
    id: undefined,
    groupId: undefined,
    parentId: undefined,
    type: TemplateEntryTypeEnum.FOLDER,
    fileName: '',
    engineType: 1,
    remarks: ''
  })

  // 提交按钮的 loading 状态控制
  const submitLoading = ref<boolean>(false)

  const { validate, resetFields } = useForm(modelRef)

  function handleSubmit() {
    validate().then(() => {
      // 如果是文件夹，则删除 templateInfo 属性
      if (modelRef.type === TemplateEntryTypeEnum.FOLDER) {
        delete modelRef.templateInfo
      }
      const requestFunction = isCreate.value ? addTemplateEntry : updateTemplateEntry
      doRequest(requestFunction(modelRef), {
        onSuccess() {
          emits('done')
          handleClose()
        }
      })
    })
  }

  defineExpose<TemplateEntryFormModalInstance>({
    add(currentParentFileName: string, record: TemplateEntry) {
      isCreate.value = true
      title.value = record.type === 1 ? '新建文件夹' : '新建模板文件'
      resetFields()
      Object.assign(modelRef, pick(record, Object.keys(toRaw(modelRef))))
      parentFileName.value = currentParentFileName
      handleOpen()
    },
    update(record: TemplateEntry) {
      isCreate.value = false
      resetFields()
      Object.assign(modelRef, pick(record, Object.keys(toRaw(modelRef))))
      title.value = record.type === 1 ? '编辑文件夹' : '编辑模板文件'
      handleOpen()
    }
  })
</script>

<style scoped></style>
