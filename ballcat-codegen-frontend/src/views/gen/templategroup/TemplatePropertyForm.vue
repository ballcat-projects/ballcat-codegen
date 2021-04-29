<template>
  <a-form :form="form" @submit="handleSubmit">
    <a-form-item v-if="formAction === FORM_ACTION.UPDATE" style="display: none">
      <a-input v-decorator="['id']" />
    </a-form-item>

    <a-form-item v-if="formAction === FORM_ACTION.CREATE" style="display: none">
      <a-input v-decorator="['groupId']" />
    </a-form-item>

    <a-form-item label="标题" :label-col="labelCol" :wrapper-col="wrapperCol">
      <a-input v-decorator="['title']" placeholder="标题" />
    </a-form-item>

    <a-form-item label="属性键" :label-col="labelCol" :wrapper-col="wrapperCol">
      <a-input v-decorator="['propKey']" placeholder="属性键" />
    </a-form-item>

    <a-form-item label="默认值" :label-col="labelCol" :wrapper-col="wrapperCol">
      <a-input v-decorator="['defaultValue']" placeholder="默认值(可置空)" />
    </a-form-item>

    <a-form-item label="是否必填" :label-col="labelCol" :wrapper-col="wrapperCol">
      <a-select v-decorator="['required']" placeholder="必填，1：是，0：否">
        <a-select-option :value="1">是</a-select-option>
        <a-select-option :value="0">否</a-select-option>
      </a-select>
    </a-form-item>

    <a-form-item label="备注信息" :label-col="labelCol" :wrapper-col="wrapperCol">
      <a-textarea v-decorator="['remarks']" placeholder="备注信息" />
    </a-form-item>

    <div v-show="formAction === FORM_ACTION.UPDATE">
      <a-form-item label="创建时间" :label-col="labelCol" :wrapper-col="wrapperCol">
        <span>{{ displayData.createTime }}</span>
      </a-form-item>
      <a-form-item label="修改时间" :label-col="labelCol" :wrapper-col="wrapperCol">
        <span>{{ displayData.updateTime }}</span>
      </a-form-item>
    </div>
    <a-form-item :wrapper-col="{ offset: 3 }">
      <a-button html-type="submit" type="primary" :loading="submitLoading">提交</a-button>
      <a-button style="margin-left: 8px" @click="backToPage(false)">取消</a-button>
    </a-form-item>
  </a-form>
</template>

<script>
import { FormPageMixin } from '@/mixins'
import { addObj, putObj } from '@/api/gen/templateproperty'

export default {
  name: 'TemplatePropertyFormPage',
  mixins: [FormPageMixin],
  data() {
    return {
      reqFunctions: {
        create: addObj,
        update: putObj
      },

      // 校验配置
      decoratorOptions: {}
    }
  },
  methods: {
    createdFormCallback(argument) {
      if (this.formAction === FORM_ACTION.CREATE) {
        this.fillFormData({ groupId: argument })
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
