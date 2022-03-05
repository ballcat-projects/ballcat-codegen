<template>
  <a-modal
    :title="title"
    :visible="visible"
    :mask-closable="false"
    :body-style="{ paddingBottom: '8px' }"
    :confirm-loading="submitLoading"
    :width="500"
    @ok="handleSubmit"
    @cancel="handleClose"
  >
    <a-form :label-col="labelCol" :wrapper-col="wrapperCol">
      <a-form-item v-if="isUpdate" style="display: none">
        <a-input v-model:value="modelRef.id" />
      </a-form-item>

      <a-form-item label="名称" v-bind="validateInfos.name">
        <a-input v-model:value="modelRef.name" placeholder="数据源名称" />
      </a-form-item>

      <a-form-item label="用户名" v-bind="validateInfos.username">
        <a-input v-model:value="modelRef.username" placeholder="用户名" />
      </a-form-item>

      <a-form-item v-if="isUpdate" label="原密码">
        <a-input v-model:value="modelRef.password" disabled />
      </a-form-item>

      <a-form-item label="密码" v-bind="validateInfos.pass">
        <template v-if="isUpdate" #extra>
          <p style="color: red; margin-bottom: 0">注意：如果需要修改密码则填写此处，不修改请置空</p>
        </template>
        <a-input v-model:value="modelRef.pass" placeholder="密码" />
      </a-form-item>

      <a-form-item label="连接地址" v-bind="validateInfos.url">
        <a-textarea v-model:value="modelRef.url" placeholder="连接地址" :rows="4" />
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
  let emits = defineEmits<{
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
    name: '',
    username: '',
    password: '',
    url: ''
  })

  // 表单校验规则
  const rulesRef = computed(() => {
    return {
      name: [{ required: true, message: '请输入数据源名称!' }],
      username: [{ required: true, message: '请输入用户名!' }],
      pass: isUpdate.value ? [] : [{ required: true, message: '请输入密码!' }],
      url: [{ required: true, message: '请输入连接地址!' }]
    }
  })

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
