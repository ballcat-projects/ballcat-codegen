<template>
  <a-modal
    :title="'属性配置-' + groupName"
    :visible="visible"
    :confirm-loading="confirmLoading"
    :mask-closable="false"
    :footer="null"
    :body-style="{ padding: '12px 18px' }"
    :width="800"
    :centered="true"
    @cancel="handleClose"
  >
    <div v-show="tableShow" :bordered="false">
      <!-- 操作按钮区域 -->
      <div style="padding-bottom: 10px">
        <a-button type="primary" icon="plus" @click="handleAdd(groupId)">新建</a-button>
      </div>

      <!--数据表格区域-->
      <div class="table-wrapper">
        <a-table
          ref="table"
          size="small"
          :row-key="rowKey"
          :columns="columns"
          :data-source="dataSource"
          :pagination="pagination"
          :loading="loading"
          @change="handleTableChange"
        >
          <template slot="action-slot" slot-scope="text, record">
            <a @click="handleEdit(record)">编辑</a>
            <a-divider type="vertical" />
            <a-popconfirm title="确认要删除吗？" @confirm="() => handleDel(record)">
              <a href="javascript:;">删除</a>
            </a-popconfirm>
          </template>
        </a-table>
      </div>
    </div>

    <!--表单页面-->
    <page-form v-show="!tableShow" ref="pageForm" @back-to-page="backToPage" />
  </a-modal>
</template>

<script>
import { getPage, delObj } from '@/api/gen/templateproperty'
import PageForm from './TemplatePropertyForm'
import { TablePageMixin } from '@/mixins'

export default {
  name: 'TemplatePropertyPage',
  components: { PageForm },
  mixins: [TablePageMixin],
  data() {
    return {
      getPage: getPage,
      delObj: delObj,

      columns: [
        {
          title: '标题',
          dataIndex: 'title'
        },
        {
          title: '属性键',
          dataIndex: 'propKey'
        },
        {
          title: '默认值',
          dataIndex: 'defaultValue'
        },
        {
          title: '必填',
          dataIndex: 'required',
          customRender: text => {
            return text === 1 ? '是' : '否'
          }
        },
        {
          title: '备注信息',
          dataIndex: 'remarks'
        },
        {
          title: '操作',
          width: 100,
          scopedSlots: { customRender: 'action-slot' }
        }
      ],

      confirmLoading: false,
      visible: false,

      groupName: '',
      groupId: '',
      queryParam: {},
      lazyLoad: true
    }
  },
  methods: {
    show(record) {
      this.visible = true
      this.groupName = record.name
      this.groupId = record.id
      this.queryParam = {
        groupId: this.groupId
      }
      this.loadData()
    },
    // 新建模板属性
    handleAdd () {
      this.switchPage()
      this.$refs.pageForm.add({title: '新建模板属性', groupId: this.groupId})
    },
    // 编辑模板属性
    handleEdit (record) {
      this.switchPage()
      this.$refs.pageForm.update(record, {title: '编辑模板属性', groupId: this.groupId})
    },
    handleClose() {
      this.visible = false
      this.confirmLoading = false
    }
  }
}
</script>
