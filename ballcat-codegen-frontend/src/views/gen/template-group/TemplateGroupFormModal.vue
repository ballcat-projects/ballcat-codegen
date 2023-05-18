<template>
  <a-modal
    v-model:open="visible"
    :title="title"
    :mask-closable="false"
    :body-style="{ paddingBottom: '8px' }"
    :confirm-loading="submitLoading"
    :width="480"
    @ok="handleSubmit"
    @cancel="handleClose"
  >
    <a-form :label-col="{ span: 6 }" :wrapper-col="{ span: 16 }">
      <a-form-item v-if="isUpdate" style="display: none">
        <a-input v-model:value="modelRef.id" />
      </a-form-item>
      <a-row v-if="isCopy" style="margin-bottom: 24px" :gutter="8">
        <a-col :span="6"><h3 style="text-align: right">复制来源:</h3></a-col>
        <a-col :span="16">
          <h3>{{ resourceGroupName }}</h3>
        </a-col>
      </a-row>
      <a-form-item label="唯一标识" v-bind="validateInfos.groupKey">
        <a-input v-model:value="modelRef.groupKey" placeholder="请输入" :disabled="isUpdate" />
      </a-form-item>
      <a-form-item label="名称" v-bind="validateInfos.name">
        <a-input v-model:value="modelRef.name" placeholder="请输入" />
      </a-form-item>
      <a-form-item label="图标">
        <div class="icon-uploader" @click="handleUpload">
          <img v-if="modelRef.icon" :src="modelRef.icon" alt="icon" style="height: 100%" />
          <div v-else>
            <plus-outlined />
            <div class="ant-upload-text">Upload</div>
          </div>
        </div>
      </a-form-item>
      <a-form-item label="需要数据表" v-bind="validateInfos.useTable">
        <a-radio-group v-model:value="modelRef.useTable">
          <a-radio :value="1">是</a-radio>
          <a-radio :value="0">否</a-radio>
        </a-radio-group>
      </a-form-item>
      <a-form-item label="备注信息">
        <a-textarea v-model:value="modelRef.remarks" placeholder="请输入" />
      </a-form-item>
    </a-form>
  </a-modal>

  <cropper-modal ref="cropperModalRef" :upload-processor="uploadProcess" />
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { usePopup } from '@/hooks/popup'
import { copyProperties } from '@/utils/bean-util'
import { addTemplateGroup, updateTemplateGroup, copyTemplateGroup } from '@/api/gen/template-group'
import { useForm } from 'ant-design-vue/es/form'
import { doRequest } from '@/utils/axios/request'
import CropperModal from '@/components/cropper-modal/index.vue'
import { PlusOutlined } from '@ant-design/icons-vue'

// 类型引入
import type { AxiosResponse } from 'axios'
import type { R } from '@/utils/axios/types'
import type { TemplateGroup } from '@/api/gen/template-group/types'
import type { TemplateGroupFormModalInstance } from '@/views/gen/template-group/types'

const cropperModalRef = ref()

// 定义事件
const emits = defineEmits<{
  (e: 'done'): void // 提交完成事件
}>()

const { visible, handleOpen, handleClose } = usePopup()

const resourceGroupKey = ref<string>()
const resourceGroupName = ref<string>()

const title = ref<string>('')

let reqFunction: (data: TemplateGroup) => Promise<AxiosResponse<R>>

const formAction = ref('ADD')
const isUpdate = computed(() => formAction.value === 'UPDATE')
const isCopy = computed(() => formAction.value === 'COPY')

const modelRef = reactive<TemplateGroup>({
  id: undefined,
  groupKey: undefined,
  name: '',
  icon: '',
  useTable: 1,
  remarks: ''
})

const rulesRef = reactive({
  name: [{ required: true, message: '模板组名称不能为空!' }],
  groupKey: [
    { required: true, message: '唯一标识不能为空' },
    {
      pattern: /^[a-zA-Z0-9_-]+$/,
      message: '只能由数字字母以及中划线下划线组成'
    },
    { min: 1, max: 50, message: '已超过最大长度 50' }
  ],
  useTable: [{ required: true, message: '请选择使用是否需要数据表支持!' }]
})

// 提交按钮的 loading 状态控制
const submitLoading = ref<boolean>(false)

const { validate, validateInfos, resetFields } = useForm(modelRef, rulesRef)

function handleUpload() {
  cropperModalRef.value?.open(modelRef.icon)
}

function uploadProcess(dataURL: string) {
  modelRef.icon = dataURL
  return Promise.resolve()
}

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
    resourceGroupKey.value = record.groupKey
    resourceGroupName.value = record.name
    reqFunction = (record: TemplateGroup) => {
      return copyTemplateGroup(resourceGroupKey.value!, record)
    }
  }
})
</script>

<style scoped lang="less">
.icon-uploader {
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  width: 104px;
  height: 104px;
  margin-right: 8px;
  margin-bottom: 8px;
  vertical-align: top;
  background-color: #fafafa;
  border: 1px dashed #d9d9d9;
  border-radius: 2px;
  cursor: pointer;
  transition: border-color 0.3s;
}
</style>
