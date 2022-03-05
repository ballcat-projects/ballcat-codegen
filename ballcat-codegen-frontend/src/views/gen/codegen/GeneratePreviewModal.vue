<template>
  <a-modal
    :title="modalTitle"
    :width="1100"
    :visible="visible"
    :footer="null"
    :centered="true"
    :body-style="{ padding: 0 }"
    wrap-class-name="preview-modal-wrapper"
    @cancel="handleClose"
  >
    <splitpanes class="preview-splitpanes">
      <pane size="25" class="template-entry-tree-wrapper">
        <a-directory-tree
          class="template-entry-tree"
          :tree-data="fileEntryTree"
          :show-icon="true"
          style="overflow: initial"
          @dblclick="ondblclick"
        />
      </pane>
      <pane size="75" class="template-content-wrapper">
        <highlightjs :language="language" :code="code" style="overflow: initial" />
      </pane>
    </splitpanes>

    <!-- 代码复制下载，放在面板里面渲染会出问题，所以拿到外面 -->
    <span style="position: absolute; top: 62px; right: 28px">
      <a-button type="link" style="padding: 0" :disabled="!selectedEntry" @click="handleDownload">
        <template #icon>
          <DownloadOutlined />
        </template>
        下载
      </a-button>
      <a-button
        type="link"
        style="padding: 0; margin-left: 12px"
        :disabled="!selectedEntry"
        @click="handleCopy"
      >
        <template #icon>
          <CopyOutlined v-if="!copied" />
          <CheckOutlined v-else />
        </template>
        复制
      </a-button>
    </span>
  </a-modal>
</template>
<script setup lang="ts">
  import 'highlight.js/lib/common'
  import hljsVuePlugin from '@highlightjs/vue-plugin'
  import { defineComponent, ref } from 'vue'
  import { usePopup } from '@/hooks/popup'
  import { listToTree } from '@/utils/tree-util'
  import 'highlight.js/styles/github.css'
  import { DataNode } from 'ant-design-vue/lib/vc-tree/interface'
  import { PreviewModalInstance } from './types'
  import { FileEntry } from '@/api/gen/template-entry/types'
  import { Splitpanes, Pane } from 'splitpanes'
  import 'splitpanes/dist/splitpanes.css'
  import { CopyOutlined, CheckOutlined, DownloadOutlined } from '@ant-design/icons-vue'
  import { useClipboard } from '@vueuse/core'
  import { fileDownload } from '@/utils/file-util'

  // 当前选中的 entry
  const selectedEntry = ref<FileEntry>()

  // vueuse copy
  const { copy, copied } = useClipboard()

  /** 代码复制 */
  const handleCopy = () => {
    if (selectedEntry.value) {
      copy(code.value)
    }
  }

  /** 代码下载 */
  const handleDownload = () => {
    const entry = selectedEntry.value
    if (entry) {
      const blob = new Blob([code.value], {
        type: 'text/plain'
      })
      fileDownload(blob, entry.filename)
    }
  }

  // 不能删除
  const highlightjs = defineComponent(hljsVuePlugin.component)

  let { visible, handleOpen, handleClose } = usePopup()

  const language = ref<string>('javascript')
  const code = ref<string>('双击文件查看代码信息')
  const modalTitle = ref<string>('代码预览')
  const fileEntryTree = ref<FileEntry[]>()

  const ondblclick = (e: Event, node: { dataRef: FileEntry }) => {
    // 非文件类型不加载
    const entry = node.dataRef
    if (entry.type === 2) {
      selectedEntry.value = entry
      language.value = entry.filename
      code.value = entry.content ? entry.content : ''
      modalTitle.value = '代码预览 - ' + entry.filename
    }
  }

  function buildTree(fileEntryList: FileEntry[]) {
    return listToTree(fileEntryList, '', {
      idKey: 'filePath',
      parentIdKey: 'parentFilePath',
      attributeMapping: treeNode => {
        const dataNode = treeNode as unknown as DataNode
        dataNode.key = treeNode.filePath
        dataNode.isLeaf = treeNode.type === 2
        dataNode.title = treeNode.filename
      }
    })
  }

  defineExpose<PreviewModalInstance>({
    open: (fileEntryList?: FileEntry[]) => {
      fileEntryTree.value = fileEntryList ? buildTree(fileEntryList) : []
      code.value = '双击文件查看代码信息'
      selectedEntry.value = undefined
      handleOpen()
    }
  })
</script>

<style lang="less">
  pre code.hljs {
    overflow-x: visible;
  }

  // 分割器
  .preview-splitpanes.splitpanes--vertical > .splitpanes__splitter {
    min-width: 5px;
    cursor: col-resize;
  }
</style>

<style scoped lang="less">
  :deep(.ant-tree.ant-tree-block-node .ant-tree-list-holder-inner .ant-tree-node-content-wrapper) {
    white-space: nowrap;
  }

  :deep(.template-entry-tree) {
    background: #fbfbfb;
    width: 100%;
    min-width: 200px;
  }

  .preview-modal-wrapper {
    .ant-modal-header {
      padding: 8px 24px !important;
    }
    .ant-modal-close-x {
      height: 40px;
      line-height: 40px;
    }
  }

  .template-entry-tree-wrapper {
    height: 650px;
    overflow: auto;
    border-right-style: solid;
    border-right-width: 1px;
    border-right-color: #dcdcdc;
    background: #fbfbfb;
  }

  .template-content-wrapper {
    height: 650px;
    overflow: auto;
  }
</style>
