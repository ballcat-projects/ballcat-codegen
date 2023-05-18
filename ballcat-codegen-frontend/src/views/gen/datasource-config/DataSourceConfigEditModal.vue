<template>
  <a-modal
    v-model:open="visible"
    :title="title"
    :mask-closable="false"
    :body-style="{ paddingBottom: '8px' }"
    :confirm-loading="submitLoading"
    :width="520"
    @ok="handleSubmit"
    @cancel="handleClose"
  >
    <a-form :label-col="labelCol" :wrapper-col="wrapperCol">
      <a-form-item v-if="isUpdate" style="display: none">
        <a-input v-model:value="modelRef.id" />
      </a-form-item>

      <a-form-item label="标题" v-bind="validateInfos.title">
        <a-input v-model:value="modelRef.title" placeholder="数据源标题" />
      </a-form-item>

      <a-form-item
        label="dsKey"
        v-bind="validateInfos.dsKey"
        extra="用户自定义的唯一标识，切换数据源时使用，例如：db1"
      >
        <a-input v-model:value="modelRef.dsKey" placeholder="数据源dsKey" />
      </a-form-item>

      <a-form-item label="用户名" v-bind="validateInfos.username">
        <a-input v-model:value="modelRef.username" placeholder="数据库用户名" />
      </a-form-item>

      <a-form-item v-if="isUpdate" label="原密码">
        <a-input v-model:value="modelRef.password" disabled />
      </a-form-item>

      <a-form-item label="密码" v-bind="validateInfos.pass">
        <template v-if="isUpdate" #extra>
          <p style="color: red; margin-bottom: 0">注意：如果需要修改密码则填写此处，不修改请置空</p>
        </template>
        <a-input v-model:value="modelRef.pass" placeholder="数据库密码" />
      </a-form-item>

      <a-form-item label="连接地址" v-bind="validateInfos.url">
        <a-textarea v-model:value="modelRef.url" placeholder="jdbc url" :rows="4" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { addDatasourceConfig, updateDatasourceConfig } from '@/api/gen/datasource-config'
import { copyProperties } from '@/utils/bean-util'
import { usePopup } from '@/hooks/popup'
// 类型导入
import type { DataSourceConfig } from '@/api/gen/datasource-config/types'
import type { DataSourceConfigEditModalInstance } from './types'
import { useForm } from 'ant-design-vue/es/form'
import { doRequest } from '@/utils/axios/request'

// 定义事件
const emits = defineEmits<{
  // 提交完成事件
  (e: 'done'): void
}>()

const title = ref('')

// 是否是更新
const isUpdate = ref(false)

// 弹窗显示隐藏
const { visible, handleOpen, handleClose } = usePopup()

// 表单的布局设置
const labelCol = {
  xs: { span: 12 },
  sm: { span: 24 },
  lg: { span: 5 }
}
const wrapperCol = {
  xs: { span: 12 },
  sm: { span: 24 },
  lg: { span: 18 }
}

// 表单绑定数据
const modelRef = reactive<DataSourceConfig & { pass?: string }>({
  id: undefined,
  pass: '',
  title: '',
  dsKey: '',
  username: '',
  password: '',
  url: ''
})

// 表单校验规则
const rulesRef = computed(() => {
  return {
    title: [{ required: true, message: '请输入数据源标题！' }],
    dsKey: [{ required: true, message: '请输入数据源dsKey!' }, { validator: validRule }],
    username: [{ required: true, message: '请输入用户名!' }],
    pass: isUpdate.value ? [] : [{ required: true, message: '请输入密码!' }],
    url: [{ required: true, message: '请输入连接地址!' }]
  }
})

const validRule = (rule: any, value: string) => {
  if (value) {
    if (/[^\a-\z\A-\Z0-9\_-]/g.test(value)) {
      return Promise.reject('只能输入字母、字母、下划线和中划线')
    }
  }
  return Promise.resolve()
}

// 提交按钮的 loading 状态控制
const submitLoading = ref<boolean>(false)

const { validate, validateInfos, resetFields } = useForm(modelRef, rulesRef)

/* 表单提交 */
function handleSubmit() {
  validate().then(() => {
    const reqFunction = isUpdate.value ? updateDatasourceConfig : addDatasourceConfig
    delete modelRef.password
    if (!isUpdate.value) {
      delete modelRef.id
    }
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

const add = () => {
  isUpdate.value = false
  title.value = '新建数据源'
  resetFields()
  handleOpen()
}
const update = (record: DataSourceConfig) => {
  isUpdate.value = true
  title.value = '修改数据源'
  resetFields()
  handleOpen()
  copyProperties(modelRef, record)
}

defineExpose<DataSourceConfigEditModalInstance>({
  add,
  update
})
</script>
