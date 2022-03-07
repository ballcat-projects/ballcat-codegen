<template>
  <a-card :bordered="false" :body-style="{ padding: 0 }">
    <a-row type="flex" style="min-height: calc(100vh - 150px)">
      <a-col :flex="5">
        <div class="database-title">数据源</div>
        <a-menu
          v-model:selectedKeys="selectedDsNames"
          mode="inline"
          style="height: calc(100% - 56px)"
        >
          <a-menu-item key="master" value="master">master</a-menu-item>
          <a-menu-item v-for="item in dataSourceSelectData" :key="item.value">
            {{ item.name }}
          </a-menu-item>
        </a-menu>
      </a-col>
      <a-col :flex="20">
        <div style="padding: 24px">
          <a-form>
            <a-row :gutter="12">
              <a-col :xl="6" :md="12" :sm="24">
                <a-form-item label="">
                  <a-input-search
                    v-model:value="queryParam.tableName"
                    placeholder="表名"
                    @search="tableState.reloadTable(true)"
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
              selectedRowKeys: tableState.selectedRowKeys,
              onChange: tableState.onSelectChange
            }"
            table-layout="fixed"
            :scroll="{ x: 620 }"
            @change="tableState.handleTableChange"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.dataIndex === 'action'">
                <a @click="singleGenerate(record)">生成</a>
              </template>
            </template>
          </a-table>
        </div>
      </a-col>
    </a-row>
  </a-card>
  <!-- 代码生成弹窗 -->
  <generate-modal ref="generateModel" />
</template>

<script setup lang="ts">
  import { reactive, ref, watch } from 'vue'
  import { queryTableInfoPage } from '@/api/gen/generate'
  import { listDatasourceConfigSelectData } from '@/api/gen/datasource-config'
  import type { ColumnProps } from 'ant-design-vue/lib/table'
  import { DownloadOutlined } from '@ant-design/icons-vue'
  import type { TableInfo } from '@/api/gen/generate/types'
  import { TableInfoPageParam } from '@/api/gen/generate/types'
  import useTable from '@/hooks/table'
  import { doRequest } from '@/utils/axios/request'
  import { SelectData } from '@/api/types'
  import { message } from 'ant-design-vue'
  import GenerateModal from '@/views/gen/codegen/GenerateModal.vue'
  import { GenerateModalInstance } from './types'

  // 表格列配置
  const columns = ref<ColumnProps[]>([
    {
      title: '表名',
      dataIndex: 'tableName'
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
      width: 150,
      sorter: true
    },
    {
      title: '操作',
      dataIndex: 'action',
      width: 80
    }
  ])

  const generateModel = ref<GenerateModalInstance>()

  const selectedDsNames = ref<string[]>(['master'])

  const dsName = ref<string>('master')

  watch(
    () => selectedDsNames,
    () => {
      dsName.value = selectedDsNames.value[0]
      tableState.loadData()
    },
    { deep: true }
  )

  const queryParam = reactive<TableInfoPageParam>({})

  let tableState = useTable<TableInfo>({
    queryParam: queryParam,
    pageRequest: (pageParams: TableInfoPageParam) => {
      return queryTableInfoPage(dsName.value, pageParams)
    }
  })
  const { dataSource, pagination, loading } = tableState

  tableState.loadData()

  const dataSourceSelectData = ref<SelectData[]>([])
  doRequest({
    request: listDatasourceConfigSelectData(),
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
    const tableNames = tableState.selectedRowKeys.value as string[]
    if (tableNames && tableNames.length > 0) {
      generateModel.value?.open(dsName.value, tableNames)
    } else {
      message.warning('至少选中一张表')
    }
  }
</script>

<style scoped lang="less">
  .database-title {
    color: rgba(0, 0, 0, 0.85);
    font-size: 16px;
    font-weight: 400;
    line-height: 32px;
    padding: 8px 0 8px 12px;
    border-bottom: 1px solid #f0f0f0;
    border-right: 1px solid #f0f0f0;
    width: 100%;
  }
</style>
