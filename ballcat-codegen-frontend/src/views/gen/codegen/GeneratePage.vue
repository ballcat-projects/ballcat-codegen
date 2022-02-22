<template>
  <a-card>
    <a-form>
      <a-row :gutter="12">
        <a-col :xl="6" :md="12" :sm="24">
          <a-form-item label="数据源">
            <a-select
              v-model:value="dsName"
              style="width: 100%"
              @change="tableHooks.reloadTable(true)"
            >
              <a-select-option key="master" value="master">master</a-select-option>
              <a-select-option v-for="item in dataSourceSelectData" :key="item.value">
                {{ item.name }}
              </a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :xl="6" :md="12" :sm="24">
          <a-form-item label="">
            <a-input-search
              v-model:value="queryParam.tableName"
              placeholder="表名"
              @search="tableHooks.reloadTable(true)"
            />
          </a-form-item>
        </a-col>
        <a-col :xl="6" :md="12" :sm="24">
          <a-form-item label="">
            <a-button type="primary" @click="multiGenerate">
              <template #icon>
                <DownloadOutlined />
              </template>
              批量生成
            </a-button>
          </a-form-item>
        </a-col>
      </a-row>
    </a-form>
    <a-table
      row-key="tableName"
      size="middle"
      :columns="columns"
      :data-source="dataSource"
      :pagination="pagination"
      :loading="loading"
      :row-selection="{
        selectedRowKeys: tableHooks.selectedRowKeys,
        onChange: tableHooks.onSelectChange
      }"
      :scroll="{ x: 1000 }"
      @change="tableHooks.handleTableChange"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'action'">
          <a @click="singleGenerate(record)">生成</a>
        </template>
      </template>
    </a-table>
  </a-card>

  <!-- 代码生成弹窗 -->
  <generate-modal ref="generateModel" />
</template>

<script setup lang="ts">
  import { reactive, ref } from 'vue'
  import { queryTableInfoPage } from '@/api/gen/generate'
  import { listDatasourceConfigSelectData } from '@/api/gen/datasourceconfig'
  import type { ColumnProps } from 'ant-design-vue/lib/table'
  import { DownloadOutlined } from '@ant-design/icons-vue'
  import type { TableInfo } from '@/api/gen/model/generate'
  import { TableInfoPageParam } from '@/api/gen/model/generate'
  import useTable from '@/hooks/tableHooks'
  import { doRequest } from '@/utils/axios/request'
  import { SelectData } from '@/api/types'
  import { message } from 'ant-design-vue'
  import GenerateModal from '@/views/gen/codegen/GenerateModal.vue'
  import { GenerateModalInstance } from './types'

  // 表格列配置
  const columns = ref<ColumnProps[]>([
    {
      title: '表名',
      dataIndex: 'tableName',
      width: '250px'
    },
    {
      title: 'Engine',
      dataIndex: 'engine'
    },
    {
      title: '表备注',
      dataIndex: 'tableComment'
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
      width: '150px'
    }
  ])

  const generateModel = ref<GenerateModalInstance>()

  const dsName = ref<string>('master')

  const queryParam = reactive({})

  let tableHooks = useTable<TableInfo>({
    queryParam: queryParam,
    pageRequest: (pageParams: TableInfoPageParam) => {
      return queryTableInfoPage(dsName.value, pageParams)
    }
  })
  const { dataSource, pagination, loading } = tableHooks

  tableHooks.loadData()

  const dataSourceSelectData = ref<SelectData[]>([])
  doRequest(listDatasourceConfigSelectData(), {
    successMessage: false,
    onSuccess: res => {
      dataSourceSelectData.value = res.data as SelectData[]
    }
  })

  /* 单表代码生成 */
  const singleGenerate = (record: TableInfo) => {
    const tableNames = [record.tableName]
    generateModel.value?.open(dsName.value, tableNames)
  }

  /* 多表代码生成 */
  const multiGenerate = () => {
    const tableNames = tableHooks.selectedRowKeys.value as string[]
    if (tableNames && tableNames.length > 0) {
      generateModel.value?.open(dsName.value, tableNames)
    } else {
      message.warning('至少选中一张表')
    }
  }
</script>
