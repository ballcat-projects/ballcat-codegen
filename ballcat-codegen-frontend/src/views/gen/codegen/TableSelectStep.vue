<template>
  <div>
    <a-result v-show="!generatorConfigStore.isUseTable" title="当前模板组不依赖数据源即可生成代码">
      <template #icon>
        <SmileTwoTone />
      </template>
    </a-result>

    <a-row
      v-show="generatorConfigStore.isUseTable"
      type="flex"
      style="min-height: calc(100vh - 600px); align-items: stretch"
    >
      <a-col :flex="5">
        <div class="database-title">
          <a-row type="flex">
            <a-col :flex="9">数据源</a-col>
            <a-col :flex="1">
              <ReloadOutlined :spin="datasourceLoading" @click="loadDataSources" />
            </a-col>
          </a-row>
        </div>
        <div style="overflow: auto">
          <template v-if="dataSourceSelectData && dataSourceSelectData.length > 0">
            <a-menu v-model:selectedKeys="selectedDsNames" mode="inline" :style="menuStyle">
              <a-menu-item v-for="item in dataSourceSelectData" :key="item.value">
                {{ item.name || item.value }}
              </a-menu-item>
            </a-menu>
          </template>
          <template v-else>
            <a-empty :image="false" :description="false" style="padding-top: 50px">
              <a-button @click="openDatasourcePage"> 新建数据源 </a-button>
            </a-empty>
          </template>
        </div>
      </a-col>
      <a-col :flex="20">
        <div ref="tableColRef" style="padding: 0 24px">
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
              #customFilterDropdown="{
                setSelectedKeys,
                selectedKeys,
                confirm,
                clearFilters,
                column
              }"
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
  </div>
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
import { SearchOutlined, SmileTwoTone, ReloadOutlined } from '@ant-design/icons-vue'
import type { GenerateStepInstance } from './types'
import { useGeneratorConfigStore } from '@/store'
import { useRouter } from 'vue-router'

const generatorConfigStore = useGeneratorConfigStore()

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
    const tableColHeight = Math.max(548, mutations[0].contentRect.height)
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

const selectedDsNames = ref<string[]>([''])

const dsName = ref<string>('')

watch(
  () => selectedDsNames,
  () => {
    dsName.value = selectedDsNames.value[0]
    tableState.loadData()
  },
  { deep: true }
)

const tableState = useTable<TableInfo>({
  pageRequest: (pageParams: TableInfoPageParam) => {
    return queryTableInfoPage(dsName.value, pageParams)
  }
})
const { dataSource, pagination, loading } = tableState

const dataSourceSelectData = ref<SelectData[]>([])

// @ts-ignore
const handleSearch = (selectedKeys, confirm, dataIndex) => {
  confirm()
}

// @ts-ignore
const handleReset = clearFilters => {
  clearFilters({ confirm: true })
}

const router = useRouter()

/* 打开数据源管理页 */
function openDatasourcePage() {
  const { href } = router.resolve('/datasource')
  window.open(href, '_blank')
}

// 数据源加载中
const datasourceLoading = ref(false)

/* 加载数据源信息 */
function loadDataSources() {
  datasourceLoading.value = true
  doRequest({
    request: listDatasourceConfigSelectData(),
    onSuccess: res => {
      dataSourceSelectData.value = res.data as SelectData[]
      if (dataSourceSelectData.value && dataSourceSelectData.value.length > 0) {
        selectedDsNames.value = [dataSourceSelectData.value[0].value]
      }
    },
    onFinally: () => (datasourceLoading.value = false)
  })
}

defineExpose<GenerateStepInstance>({
  enter: loadDataSources,
  validate: () => {
    if (!generatorConfigStore.isUseTable) {
      return Promise.resolve()
    }
    if (tableState.selectedRowKeys.value && tableState.selectedRowKeys.value.length > 0) {
      return Promise.resolve()
    }
    return Promise.reject({ message: '请至少选择一张数据表' })
  },
  next: () => {
    generatorConfigStore.dsName = dsName.value
    generatorConfigStore.options.tableNames = tableState.selectedRowKeys.value as string[]
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
