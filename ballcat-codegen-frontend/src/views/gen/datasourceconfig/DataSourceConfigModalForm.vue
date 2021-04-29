<template>
  <a-modal
    :title="title"
    :visible="visible"
    :mask-closable="false"
    :body-style="{paddingBottom: '8px'}"
    :confirm-loading="submitLoading"
    :width="500"
    @ok="handleSubmit"
    @cancel="handleClose"
  >
    <a-form
      :form="form"
      :label-col="labelCol"
      :wrapper-col="wrapperCol"
      @submit="handleSubmit"
    >
      <a-form-item v-if="isUpdateForm" style="display: none">
        <a-input v-decorator="['id']" />
      </a-form-item>

      <a-form-item label="名称">
        <a-input v-decorator="['name', decoratorOptions.name]" placeholder="数据源名称" />
      </a-form-item>

      <a-form-item label="用户名" :label-col="labelCol" :wrapper-col="wrapperCol">
        <a-input v-decorator="['username', decoratorOptions.username]" placeholder="用户名" />
      </a-form-item>

      <a-form-item
        v-if="isUpdateForm"
        label="原密码"
      >
        <a-input v-model="password" disabled />
      </a-form-item>

      <a-form-item label="密码">
        <template v-if="isUpdateForm" #extra>
          <p style="color: red">注意：如果需要修改密码则填写此处，不修改请置空</p>
        </template>
        <a-input v-decorator="['pass', isCreateForm? decoratorOptions.pass: {}]" placeholder="密码" />
      </a-form-item>

      <a-form-item label="连接地址">
        <a-textarea v-decorator="['url', decoratorOptions.url]" placeholder="连接地址" :rows="4" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script>
import { PopUpFormMixin } from '@/mixins'
import { addObj, putObj } from '@/api/gen/datasourceconfig'

export default {
  name: 'DataSourceConfigFormPage',
  mixins: [PopUpFormMixin],
  data() {
    return {
      reqFunctions: {
        create: addObj,
        update: putObj
      },

      labelCol: {
        sm: { span: 24 },
        md: { span: 5 }
      },
      wrapperCol: {
        sm: { span: 24 },
        md: { span: 18 }
      },

      // 校验配置
      decoratorOptions: {
        name: {
          rules: [{ required: true, message: '请输入数据源名称!' }]
        },
        username: {
          rules: [{ required: true, message: '请输入用户名!' }]
        },
        pass: {
          rules: [{ required: true, message: '请输入用户名!' }]
        },
        url: {
          rules: [{ required: true, message: '请输入连接地址!' }]
        },
      },

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
