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
        <template v-if="column.dataIndex === 'icon'">
          <a-avatar shape="square" :src="record.icon" size="large" />
        </template>
        <template v-else-if="column.dataIndex === 'action'">
          <a @click="handleEdit(record)">编辑</a>
          <a-divider type="vertical" />
          <a @click="handleCopy(record)">复制</a>
          <a-divider type="vertical" />
          <a-popconfirm title="确认要删除吗？" @confirm="handleRemove(record)">
            <a class="ballcat-text-danger">删除</a>
          </a-popconfirm>
        </template>
        <template v-else-if="column.dataIndex === 'templateFileAction'">
          <a @click="handleEntry(record)">模板编辑</a>
          <a-divider type="vertical" />
          <div style="position: relative; display: inline-block">
            <a-upload
              accept=".zip,.7z,.rar"
              :show-upload-list="false"
              :custom-request="(fileInfo: UploadRequestOption) => handleEntryImport(fileInfo, record)"
            >
              <a>模板导入</a>
            </a-upload>
          </div>
          <a-divider type="vertical" />
          <a @click="handleEntryExport(record)">模板导出</a>
        </template>
        <template v-else-if="column.dataIndex === 'templatePropertyAction'">
          <a @click="handleProperty(record)">属性配置</a>
          <a-divider type="vertical" />
          <div style="position: relative; display: inline-block">
            <a-upload
              accept=".json"
              :show-upload-list="false"
              :custom-request="(fileInfo: UploadRequestOption) => handlePropertyImport(fileInfo, record)"
            >
              <a>属性导入</a>
            </a-upload>
          </div>
          <a-divider type="vertical" />
          <a @click="handlePropertyExport(record)">属性导出</a>
        </template>
      </template>
    </a-table>
  </a-card>

  <!-- 模板组编辑页面 -->
  <template-entry-edit-page
    v-show="!tableShow"
    ref="templateEntryEditPageRef"
    @handle-property="handleProperty(editedTemplateGroup!)"
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
import { exportTemplateGroupEntries, importTemplateGroupEntries } from '@/api/gen/template-entry'
import {
  exportTemplateGroupProperties,
  importTemplateGroupProperties
} from '@/api/gen/template-property'
import AddButton from '@/components/button/AddButton.vue'
import TemplateEntryEditPage from '@/views/gen/template-group/TemplateEntryEditPage.vue'
import TemplatePropertyModal from '@/views/gen/template-property/TemplatePropertyModal.vue'
import TemplateGroupFormModal from '@/views/gen/template-group/TemplateGroupFormModal.vue'
import { doRequest } from '@/utils/axios/request'
import type { TemplateGroup } from '@/api/gen/template-group/types'
import type { TemplateGroupFormModalInstance, TemplatePropertyModalInstance } from './types'
import type { TemplateGroupPageParam } from '@/api/gen/template-group/types'
import { remoteFileDownload } from '@/utils/file-util'
import type { UploadRequestOption } from 'ant-design-vue/es/vc-upload/interface'

const templateGroupFormModalRef = ref<TemplateGroupFormModalInstance>()
const templatePropertyModalRef = ref<TemplatePropertyModalInstance>()
const templateEntryEditPageRef = ref()

const tableShow = ref<boolean>(true)
const editedTemplateGroup = ref<TemplateGroup>()

const columns = [
  {
    title: '#',
    dataIndex: 'id'
  },
  {
    title: '唯一标识',
    dataIndex: 'groupKey'
  },
  {
    title: '图标',
    dataIndex: 'icon'
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
    dataIndex: 'action',
    width: '150px'
  },
  {
    title: '模板操作',
    dataIndex: 'templateFileAction',
    width: '230px'
  },
  {
    title: '模板属性操作',
    dataIndex: 'templatePropertyAction',
    width: '230px'
  }
]

// 查询参数
const queryParam = reactive<TemplateGroupPageParam>({})
// 数据表格
const tableState = useTable<TemplateGroup>({
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
function handleRemove(record: TemplateGroup) {
  doRequest({
    request: removeTemplateGroup(record.groupKey as string),
    successMessage: '删除成功！',
    onSuccess() {
      tableState.reloadTable(false)
    }
  })
}

function handleEntry(record: TemplateGroup) {
  tableShow.value = false
  editedTemplateGroup.value = record
  templateEntryEditPageRef.value.edit(record)
}
/** 模板组文件导入 **/
function handleEntryImport(fileInfo: UploadRequestOption, record: TemplateGroup) {
  doRequest({
    request: importTemplateGroupEntries(record.groupKey as string, fileInfo.file as File),
    successMessage: '导入模板组文件成功！',
    onSuccess() {
      tableState.reloadTable(false)
    }
  })
}
/** 模板组文件导出 **/
function handleEntryExport(record: TemplateGroup) {
  exportTemplateGroupEntries(record.groupKey as string).then(response => {
    remoteFileDownload(response)
  })
}

/** 模板组属性编辑新建 **/
function handleProperty(record: TemplateGroup) {
  templatePropertyModalRef.value?.open(record)
}
/** 模板组属性导入 **/
function handlePropertyImport(fileInfo: UploadRequestOption, record: TemplateGroup) {
  doRequest({
    request: importTemplateGroupProperties(record.groupKey as string, fileInfo.file as File),
    successMessage: '导入模板组属性成功！',
    onSuccess() {
      tableState.reloadTable(false)
    }
  })
}
/** 模板组属性导出 **/
function handlePropertyExport(record: TemplateGroup) {
  exportTemplateGroupProperties(record.groupKey as string).then(response => {
    remoteFileDownload(response)
  })
}
</script>

<style scoped></style>
