<template>
  <a-modal
    v-model:open="visible"
    :title="title"
    :mask-closable="false"
    :footer="null"
    :body-style="{ padding: '12px 18px' }"
    :width="900"
    @cancel="handleClose"
  >
    <template-property-table-page
      v-show="showTable"
      ref="templatePropertyTablePageRef"
      @create="handleCreate"
      @update="handleUpdate"
    />
    <template-property-edit-page
      v-show="!showTable"
      ref="templatePropertyEditPageRef"
      @back-page="backToPage"
    />
  </a-modal>
</template>

<script setup lang="ts">
// import type
import { nextTick, ref } from 'vue'
import { usePopup } from '@/hooks/popup'
import TemplatePropertyTablePage from '@/views/gen/template-property/TemplatePropertyTablePage.vue'
import TemplatePropertyEditPage from '@/views/gen/template-property/TemplatePropertyEditPage.vue'
import type { TemplatePropertyModalInstance } from '@/views/gen/template-group/types'
import type { TemplateGroup } from '@/api/gen/template-group/types'
import type { TemplateProperty } from '@/api/gen/template-property/types'

const templatePropertyTablePageRef = ref()
const templatePropertyEditPageRef = ref()

let currentTemplateGroup: TemplateGroup | undefined = undefined

const { visible, handleOpen, handleClose } = usePopup()

const title = ref<string>()

const showTable = ref<boolean>(true)

function switchPage() {
  showTable.value = !showTable.value
}

function backToPage(reloadTable = false) {
  reloadTable && templatePropertyTablePageRef.value.reloadTable()
  switchPage()
}

function handleCreate() {
  switchPage()
  templatePropertyEditPageRef.value.create(currentTemplateGroup?.groupKey)
}

function handleUpdate(record: TemplateProperty) {
  switchPage()
  templatePropertyEditPageRef.value.update(record)
}

defineExpose<TemplatePropertyModalInstance>({
  open(templateGroup: TemplateGroup) {
    currentTemplateGroup = templateGroup
    showTable.value = true
    title.value = '属性配置-' + templateGroup.name
    nextTick(() => {
      templatePropertyTablePageRef.value.load(templateGroup)
    })
    handleOpen()
  }
})
</script>
