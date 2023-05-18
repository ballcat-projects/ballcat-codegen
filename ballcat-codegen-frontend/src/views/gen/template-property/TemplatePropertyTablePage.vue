<template>
  <div>
    <!-- 操作按钮区域 -->
    <div class="table-list-toolbar" style="margin-bottom: 10px">
      <add-button @click="handleCreate()" />
    </div>

    <!--数据表格区域-->
    <a-table
      ref="table"
      size="small"
      row-key="id"
      :columns="columns"
      :data-source="dataSource"
      :pagination="pagination"
      :loading="loading"
      :scroll="{ x: 700 }"
      @change="tableState.handleTableChange"
    >
      <template #bodyCell="{ column, text, record }">
        <template v-if="column.dataIndex === 'required'">
          <a-tag v-if="text === 1" color="red">是</a-tag>
          <a-tag v-else>否</a-tag>
        </template>
        <template v-else-if="column.dataIndex === 'action'">
          <a @click="handleUpdate(record)">编辑</a>
          <a-divider type="vertical" />
          <a-popconfirm title="确定要删除吗?" @confirm="handleRemove(record.id)">
            <a class="ballcat-text-danger">删除</a>
          </a-popconfirm>
        </template>
      </template>
    </a-table>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import useTable from '@/hooks/table'
import { queryTemplatePropertyPage, removeTemplateProperty } from '@/api/gen/template-property'
import { doRequest } from '@/utils/axios/request'
import type { TemplateGroup } from '@/api/gen/template-group/types'
import type { TemplateProperty } from '@/api/gen/template-property/types'
import type { PageParam } from '@/api/types'

const emits = defineEmits<{
  // 提交完成事件
  (e: 'create'): void
  (e: 'update', record: TemplateProperty): void
}>()

const columns = [
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
    title: '组件类型',
    dataIndex: 'componentType',
    width: 80
  },
  {
    title: '必填',
    dataIndex: 'required',
    width: 60
  },
  {
    title: '排序',
    dataIndex: 'orderValue',
    width: 60
  },
  {
    title: '备注信息',
    dataIndex: 'remarks',
    width: 180
  },
  {
    title: '操作',
    dataIndex: 'action',
    width: 100
  }
]

// 所属模板组标识
const templateGroupKey = ref<string>()
// 数据表格
const tableState = useTable<TemplateProperty>({
  pageRequest: (query: PageParam) => {
    const params = Object.assign({ groupKey: templateGroupKey.value, sort: 'orderValue' }, query)
    return queryTemplatePropertyPage(params)
  }
})
const { dataSource, pagination, loading } = tableState

function handleCreate() {
  emits('create')
}

function handleUpdate(record: TemplateProperty) {
  emits('update', record)
}

function handleRemove(id: number) {
  doRequest({
    request: removeTemplateProperty(id),
    successMessage: '删除成功',
    onSuccess() {
      tableState.reloadTable(false)
    }
  })
}

defineExpose({
  load(templateGroup: TemplateGroup) {
    templateGroupKey.value = templateGroup.groupKey
    tableState.loadData()
  },
  reloadTable() {
    tableState.loadData()
  }
})
</script>

<script lang="ts">
export default {
  name: 'TemplatePropertyEditPage'
}
</script>

<style scoped></style>
