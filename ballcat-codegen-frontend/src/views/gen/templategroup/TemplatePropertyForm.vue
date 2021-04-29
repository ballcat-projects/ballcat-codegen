<template>
  <a-form
    :form="form"
    :label-col="labelCol"
    :wrapper-col="wrapperCol"
    @submit="handleSubmit"
  >
    <a-form-item v-if="isUpdateForm" style="display: none">
      <a-input v-decorator="['id']" />
    </a-form-item>

    <a-form-item v-if="isCreateForm" style="display: none">
      <a-input v-decorator="['groupId']" />
    </a-form-item>

    <a-form-item label="标题">
      <a-input v-decorator="['title']" placeholder="标题" />
    </a-form-item>

    <a-form-item label="属性键">
      <a-input v-decorator="['propKey']" placeholder="属性键" />
    </a-form-item>

    <a-form-item label="默认值">
      <a-input v-decorator="['defaultValue']" placeholder="默认值(可置空)" />
    </a-form-item>

    <a-form-item label="是否必填">
      <a-select v-decorator="['required']" placeholder="必填，1：是，0：否">
        <a-select-option :value="1">是</a-select-option>
        <a-select-option :value="0">否</a-select-option>
      </a-select>
    </a-form-item>

    <a-form-item label="备注信息">
      <a-textarea v-decorator="['remarks']" placeholder="备注信息" rows="4" />
    </a-form-item>

    <a-form-item :wrapper-col="{ offset: 3 }">
      <a-button html-type="submit" type="primary" :loading="submitLoading">提交</a-button>
      <a-button style="margin-left: 8px" @click="backToPage(false)">取消</a-button>
    </a-form-item>
  </a-form>
</template>

<script>
import { PageFormMixin } from '@/mixins'
import { addObj, putObj } from '@/api/gen/templateproperty'

export default {
  name: 'TemplatePropertyFormPage',
  mixins: [PageFormMixin],
  data() {
    return {
      reqFunctions: {
        create: addObj,
        update: putObj
      },

      labelCol: {
        sm: { span: 24 },
        md: { span: 3 }
      },
      wrapperCol: {
        sm: { span: 24 },
        md: { span: 20 }
      },

      // 校验配置
      decoratorOptions: {}
    }
  },
  methods: {
    createdFormCallback(attribute) {
      if (this.isCreateForm) {
        this.fillFormData({ groupId: attribute.groupId })
      }
    }
  }
}
</script>
<style scoped>
.ant-form-item {
  margin-bottom: 8px;
}
</style>
