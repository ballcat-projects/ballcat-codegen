<template>
  <a-modal
    :title="title"
    :visible="visible"
    :mask-closable="false"
    :footer="null"
    :body-style="{ padding: '12px 18px' }"
    :width="900"
    :centered="true"
    @cancel="handleClose"
  >
    <!-- 操作按钮区域 -->
    <div class="table-list-toolbar" style="margin-bottom: 10px">
      <add-button type="dashed" style="width: 100%; border-radius: 5px" @click="handleAdd()" />
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
          <div>
            <a-switch
              v-if="editableData[record.id]"
              v-model:checked="editableData[record.id].required"
              :checked-value="1"
              :un-checked-value="0"
              checked-children="是"
              un-checked-children="否"
            />
            <template v-else>
              <a-tag v-if="text === 1" color="red">是</a-tag>
              <a-tag v-else>否</a-tag>
            </template>
          </div>
        </template>
        <template v-else-if="column.dataIndex === 'action'">
          <template v-if="editableData[record.id]">
            <a @click="handleSave(record.id)">保存</a>
            <a-divider type="vertical" />
            <a-popconfirm title="确定要取消吗?" @confirm="handleCancel(record.id)">
              <a>取消</a>
            </a-popconfirm>
          </template>
          <template v-else>
            <a @click="handleEdit(record.id)">编辑</a>
          </template>
          <a-divider type="vertical" />
          <a-popconfirm title="确定要删除吗?" @confirm="handleRemove(record.id)">
            <a class="ballcat-text-danger">删除</a>
          </a-popconfirm>
        </template>
        <template v-else>
          <div>
            <a-input
              v-if="editableData[record.id]"
              v-model:value="editableData[record.id][column.dataIndex]"
            />
            <template v-else>
              {{ text }}
            </template>
          </div>
        </template>
      </template>
    </a-table>
  </a-modal>
</template>

<script setup lang="ts">
  import { reactive, ref, UnwrapRef } from 'vue'
  import { usePopup } from '@/hooks/popup'
  import { TemplateGroup } from '@/api/gen/template-group/types'
  import AddButton from '@/components/button/AddButton.vue'
  import cloneDeep from 'lodash-es/cloneDeep'
  import useTable from '@/hooks/table'
  import {
    addTemplateProperty,
    queryTemplatePropertyPage,
    removeTemplateProperty,
    updateTemplateProperty
  } from '@/api/gen/template-property'
  import { PageParam } from '@/api/types'
  import { doRequest } from '@/utils/axios/request'

  // import type
  import type { TemplateProperty } from '@/api/gen/template-property/types'
  import { message } from 'ant-design-vue'
  import { TemplatePropertyModalInstance } from '@/views/gen/template-group/types'

  const { visible, handleOpen, handleClose } = usePopup()

  const title = ref<string>()

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
      title: '必填',
      dataIndex: 'required',
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
      width: 150
    }
  ]

  // 所属模板组 id
  const templateGroupId = ref<number>()
  // 数据表格
  let tableState = useTable<TemplateProperty>({
    pageRequest: (query: PageParam) => {
      const params = Object.assign({ groupId: templateGroupId.value }, query)
      return queryTemplatePropertyPage(params)
    }
  })
  const { dataSource, pagination, loading } = tableState

  const editableData = reactive<UnwrapRef<Record<string, TemplateProperty>>>({})

  function handleAdd() {
    if (editableData[0]) {
      message.error('请先保存正在创建的属性')
      return
    }
    let temp = { id: 0, required: 0 } as TemplateProperty
    editableData[0] = temp
    dataSource.value.push(temp)
  }

  function handleEdit(id: number) {
    editableData[id] = cloneDeep(dataSource.value.filter(item => id === item.id)[0])
  }

  function handleSave(id: number) {
    let editableDatum = editableData[id]
    editableDatum.groupId = templateGroupId.value
    if (id === 0) {
      delete editableDatum.id
      loading.value = true
      doRequest({
        request: addTemplateProperty(editableDatum),
        successMessage: '保存成功！',
        onSuccess: () => tableState.reloadTable(false)
      })
    } else {
      loading.value = true
      doRequest({
        request: updateTemplateProperty(editableDatum),
        successMessage: '修改成功！',
        onSuccess: () => tableState.reloadTable(false)
      })
      delete editableData[id]
    }
  }

  function handleCancel(id: number) {
    delete editableData[id]
    if (id === 0) {
      dataSource.value.splice(
        dataSource.value.findIndex(item => item.id === 0),
        1
      )
    }
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

  defineExpose<TemplatePropertyModalInstance>({
    open(templateGroup: TemplateGroup) {
      title.value = '属性配置-' + templateGroup.name
      templateGroupId.value = templateGroup.id
      tableState.loadData()
      handleOpen()
    }
  })
</script>
