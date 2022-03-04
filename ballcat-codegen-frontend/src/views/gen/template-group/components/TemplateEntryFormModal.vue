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
        <span> {{ parentFilename }}</span>
      </a-form-item>
      <a-form-item label="文件名">
        <a-input v-model:value="modelRef.filename" placeholder="请输入文件名" />
      </a-form-item>
      <!-- 模板文件需要以下额外属性 -->
      <template v-if="modelRef.type === 2">
        <a-form-item label="引擎">
          <a-radio-group v-model:value="modelRef.engineType">
            <a-radio :value="1">Velocity</a-radio>
            <a-radio :value="2">Freemarker</a-radio>
          </a-radio-group>
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
  import { usePopup } from '@/hooks/popup'
  import { doRequest } from '@/utils/axios/request'
  import { useForm } from 'ant-design-vue/es/form'
  import { addTemplateEntry, updateTemplateEntry } from '@/api/gen/template-entry'
  import { pick } from 'lodash-es'
  import {
    TemplateEntry,
    TemplateEntryDTO,
    TemplateEntryTypeEnum
  } from '@/api/gen/template-entry/types'
  import type { TemplateEntryFormModalInstance } from '@/views/gen/template-group/components/types'

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
  const parentFilename = ref<string>('')

  //  弹窗相关
  const { visible, handleOpen, handleClose } = usePopup()

  const modelRef = reactive<TemplateEntryDTO>({
    id: undefined,
    groupId: undefined,
    parentId: undefined,
    type: TemplateEntryTypeEnum.FOLDER,
    filename: '',
    engineType: 1,
    remarks: ''
  })

  // 提交按钮的 loading 状态控制
  const submitLoading = ref<boolean>(false)

  const { validate, resetFields } = useForm(modelRef)

  function handleSubmit() {
    validate().then(() => {
      // 如果是文件夹，则删除 engineType 属性
      if (modelRef.type === TemplateEntryTypeEnum.FOLDER) {
        delete modelRef.engineType
      }
      doRequest({
        request: isCreate.value ? addTemplateEntry(modelRef) : updateTemplateEntry(modelRef),
        successMessage: '保存成功！',
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
      parentFilename.value = currentParentFileName
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
