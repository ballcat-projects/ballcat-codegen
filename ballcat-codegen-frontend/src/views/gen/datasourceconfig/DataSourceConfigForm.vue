<template>
  <a-form :form="form" @submit="handleSubmit">
    <a-form-item v-if="formAction === FORM_ACTION.UPDATE" style="display: none">
      <a-input v-decorator="['id']" />
    </a-form-item>

    <a-form-item label="数据源名称" :label-col="labelCol" :wrapper-col="wrapperCol">
      <a-input v-decorator="['name']" placeholder="数据源名称" />
    </a-form-item>

    <a-form-item label="用户名" :label-col="labelCol" :wrapper-col="wrapperCol">
      <a-input v-decorator="['username']" placeholder="用户名" />
    </a-form-item>

    <a-form-item
      v-if="formAction === FORM_ACTION.UPDATE"
      label="原密码"
      :label-col="labelCol"
      :wrapper-col="wrapperCol"
    >
      <a-input v-model="password" disabled />
    </a-form-item>

    <a-form-item label="密码" :label-col="labelCol" :wrapper-col="wrapperCol">
      <template v-if="formAction === FORM_ACTION.UPDATE" #extra>
        <p style="color: red">注意：如果需要修改密码则填写此处，不修改请置空</p>
      </template>
      <a-input v-decorator="['pass']" placeholder="密码" />
    </a-form-item>

    <a-form-item label="连接地址" :label-col="labelCol" :wrapper-col="wrapperCol">
      <a-textarea v-decorator="['url']" placeholder="连接地址" :rows="4" />
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
import { addObj, putObj } from '@/api/gen/datasourceconfig'

export default {
  name: 'DataSourceConfigFormPage',
  mixins: [FormPageMixin],
  data() {
    return {
      reqFunctions: {
        create: addObj,
        update: putObj
      },

      // 校验配置
      decoratorOptions: {},

      password: ''
    }
  },
  methods: {
    echoDataProcess(data) {
      this.password = data.password
      return data
    }
  }
}
</script>
