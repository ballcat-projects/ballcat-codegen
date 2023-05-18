<template>
  <a-card
    ref="entryEditor"
    size="small"
    :bordered="false"
    :head-style="{ minHeight: '48px' }"
    :body-style="{
      padding: 0,
      height: isFullscreen ? 'calc(100vh - 49px)' : 'calc(100vh - 156px)'
    }"
  >
    <template #title>
      <div style="position: relative; height: 32px; line-height: 32px; padding-left: 1%">
        <span>{{ templateGroup?.name }}</span>
        <span style="position: absolute; right: 1%">
          <a-space>
            <a-button @click="handleProperty">属性配置</a-button>
            <a-button @click="toggle">
              <template v-if="isFullscreen"> 退出全屏 </template>
              <template v-else> 全屏显示 </template>
            </a-button>
            <a-button @click="handleGoBack">返回上级</a-button>
          </a-space>
        </span>
      </div>
    </template>

    <splitpanes class="default-theme">
      <pane size="25" style="border-bottom-left-radius: 10px">
        <template-entry-tree ref="templateEntryTreeRef" @edit-template-info="editTemplateInfo" />
      </pane>
      <pane size="75" style="border-bottom-right-radius: 10px">
        <template-entry-content-editor ref="editorRef" @re-upload="updateTemplateEntry" />
      </pane>
    </splitpanes>
  </a-card>
</template>

<script setup lang="ts">
import { Splitpanes, Pane } from 'splitpanes'
import 'splitpanes/dist/splitpanes.css'
import TemplateEntryTree from '@/views/gen/template-group/components/TemplateEntryTree.vue'
import type { TemplateGroup } from '@/api/gen/template-group/types'
import { ref } from 'vue'
import type { TemplateEntry } from '@/api/gen/template-entry/types'
import TemplateEntryContentEditor from '@/views/gen/template-group/components/TemplateEntryContentEditor.vue'
import { Modal } from 'ant-design-vue'
import type { TemplateContentEditorInstance } from '@/views/gen/template-group/components/types'
import { useFullscreen } from '@vueuse/core'

const emits = defineEmits<{
  (e: 'handle-property'): void
  (e: 'go-back'): void
}>()

const entryEditor = ref<HTMLElement | null>(null)
const templateEntryTreeRef = ref()
const { isFullscreen, exit, toggle } = useFullscreen(entryEditor)

// 模板组
const templateGroup = ref<TemplateGroup>()

// 模板信息编辑器实例
const editorRef = ref<TemplateContentEditorInstance>()

/** 模板修改属性 **/
const handleProperty = () => {
  emits('handle-property')
}

/** 返回上级 */
function handleGoBack() {
  if (editorRef.value?.checkSaveState()) {
    isFullscreen.value && exit()
    emits('go-back')
    return
  }
  Modal.confirm({
    content: '部分文件已修改但未保存，确定放弃修改内容吗？ (选中文件编辑器后，按 Ctrl+S 保存）',
    onOk: () => emits('go-back')
  })
}

/** 编辑模板信息内容 */
function editTemplateInfo(entry: TemplateEntry) {
  editorRef.value?.editContent(entry)
}

/** 编辑模板 */
function updateTemplateEntry() {
  templateEntryTreeRef.value?.updateEntry()
}

defineExpose({
  edit(record: TemplateGroup) {
    templateGroup.value = record
    // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
    const templateGroupKey = record.groupKey!
    templateEntryTreeRef.value?.load(templateGroupKey)
    editorRef.value?.load(templateGroupKey)
  }
})
</script>

<style lang="less">
// 面板
.splitpanes.default-theme .splitpanes__pane {
  background-color: #ffffff !important;
  border-top: 1px solid #f2f2f2 !important;
}

// 分割器的背景色
.default-theme.splitpanes--vertical > .splitpanes__splitter {
  //background-color: #f2f2f2 !important;
  width: 3px;
  border-top: 1px solid #eee;
}

.pane-scroll::-webkit-scrollbar {
  /*滚动条整体样式*/
  width: 9px; /*高宽分别对应横竖滚动条的尺寸*/
  height: 10px;
}

.pane-scroll::-webkit-scrollbar-thumb {
  /*滚动条里面小方块*/
  border-radius: 8px;
  box-shadow: inset 0 0 5px rgba(0, 0, 0, 0.2);
  background: #a39e9e;
}

.pane-scroll::-webkit-scrollbar-track {
  /*滚动条里面轨道*/
  box-shadow: inset 0 0 5px rgba(0, 0, 0, 0.2);
  border-radius: 8px;
  background: #ededed;
}
</style>
