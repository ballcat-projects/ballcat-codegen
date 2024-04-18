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

      <a-form-item label="数据库类型" v-bind="validateInfos.dbType">
        <a-select v-model:value="modelRef.dbType" :placeholder="'数据库类型'">
          <a-select-option v-for="(value, key) in DbType" :key="key" :value="value">{{ key }}</a-select-option>
        </a-select>
      </a-form-item>

      <a-form-item
        label="模板组标识"
        v-bind="validateInfos.groupKey"
        extra="用户自定义的模板组标识"
      >
        <a-input v-model:value="modelRef.groupKey" placeholder="模板组标识" />
      </a-form-item>

      <a-form-item label="代码属性值" v-bind="validateInfos.columnValue">
        <a-input v-model:value="modelRef.columnValue" placeholder="DB对应代码属性值"/>
      </a-form-item>

      <a-form-item label="DB属性类型" v-bind="validateInfos.columnKey">
        <a-input v-model:value="modelRef.columnKey" placeholder="DB属性类型" />
      </a-form-item>

      <a-form-item label="属性包路径" v-bind="validateInfos.packageName">
        <a-input v-model:value="modelRef.packageName" placeholder="属性包路径+类名" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { addFieldType, updateFieldType } from '@/api/gen/field-type'
import { copyProperties } from '@/utils/bean-util'
import { usePopup } from '@/hooks/popup'
// 类型导入
import type { FieldType } from '@/api/gen/field-type/types'
import type { FieldTypeEditModalInstance } from './types'
import { useForm } from 'ant-design-vue/es/form'
import { doRequest } from '@/utils/axios/request'
import { DbType } from '@/api/gen/field-type/types'

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
const modelRef = reactive<FieldType>({
  id: undefined,
  groupKey: '',
  columnKey: '',
  dbType: '',
  columnValue: '',
  packageName: ''
})

// 表单校验规则
const rulesRef = computed(() => {
  return {
    groupKey: [{required: true, message: '请输入模板组标识！'}],
    columnKey: [{required: true, message: '请输入DB属性类型!'}],
    dbType: [{required: true, message: '请选择数据库类型!'}],
    columnValue: [{required: true, message: '请输入Java对应类型!'}],
    packageName: [{validator: validRule}]
  }
})

const validRule = (rule: any, value: string) => {
  if (value) {
    if (!/^[a-zA-Z.]+$/.test(value)) {
      return Promise.reject('只能输入字母和点号')
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
    const reqFunction = isUpdate.value ? updateFieldType : addFieldType
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
  title.value = '新建数据类型'
  resetFields()
  handleOpen()
}
const update = (record: FieldType) => {
  isUpdate.value = true
  title.value = '修改数据类型'
  resetFields()
  handleOpen()
  copyProperties(modelRef, record)
}

defineExpose<FieldTypeEditModalInstance>({
  add,
  update
})
</script>
