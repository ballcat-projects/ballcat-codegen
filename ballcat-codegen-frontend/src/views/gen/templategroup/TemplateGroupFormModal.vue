<template>
  <a-modal
    :title="title"
    :visible="visible"
    :confirm-loading="submitLoading"
    :body-style="{paddingBottom: '8px'}"
    :width="350"
    @ok="handleSubmit"
    @cancel="handleClose"
  >
    <a-form
      :form="form"
      layout="vertical"
      :label-col="labelCol"
      :wrapper-col="wrapperCol"
    >
      <a-form-item v-if="isUpdateForm" style="display: none">
        <a-input v-decorator="['id']" />
      </a-form-item>
      <div v-if="formAction === 'copy'" style="margin-bottom: 24px">
        <h3><span style="margin-right: 12px">源模板组:</span> {{ resourceGroupName }}</h3>
      </div>
      <a-form-item label="名称">
        <a-input
          v-decorator="['name', { rules: [{ required: true, message: '模板组名称不能为空' }] }]"
          placeholder="请输入模板组名称"
        />
      </a-form-item>
      <a-form-item label="备注信息">
        <a-textarea v-decorator="['remarks']" placeholder="请输入模板组备注信息" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script>
import { PopUpFormMixin } from '@/mixins'
import { addObj, putObj, copyObj } from '@/api/gen/templategroup'

export default {
  mixins: [PopUpFormMixin],
  data() {
    return {
      reqFunctions: {
        create: addObj,
        update: putObj,
        copy: this.copyRestFunction
      },

      labelCol: {
        sm: { span: 24 },
      },
      wrapperCol: {
        sm: { span: 24 },
      },

      resourceGroupId: '',
      resourceGroupName: ''
    }
  },
  methods: {
    copy(record, title) {
      this.form.resetFields()
      this.formAction = 'copy'
      this.resourceGroupId = record.id
      this.resourceGroupName = record.name
      this.show(title)
    },
    copyRestFunction(data) {
      return copyObj(this.resourceGroupId, data)
    }
  }
}
</script>
