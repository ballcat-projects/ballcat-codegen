<template>
  <a-spin :spinning="loading">
    <a-form
      ref="formRef"
      :label-col="{ span: 6 }"
      :wrapper-col="{ span: 12 }"
      :model="modelRef"
      :loading="loading"
      @finish="submitForm"
    >
      <a-form-item v-if="isUpdate" name="id" style="display: none">
        <a-input v-model:value="modelRef.id" />
      </a-form-item>
      <a-form-item name="groupKey" style="display: none">
        <a-input v-model:value="modelRef.groupKey" />
      </a-form-item>
      <a-form-item label="属性标题" name="title">
        <a-input v-model:value="modelRef.title" placeholder="请输入" />
      </a-form-item>
      <a-form-item label="属性键" name="propKey">
        <a-input v-model:value="modelRef.propKey" placeholder="请输入" />
      </a-form-item>
      <a-form-item label="默认值" name="defaultValue">
        <a-input v-model:value="modelRef.defaultValue" placeholder="请输入" />
      </a-form-item>
      <a-form-item label="属性控件" name="componentType">
        <a-radio-group v-model:value="modelRef.componentType">
          <a-radio :value="ComponentType.INPUT">Input</a-radio>
          <a-radio :value="ComponentType.INPUT_NUMBER">Input Number</a-radio>
          <a-radio :value="ComponentType.SELECT">Select</a-radio>
          <a-radio :value="ComponentType.RADIO">Radio</a-radio>
        </a-radio-group>
      </a-form-item>
      <a-form-item
        v-if="
          modelRef.componentType === ComponentType.SELECT ||
          modelRef.componentType === ComponentType.RADIO
        "
        label="选项配置"
      >
        <a-space
          v-for="(option, index) in modelRef.componentOptions"
          :key="option.value"
          style="display: flex; margin-bottom: 8px"
          align="baseline"
        >
          <a-form-item
            :name="['componentOptions', index, 'name']"
            :rules="{
              required: true,
              message: '请输入选项的名称'
            }"
          >
            <a-input v-model:value="option.name" placeholder="Name" />
          </a-form-item>
          <a-form-item
            :name="['componentOptions', index, 'value']"
            :rules="{
              required: true,
              message: '请输入选项的值'
            }"
          >
            <a-input v-model:value="option.value" placeholder="Value" />
          </a-form-item>
          <MinusCircleOutlined @click="removeOption(option)" />
        </a-space>
        <a-button type="dashed" block @click="addOption">
          <PlusOutlined />
          添加选项
        </a-button>
      </a-form-item>
      <a-form-item label="排序值" name="orderValue">
        <a-input-number v-model:value="modelRef.orderValue" placeholder="请输入" />
      </a-form-item>
      <a-form-item label="是否必填" name="required">
        <a-switch
          v-model:checked="modelRef.required"
          :checked-value="1"
          :un-checked-value="0"
          checked-children="是"
          un-checked-children="否"
        />
      </a-form-item>
      <a-form-item label="备注信息" name="remarks">
        <a-textarea v-model:value="modelRef.remarks" placeholder="请输入" />
      </a-form-item>
      <a-form-item label=" " :colon="false">
        <a-space>
          <a-button type="primary" html-type="submit" :loading="loading">保存</a-button>
          <a-button @click="handleCancel">取消</a-button>
        </a-space>
      </a-form-item>
    </a-form>
  </a-spin>
</template>

<script setup lang="ts">
import { MinusCircleOutlined, PlusOutlined } from '@ant-design/icons-vue'
import { computed, reactive, ref, toRaw } from 'vue'
import type { FormInstance } from 'ant-design-vue'
import type { TemplateProperty } from '@/api/gen/template-property/types'
import { ComponentType } from '@/api/gen/template-property/types'
import { copyProperties } from '@/utils/bean-util'
import { addTemplateProperty, updateTemplateProperty } from '@/api/gen/template-property'
import { doRequest } from '@/utils/axios/request'

const emits = defineEmits<{
  (e: 'back-page', reloadTable: boolean): void
}>()

interface Option {
  name: string
  value: string
}

const loading = ref(false)

const formRef = ref<FormInstance>()

const formAction = ref('CREATE')
const isCreate = computed(() => formAction.value === 'CREATE')
const isUpdate = computed(() => formAction.value === 'UPDATE')

const modelRef = reactive<TemplateProperty>({
  id: undefined,
  groupKey: '',
  title: '',
  propKey: '',
  defaultValue: '',
  required: 0,
  componentOptions: [],
  componentType: ComponentType.INPUT,
  orderValue: 0,
  remarks: ''
})

const removeOption = (item: Option) => {
  const index = modelRef.componentOptions.indexOf(item)
  if (index !== -1) {
    modelRef.componentOptions.splice(index, 1)
  }
}

const addOption = () => {
  modelRef.componentOptions.push({
    name: '',
    value: ''
  })
}

function resetForm() {
  formRef.value?.resetFields()
  modelRef.componentOptions = []
}

function submitForm() {
  loading.value = true
  const request = isCreate.value
    ? addTemplateProperty(toRaw(modelRef))
    : updateTemplateProperty(toRaw(modelRef))
  doRequest({
    request: request,
    successMessage: isCreate.value ? '新建成功！' : '修改成功！',
    onSuccess: () => backToPage(true),
    onFinally: () => (loading.value = false)
  })
}

function handleCancel() {
  backToPage(false)
}

function backToPage(reloadTable: boolean) {
  emits('back-page', reloadTable)
}

defineExpose({
  create(groupKey: string) {
    formAction.value = 'CREATE'
    resetForm()
    modelRef.id = undefined
    modelRef.groupKey = groupKey
  },
  update: (record: TemplateProperty) => {
    formAction.value = 'UPDATE'
    resetForm()
    copyProperties(modelRef, record)
  }
})
</script>

<script lang="ts">
export default {
  name: 'TemplatePropertyEditPage'
}
</script>

<style scoped></style>
