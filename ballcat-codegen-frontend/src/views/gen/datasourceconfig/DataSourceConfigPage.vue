<template>
  <div>
    <a-card :bordered="false">
      <!-- 查询条件 -->
      <div class="table-page-search-wrapper">
        <a-form layout="inline">
          <a-row :gutter="48">
            <a-col :md="8" :sm="24">
              <a-form-item label="ID">
                <a-input v-model="queryParam.id" placeholder="" />
              </a-form-item>
            </a-col>

            <!-- <template v-if="advanced">
            </template>-->
            <a-col :md="(!advanced && 8) || 24" :sm="24">
              <span
                class="table-page-search-submitButtons"
                :style="(advanced && { float: 'right', overflow: 'hidden' }) || {}"
              >
                <a-button type="primary" @click="reloadTable">查询</a-button>
                <a-button style="margin-left: 8px" @click="resetSearchForm">重置</a-button>
                <!--<a @click="toggleAdvanced" style="margin-left: 8px">
                  {{ advanced ? '收起' : '展开' }}
                  <a-icon :type="advanced ? 'up' : 'down'"/>
                </a>-->
              </span>
            </a-col>
          </a-row>
        </a-form>
      </div>

      <!-- 操作按钮区域 -->
      <div class="table-operator">
        <a-button type="primary" icon="plus" @click="handleAdd()">新建</a-button>
      </div>

      <!--数据表格区域-->
      <div class="table-wrapper">
        <a-table
          ref="table"
          size="middle"
          :row-key="rowKey"
          :columns="columns"
          :data-source="dataSource"
          :pagination="pagination"
          :loading="loading"
          @change="handleTableChange"
        >
          <template #action-slot="text, record">
            <a @click="handleEdit(record)">编辑</a>
            <a-divider type="vertical" />
            <a-popconfirm title="确认要删除吗？" @confirm="() => handleDel(record)">
              <a href="javascript:;">删除</a>
            </a-popconfirm>
          </template>
        </a-table>
      </div>
    </a-card>

    <!--表单页面-->
    <data-source-config-modal-form ref="formModal" @reload-page-table="reloadTable" />
  </div>
</template>

<script>
import { getPage, delObj } from '@/api/gen/datasourceconfig'
import DataSourceConfigModalForm from './DataSourceConfigModalForm'
import { TablePageMixin } from '@/mixins'

export default {
  name: 'DataSourceConfigPage',
  components: { DataSourceConfigModalForm },
  mixins: [TablePageMixin],
  data() {
    return {
      getPage: getPage,
      delObj: delObj,

      columns: [
        {
          title: '#',
          dataIndex: 'id',
          width: '50px'
        },
        {
          title: '数据源名称',
          dataIndex: 'name',
          ellipsis: true,
          width: '100px'
        },
        {
          title: '用户名',
          dataIndex: 'username',
          width: '80px'
        },
        {
          title: '密码',
          dataIndex: 'password',
          ellipsis: true,
          width: '200px'
        },
        {
          title: '连接地址',
          dataIndex: 'url',
          ellipsis: true
        },
        {
          title: '创建时间',
          dataIndex: 'createTime',
          width: '150px',
          sorter: true
        },
        {
          title: '操作',
          dataIndex: 'action',
          width: 120,
          scopedSlots: { customRender: 'action-slot' }
        }
      ]
    }
  },
  methods: {
    // 新建数据源
    handleAdd () {
      this.$refs.formModal.add({title: '新建数据源'})
    },
    // 编辑数据源
    handleEdit (record) {
      this.$refs.formModal.update(record, {title: '编辑数据源'})
    }
  }
}
</script>
