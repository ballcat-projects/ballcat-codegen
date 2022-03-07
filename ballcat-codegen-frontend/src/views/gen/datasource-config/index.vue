<template>
  <a-card :bordered="false" style="min-height: calc(100vh - 108px)">
    <!-- 操作按钮区域 -->
    <div class="table-list-toolbar">
      <a-input-search
        v-model:value="queryParam.name"
        placeholder="数据源名称"
        style="width: 250px"
        @search="tableState.reloadTable(true)"
      />
      <add-button @click="handleAdd" />
    </div>

    <!--数据表格区域-->
    <a-table
      ref="table"
      size="middle"
      row-key="id"
      :columns="columns"
      :data-source="dataSource"
      :pagination="pagination"
      :loading="loading"
      :scroll="{ x: 1000 }"
      @change="tableState.handleTableChange"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'action'">
          <a @click="handleEdit(record)">编辑</a>
          <a-divider type="vertical" />
          <a-popconfirm title="确认要删除吗？" @confirm="() => handleDel(record)">
            <a href="javascript:">删除</a>
          </a-popconfirm>
        </template>
      </template>
    </a-table>
  </a-card>

  <!-- 编辑弹窗 -->
  <data-source-config-edit-modal ref="editModal" @done="tableState.reloadTable(false)" />
</template>

<script setup lang="ts">
  import { reactive, ref } from 'vue'
  import useTable from '@/hooks/table'
  import { doRequest } from '@/utils/axios/request'
  import { queryDatasourceConfigPage, removeDatasourceConfig } from '@/api/gen/datasource-config'
  import { DataSourceConfig, DataSourcePageParam } from '@/api/gen/datasource-config/types'
  import DataSourceConfigEditModal from '@/views/gen/datasource-config/DataSourceConfigEditModal.vue'
  import { DataSourceConfigEditModalInstance } from './types'
  import AddButton from '@/components/button/AddButton.vue'

  // 编辑弹窗
  const editModal = ref<DataSourceConfigEditModalInstance>()

  // 表格列设置
  const columns = [
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

  // 查询参数
  const queryParam = reactive<DataSourcePageParam>({})
  // 数据表格
  let tableState = useTable<DataSourceConfig>({
    pageRequest: queryDatasourceConfigPage,
    queryParam: queryParam
  })
  const { dataSource, pagination, loading } = tableState
  // 立刻加载数据
  tableState.loadData()

  /* 新建数据源配置 */
  const handleAdd = () => {
    editModal.value?.add()
  }

  /* 编辑数据源配置 */
  const handleEdit = (record: DataSourceConfig) => {
    editModal.value?.update(record)
  }

  /* 删除数据源配置 */
  const handleDel = (record: DataSourceConfig) => {
    doRequest<void>({
      request: removeDatasourceConfig(record.id),
      successMessage: '删除成功！'
    })
  }
</script>
