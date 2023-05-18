<template>
  <a-modal
    v-model:open="visible"
    :title="title"
    :confirm-loading="submitLoading"
    @ok="handleSubmit"
    @cancel="handleClose"
  >
    <a-form :label-col="labelCol" :wrapper-col="wrapperCol">
      <a-form-item v-if="isCreate" label="父目录">
        <span> {{ parentFilename }}</span>
      </a-form-item>
      <a-form-item label="文件名" v-bind="validateInfos.filename">
        <a-input v-model:value="modelRef.filename" placeholder="请输入文件名" />
      </a-form-item>
      <a-form-item label="文件类型">
        <a-radio-group v-model:value="modelRef.type" name="radioGroup">
          <a-radio-button :value="TemplateEntryTypeEnum.FOLDER">文件夹</a-radio-button>
          <a-radio-button :value="TemplateEntryTypeEnum.TEMPLATE_FILE">模板文件</a-radio-button>
          <a-radio-button :value="TemplateEntryTypeEnum.BINARY_FILE">二进制文件</a-radio-button>
        </a-radio-group>
      </a-form-item>
      <!-- 模板文件需要以下额外属性 -->
      <template v-if="modelRef.type === TemplateEntryTypeEnum.TEMPLATE_FILE">
        <a-form-item label="模板引擎" v-bind="validateInfos.engineType">
          <a-radio-group v-model:value="modelRef.engineType">
            <a-radio :value="1">Velocity</a-radio>
            <a-radio :value="2">Freemarker</a-radio>
          </a-radio-group>
        </a-form-item>
      </template>
      <!-- 二进制文件需要进行文件上传 -->
      <template v-if="modelRef.type === TemplateEntryTypeEnum.BINARY_FILE">
        <a-form-item label="文件上传">
          <a-upload :file-list="fileList" :before-upload="selectFile" :max-count="1">
            <a-button>
              <UploadOutlined />
              上传文件
            </a-button>
          </a-upload>
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
import type { TemplateEntry, TemplateEntryDTO } from '@/api/gen/template-entry/types'
import { TemplateEntryTypeEnum } from '@/api/gen/template-entry/types'
import { UploadOutlined } from '@ant-design/icons-vue'
import type { TemplateEntryFormModalInstance } from '@/views/gen/template-group/components/types'
import type { UploadFile } from 'ant-design-vue/lib/upload/interface'
import type { UploadProps } from 'ant-design-vue'

const emits = defineEmits<{
  (e: 'done'): void
}>()

const labelCol = {
  sm: { span: 24 },
  md: { span: 4 }
}
const wrapperCol = {
  sm: { span: 24 },
  md: { span: 19 }
}

// 表单类型是否是新建
const isCreate = ref<boolean>(false)

// 弹窗标题
const title = ref<string>('')

// 当前新建目录项的父文件名
const parentFilename = ref<string>('')

//  弹窗相关
const { visible, handleOpen, handleClose } = usePopup()

// 上传文件列表
const fileList = ref<UploadProps['fileList']>([])
function selectFile(file: UploadFile) {
  if (!modelRef.filename) {
    modelRef.filename = file.name
  }
  fileList.value = [file]
  return false
}

const modelRef = reactive<TemplateEntryDTO>({
  id: undefined,
  groupKey: undefined,
  parentId: undefined,
  type: TemplateEntryTypeEnum.FOLDER,
  templateContent: '',
  filename: '',
  engineType: 1,
  remarks: ''
})

const rulesRef = reactive({
  filename: [{ required: true, message: '请输入文件名' }],
  engineType: [
    {
      required: modelRef.type === TemplateEntryTypeEnum.TEMPLATE_FILE,
      message: '请选择模板引擎类型'
    }
  ]
})

// 提交按钮的 loading 状态控制
const submitLoading = ref<boolean>(false)

const { validate, resetFields, validateInfos } = useForm(modelRef, rulesRef)

function handleSubmit() {
  validate().then(() => {
    // 如果是文件夹，则删除 engineType 属性
    if (modelRef.type === TemplateEntryTypeEnum.FOLDER) {
      delete modelRef.engineType
    }
    doRequest({
      request: isCreate.value
        ? addTemplateEntry(modelRef, fileList.value?.[0])
        : updateTemplateEntry(modelRef, fileList.value?.[0]),
      successMessage: '保存成功！',
      onSuccess() {
        emits('done')
        handleClose()
      }
    })
  })
}

function mapTypeName(entryType: TemplateEntryTypeEnum) {
  switch (entryType) {
    case TemplateEntryTypeEnum.FOLDER:
      return '文件夹'
    case TemplateEntryTypeEnum.TEMPLATE_FILE:
      return '模板文件'
    case TemplateEntryTypeEnum.BINARY_FILE:
      return '二进制文件'
  }
}

defineExpose<TemplateEntryFormModalInstance>({
  add(currentParentFileName: string, record: TemplateEntry) {
    isCreate.value = true
    title.value = '新建' + mapTypeName(record.type)
    resetFields()
    fileList.value = []
    Object.assign(modelRef, pick(record, Object.keys(toRaw(modelRef))))
    parentFilename.value = currentParentFileName
    handleOpen()
  },
  update(record: TemplateEntry) {
    isCreate.value = false
    title.value = '编辑' + mapTypeName(record.type)
    resetFields()
    fileList.value = []
    Object.assign(modelRef, pick(record, Object.keys(toRaw(modelRef))))
    handleOpen()
  }
})
</script>

<style scoped></style>
