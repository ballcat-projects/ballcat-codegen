<template>
  <a-row type="flex" style="min-height: calc(100vh - 600px); align-items: stretch">
    <a-col :flex="5">
      <div class="database-title">数据源</div>
      <div style="overflow: auto">
        <a-menu v-model:selectedKeys="selectedDsNames" mode="inline" :style="menuStyle">
          <a-menu-item key="master" value="master">master</a-menu-item>
          <a-menu-item v-for="item in dataSourceSelectData" :key="item.value">
            {{ item.name || item.value }}
          </a-menu-item>
        </a-menu>
      </div>
    </a-col>
    <a-col :flex="20">
      <div ref="tableColRef" style="padding: 0 24px">
        <!--        <a-form>-->
        <!--          <a-row :gutter="12">-->
        <!--            <a-col :xl="6" :md="12" :sm="24">-->
        <!--              <a-form-item label="">-->
        <!--                <a-input-search-->
        <!--                  v-model:value="queryParam.tableName"-->
        <!--                  placeholder="表名"-->
        <!--                  @search="tableState.reloadTable(true)"-->
        <!--                />-->
        <!--              </a-form-item>-->
        <!--            </a-col>-->
        <!--          </a-row>-->
        <!--        </a-form>-->
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
          <template #headerCell="{ column }">
            <template v-if="column.key === 'tableName'">
              <span style="color: #1890ff">表名</span>
            </template>
          </template>
          <template
            #customFilterDropdown="{ setSelectedKeys, selectedKeys, confirm, clearFilters, column }"
          >
            <div style="padding: 8px">
              <a-input
                ref="searchInput"
                :placeholder="`Search ${column.dataIndex}`"
                :value="selectedKeys[0]"
                style="width: 188px; margin-bottom: 8px; display: block"
                @change="e => setSelectedKeys(e.target.value ? [e.target.value] : [])"
                @pressEnter="handleSearch(selectedKeys, confirm, column.dataIndex)"
              />
              <a-button
                type="primary"
                size="small"
                style="width: 90px; margin-right: 8px"
                @click="handleSearch(selectedKeys, confirm, column.dataIndex)"
              >
                <template #icon><SearchOutlined /></template>
                Search
              </a-button>
              <a-button size="small" style="width: 90px" @click="handleReset(clearFilters)">
                Reset
              </a-button>
            </div>
          </template>
          <template #customFilterIcon="{ filtered }">
            <search-outlined :style="{ color: filtered ? '#108ee9' : undefined }" />
          </template>
        </a-table>
      </div>
    </a-col>
  </a-row>
</template>

<script setup lang="ts">
  import { onMounted, reactive, ref, watch } from 'vue'
  import type { CSSProperties } from 'vue'
  import { queryTableInfoPage } from '@/api/gen/generate'
  import { listDatasourceConfigSelectData } from '@/api/gen/datasource-config'
  import type { ColumnProps } from 'ant-design-vue/es/table'
  import type { TableInfo } from '@/api/gen/generate/types'
  import type { TableInfoPageParam } from '@/api/gen/generate/types'
  import useTable from '@/hooks/table'
  import { doRequest } from '@/utils/axios/request'
  import type { SelectData } from '@/api/types'
  import { SearchOutlined } from '@ant-design/icons-vue'
  import type { GenerateStepInstance } from './types'
  import { useGeneratorOptionStore } from '@/store'

  const generatorOptionStore = useGeneratorOptionStore()

  // 处理数据源菜单的高度问题，保持和表格同高
  const menuStyle: CSSProperties = reactive({
    paddingRight: '1px',
    height: '1px',
    borderRadius: '10px'
  })
  const tableColRef = ref()
  onMounted(() => {
    // 利用 ResizeObserver，监听 dom size 修改
    const resizeObserver = new ResizeObserver(mutations => {
      let tableColHeight = Math.max(548, mutations[0].contentRect.height)
      menuStyle.height = tableColHeight + 'px'
    })
    resizeObserver.observe(tableColRef.value)
  })

  // 表格列配置
  const columns = ref<ColumnProps[]>([
    {
      title: '表名',
      dataIndex: 'tableName',
      customFilterDropdown: true
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
    }
  ])

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

  let tableState = useTable<TableInfo>({
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

  // @ts-ignore
  const handleSearch = (selectedKeys, confirm, dataIndex) => {
    confirm()
  }

  // @ts-ignore
  const handleReset = clearFilters => {
    clearFilters({ confirm: true })
  }

  defineExpose<GenerateStepInstance>({
    validate: () => {
      if (tableState.selectedRowKeys.value && tableState.selectedRowKeys.value.length > 0) {
        return Promise.resolve()
      } else {
        return Promise.reject({ message: '请至少选择一张数据表' })
      }
    },
    next: () => {
      generatorOptionStore.dsName = dsName.value
      generatorOptionStore.tableNames = tableState.selectedRowKeys.value as string[]
    }
  })
</script>

<style scoped lang="less">
  .database-title {
    color: rgba(0, 0, 0, 0.85);
    font-size: 16px;
    font-weight: 400;
    line-height: 31px;
    padding: 8px 0 8px 12px;
    border-bottom: 1px solid #f0f0f0;
    border-right: 1px solid #f0f0f0;
    width: 100%;
  }
</style>
