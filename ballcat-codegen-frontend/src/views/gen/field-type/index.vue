<template>
  <a-card :bordered="false" style="min-height: calc(100vh - 108px)">
    <!-- 操作按钮区域 -->
    <div class="table-list-toolbar">
      <a-select
        v-model:value="queryParam.dbType"
        style="width: 250px"
        :placeholder="'数据库类型'"
        @change="tableState.reloadTable(true)"
      >
        <a-select-option v-for="(value, key) in DbType" :key="key" :value="value">{{
          key
        }}</a-select-option>
      </a-select>
      <a-input-search
        v-model:value="queryParam.groupKey"
        placeholder="模板组标识"
        style="width: 250px"
        @change="tableState.reloadTable(true)"
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
  <field-type-edit-modal ref="editModal" @done="tableState.reloadTable(false)" />
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import useTable from '@/hooks/table'
import { doRequest } from '@/utils/axios/request'
import { queryFieldTypePage, removeFieldType } from '@/api/gen/field-type'
import FieldTypeEditModal from '@/views/gen/field-type/FieldTypeEditModal.vue'
import AddButton from '@/components/button/AddButton.vue'

import type { FieldType, FieldTypePageParam } from '@/api/gen/field-type/types'
import type { FieldTypeEditModalInstance } from './types'
import { DbType } from '@/api/gen/field-type/types'

// 编辑弹窗
const editModal = ref<FieldTypeEditModalInstance>()

// 表格列设置
const columns = [
  {
    title: '#',
    dataIndex: 'id'
  },
  {
    title: '模板组标识',
    dataIndex: 'groupKey'
  },
  {
    title: 'DB属性类型',
    dataIndex: 'columnKey'
  },
  {
    title: '对应属性类型',
    dataIndex: 'columnValue'
  },
  {
    title: '数据库类型',
    dataIndex: 'dbType',
    customRender: ({ text }) =>
      Object.entries(DbType).find(([, enumValue]) => enumValue === text)?.[0]
  },
  {
    title: '属性包路径',
    dataIndex: 'packageName'
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
const queryParam = reactive<FieldTypePageParam>({})
// 数据表格
const tableState = useTable<FieldType>({
  pageRequest: queryFieldTypePage,
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
const handleEdit = (record: FieldType) => {
  editModal.value?.update(record)
}

/* 删除数据源配置 */
const handleDel = (record: FieldType) => {
  doRequest<void>({
    request: removeFieldType(record.id),
    successMessage: '删除成功！',
    onSuccess() {
      tableState.loadData()
    }
  })
}
</script>
