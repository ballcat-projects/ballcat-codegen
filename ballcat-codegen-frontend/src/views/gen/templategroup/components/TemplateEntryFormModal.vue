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
        <a-form-item label="标题">
          <a-input v-model:value="modelRef.templateInfo.title" placeholder="标题" />
        </a-form-item>
        <a-form-item label="引擎">
          <a-select v-model:value="modelRef.templateInfo.engineType">
            <a-select-option :value="1">velocity</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="备注">
          <a-textarea v-model:value="modelRef.templateInfo.remarks" placeholder="备注" />
        </a-form-item>
      </template>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
  import { usePopup } from '@/hooks/popupHooks'
  import { reactive, ref, toRaw } from 'vue'
  import {
    TemplateDirectoryEntry,
    TemplateDirectoryEntryDTO,
    TemplateEntryTypeEnum
  } from '@/api/gen/model/templatedirectoryentry'
  import { addTemplateEntry, updateTemplateEntry } from '@/api/gen/templatedirectoryentry'
  import { getTemplateInfo } from '@/api/gen/templateinfo'
  import { doRequest } from '@/utils/axios/request'
  import { TemplateInfo } from '@/api/gen/model/templateinfo'
  import { pick } from 'lodash-es'
  import { TemplateEntryFormModalInstance } from '@/views/gen/templategroup/components/types'
  import { useForm } from 'ant-design-vue/es/form'

  let emits = defineEmits<{
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

  const modelRef = reactive<TemplateDirectoryEntryDTO>({
    id: undefined,
    groupId: undefined,
    parentId: undefined,
    type: TemplateEntryTypeEnum.FOLDER,
    fileName: '',
    templateInfo: {
      title: '',
      engineType: 1,
      remarks: ''
    }
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
    add(currentParentFileName: string, record: TemplateDirectoryEntry) {
      isCreate.value = true
      title.value = record.type === 1 ? '新建文件夹' : '新建模板文件'
      resetFields()
      Object.assign(modelRef, pick(record, Object.keys(toRaw(modelRef))))
      parentFileName.value = currentParentFileName
      handleOpen()
    },
    update(record: TemplateDirectoryEntry) {
      isCreate.value = false
      resetFields()
      Object.assign(modelRef, pick(record, Object.keys(toRaw(modelRef))))

      if (record.type === 1) {
        title.value = '编辑文件夹'
        handleOpen()
      } else {
        title.value = '编辑模板文件'
        doRequest(getTemplateInfo(record.id), {
          successMessage: false,
          onSuccess(res) {
            const fileInfo = res.data as TemplateInfo
            modelRef.templateInfo = {
              title: fileInfo.title,
              engineType: fileInfo.engineType,
              remarks: fileInfo.remarks
            }
            handleOpen()
          }
        })
      }
    }
  })
</script>

<style scoped></style>
