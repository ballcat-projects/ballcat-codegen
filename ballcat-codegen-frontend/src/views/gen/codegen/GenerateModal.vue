<template>
  <a-modal
    title="属性配置"
    cancel-text="取消"
    :visible="visible"
    :body-style="{ paddingBottom: '8px' }"
    :confirm-loading="submitLoading"
    :width="900"
    @ok="generateCode"
    @cancel="handleClose"
  >
    <a-form>
      <a-row :gutter="6">
        <a-col :span="10">
          <a-form-item
            label="模板组"
            :label-col="labelCol"
            :wrapper-col="wrapperCol"
            v-bind="validateInfos.templateGroupId"
          >
            <a-select v-model:value="modelRef.templateGroupId">
              <a-select-option v-for="item in templateGroupSelectData" :key="Number(item.value)">
                {{ item.name }}
              </a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="生成文件选择">
            <div :style="{ borderBottom: '1px solid #E9E9E9' }">
              <a-checkbox
                :indeterminate="templateFileIdsState.indeterminate"
                :checked="templateFileIdsState.checkAll"
                @change="onCheckAllChange"
              >
                Check all
              </a-checkbox>
            </div>
            <a-checkbox-group
              v-model:value="modelRef.templateFileIds"
              style="width: 100%"
              @change="onTemplateFileIdsChange"
            >
              <a-row>
                <template v-for="item in templateFiles" :key="item.id">
                  <a-col :span="12">
                    <a-checkbox :value="item.directoryEntryId">
                      {{ item.title }}
                    </a-checkbox>
                  </a-col>
                </template>
              </a-row>
            </a-checkbox-group>
          </a-form-item>
        </a-col>
        <a-col :span="14">
          <a-divider orientation="left"> 系统属性 </a-divider>
          <a-form-item label="表前缀" :label-col="labelCol" :wrapper-col="wrapperCol">
            <a-input
              v-model:value="modelRef.tablePrefix"
              placeholder="填写则会将表名的前缀截取后，再生成类名"
            />
          </a-form-item>
          <a-divider orientation="left"> 自定义属性 </a-divider>
          <template v-for="item in properties" :key="item.id">
            <a-form-item
              :label="item.propKey"
              :label-col="labelCol"
              :wrapper-col="wrapperCol"
              v-bind="validateInfos['genProperties.' + item.propKey]"
            >
              <a-input
                v-model:value="modelRef.genProperties[item.propKey]"
                :placeholder="'请输入' + item.title"
              />
            </a-form-item>
          </template>
        </a-col>
      </a-row>
    </a-form>

    <template #footer>
      <a-button @click="handleClose"> 取消 </a-button>
      <a-button @click="previewCode"> 预览 </a-button>
      <a-button type="primary" @click="generateCode"> 确认 </a-button>
    </template>
  </a-modal>

  <generate-preview-modal ref="previewModal" />
</template>

<script setup lang="ts">
  import { reactive, ref, watchEffect } from 'vue'
  import { usePopup } from '@/hooks/popupHooks'
  import { listSelectData } from '@/api/gen/templategroup'
  import { listProperty } from '@/api/gen/templateproperty'
  import { listTemplateInfo } from '@/api/gen/templateinfo'
  import { generate, preview } from '@/api/gen/generate'
  import { doRequest } from '@/utils/axios/request'
  import { remoteFileDownload } from '@/utils/fileUtil'
  import { Form, message } from 'ant-design-vue'
  import GeneratePreviewModal from '@/views/gen/codegen/GeneratePreviewModal.vue'
  import type { SelectData } from '@/api/types'
  import type { GeneratorOption } from '@/api/gen/model/generate'
  import { TemplateProperty } from '@/api/gen/model/templateproperty'
  import { Props } from 'ant-design-vue/es/form/useForm'
  import { GenerateModalInstance, PreviewModalInstance } from '@/views/gen/codegen/types'

  const labelCol = {
    xs: { span: 12 },
    sm: { span: 24 },
    lg: { span: 6 }
  }
  const wrapperCol = {
    xs: { span: 12 },
    sm: { span: 24 },
    lg: { span: 18 }
  }

  // 弹窗属性
  const { visible, handleOpen, handleClose } = usePopup()

  const dsName = ref<string>('')

  // 表单提交的 loading 控制
  const submitLoading = ref<boolean>(false)

  // 表单数据
  const modelRef = reactive<GeneratorOption>({
    tableNames: [],
    templateGroupId: null,
    templateFileIds: [],
    tablePrefix: '',
    genProperties: {}
  })
  // 校验配置
  const rulesRef = reactive<Props>({
    templateGroupId: [{ required: true, message: '必须选择一个模板组' }]
  })
  const { validate, validateInfos } = Form.useForm(modelRef, rulesRef)

  const templateGroupSelectData = ref<SelectData[]>([])
  doRequest(listSelectData(), {
    successMessage: false,
    onSuccess: res => {
      let data = res.data as SelectData[]
      templateGroupSelectData.value = data
      if (data && data.length > 0) {
        modelRef.templateGroupId = Number(data[0].value)
      }
    }
  })

  const templateFileIdsState = reactive({
    indeterminate: false,
    checkAll: true,
    allTemplateFileIds: [] as number[]
  })
  const onCheckAllChange = (e: any): void => {
    templateFileIdsState.indeterminate = false
    templateFileIdsState.checkAll = e.target.checked
    modelRef.templateFileIds = templateFileIdsState.checkAll
      ? templateFileIdsState.allTemplateFileIds
      : []
  }
  const onTemplateFileIdsChange = (checkedList: number[]): void => {
    const allTemplateFileIds = templateFileIdsState.allTemplateFileIds
    templateFileIdsState.indeterminate =
      !!checkedList.length && checkedList.length < allTemplateFileIds.length
    templateFileIdsState.checkAll = checkedList.length === allTemplateFileIds.length
  }

  const properties = ref<TemplateProperty[]>()
  const templateFiles = ref()
  watchEffect(() => {
    const templateGroupId = modelRef.templateGroupId
    if (templateGroupId) {
      doRequest(listProperty(templateGroupId), {
        successMessage: false,
        onSuccess: res => {
          properties.value = res.data
          if (properties.value && properties.value.length > 0) {
            for (let property of properties.value) {
              modelRef.genProperties[property.propKey as string] = property.defaultValue
              rulesRef['genProperties.' + property.propKey] = [
                {
                  required: property.required === 1,
                  message: property.title + '不能为空'
                }
              ]
            }
          }
        }
      })
      doRequest(listTemplateInfo(templateGroupId), {
        successMessage: false,
        onSuccess: res => {
          templateFiles.value = res.data
          templateFileIdsState.allTemplateFileIds = res.data
            ? res.data.map(x => x.directoryEntryId)
            : []
          modelRef.templateFileIds = templateFileIdsState.allTemplateFileIds
        }
      })
    }
  })

  /* 代码生成 */
  const generateCode = () => {
    validate()
      .then(() => {
        submitLoading.value = true
        generate(dsName.value, modelRef)
          .then(response => {
            remoteFileDownload(response, 'BallCat-CodeGen.zip')
          })
          .catch(() => {
            message.error('代码生成异常')
          })
          .finally(() => {
            submitLoading.value = false
          })
      })
      .catch(err => {
        console.log('error', err)
      })
  }

  // 预览弹窗
  const previewModal = ref<PreviewModalInstance>()

  /* 代码预览 */
  const previewCode = () => {
    validate()
      .then(() => {
        submitLoading.value = true
        doRequest(preview(dsName.value, modelRef), {
          successMessage: false,
          onSuccess: res => {
            previewModal.value?.open(res.data)
          },
          onFinally: () => (submitLoading.value = false)
        })
      })
      .catch(err => {
        console.log('error', err)
      })
  }

  defineExpose<GenerateModalInstance>({
    open: (adsName: string, tableNames: string[]): void => {
      dsName.value = adsName
      modelRef.tableNames = tableNames
      handleOpen()
    },
    close: handleClose
  })
</script>

<style scoped></style>
