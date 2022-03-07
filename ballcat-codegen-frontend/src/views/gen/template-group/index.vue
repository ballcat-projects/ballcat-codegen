<template>
  <a-card v-show="tableShow" :bordered="false" style="min-height: calc(100vh - 108px)">
    <!-- 操作按钮区域 -->
    <div class="table-list-toolbar">
      <a-input-search
        v-model:value="queryParam.name"
        placeholder="模板组名称"
        style="width: 250px"
        @search="tableState.reloadTable(true)"
      />
      <add-button @click="handleAdd()" />
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
        <template v-if="column.dataIndex === 'templateAction'">
          <a @click="handleEdit(record)">编辑</a>
          <a-divider type="vertical" />
          <a @click="handleCopy(record)">复制</a>
          <a-divider type="vertical" />
          <a-popconfirm title="确认要删除吗？" @confirm="handleRemove(record.id)">
            <a class="ballcat-text-danger">删除</a>
          </a-popconfirm>
        </template>
        <template v-else-if="column.dataIndex === 'action'">
          <a @click="handleEntry(record)">模板编辑</a>
          <a-divider type="vertical" />
          <a @click="handleProperty(record)">属性配置</a>
        </template>
      </template>
    </a-table>
  </a-card>

  <!-- 模板组编辑页面 -->
  <template-entry-edit-page
    v-show="!tableShow"
    :template-group="editedTemplateGroup"
    @go-back="tableShow = true"
  />

  <!-- 模板组表单弹窗 -->
  <template-group-form-modal
    ref="templateGroupFormModalRef"
    @done="tableState.reloadTable(false)"
  />

  <!-- 模板组表单弹窗 -->
  <template-property-modal ref="templatePropertyModalRef" />
</template>

<script setup lang="ts">
  import { reactive, ref } from 'vue'
  import useTable from '@/hooks/table'
  import { queryTemplateGroupPage, removeTemplateGroup } from '@/api/gen/template-group'
  import AddButton from '@/components/button/AddButton.vue'
  import TemplateEntryEditPage from '@/views/gen/template-group/TemplateEntryEditPage.vue'
  import TemplatePropertyModal from '@/views/gen/template-group/TemplatePropertyModal.vue'
  import TemplateGroupFormModal from '@/views/gen/template-group/TemplateGroupFormModal.vue'
  import { doRequest } from '@/utils/axios/request'
  import type { TemplateGroup } from '@/api/gen/template-group/types'
  import type { TemplateGroupFormModalInstance, TemplatePropertyModalInstance } from './types'
  import { TemplateGroupPageParam } from '@/api/gen/template-group/types'

  const templateGroupFormModalRef = ref<TemplateGroupFormModalInstance>()
  const templatePropertyModalRef = ref<TemplatePropertyModalInstance>()

  const tableShow = ref<boolean>(true)
  const editedTemplateGroup = ref<TemplateGroup>({})

  const columns = [
    {
      title: '#',
      dataIndex: 'id'
    },
    {
      title: '名称',
      dataIndex: 'name'
    },
    {
      title: '备注信息',
      dataIndex: 'remarks'
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      width: '150px',
      sorter: true
    },
    {
      title: '模板组操作',
      dataIndex: 'templateAction',
      width: '150px'
    },
    {
      title: '模板操作',
      dataIndex: 'action',
      width: '150px'
    }
  ]

  // 查询参数
  const queryParam = reactive<TemplateGroupPageParam>({})
  // 数据表格
  let tableState = useTable<TemplateGroup>({
    pageRequest: queryTemplateGroupPage,
    queryParam: queryParam
  })
  const { dataSource, pagination, loading } = tableState
  // 立刻加载数据
  tableState.loadData()

  function handleAdd() {
    templateGroupFormModalRef.value?.add()
  }
  function handleEdit(record: TemplateGroup) {
    templateGroupFormModalRef.value?.update(record)
  }
  function handleCopy(record: TemplateGroup) {
    templateGroupFormModalRef.value?.copy(record)
  }
  function handleRemove(recordId: number) {
    doRequest({
      request: removeTemplateGroup(recordId),
      successMessage: '删除成功！',
      onSuccess() {
        tableState.reloadTable(false)
      }
    })
  }
  function handleEntry(record: TemplateGroup) {
    tableShow.value = false
    editedTemplateGroup.value = record
  }
  function handleProperty(record: TemplateGroup) {
    templatePropertyModalRef.value?.open(record)
  }
</script>

<style scoped></style>
