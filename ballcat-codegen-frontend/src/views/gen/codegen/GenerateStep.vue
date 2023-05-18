<template>
  <div style="position: relative">
    <!-- 代码复制下载，放在面板里面渲染会出问题，所以拿到外面 -->
    <span
      v-show="selectedEntry && selectedEntry.type === TemplateEntryTypeEnum.TEMPLATE_FILE"
      style="position: absolute; top: 0; right: 28px"
    >
      <a-button type="link" style="padding: 0" @click="handleDownload">
        <template #icon>
          <DownloadOutlined />
        </template>
        下载
      </a-button>
      <a-button type="link" style="padding: 0; margin-left: 12px" @click="handleCopy">
        <template #icon>
          <CopyOutlined v-if="!copied" />
          <CheckOutlined v-else />
        </template>
        复制
      </a-button>
    </span>

    <splitpanes class="preview-splitpanes">
      <pane size="25" class="template-entry-tree-wrapper">
        <a-skeleton v-if="loading" style="margin: 16px; width: 260px" :paragraph="{ rows: 8 }" />
        <a-directory-tree
          v-else
          class="template-entry-tree"
          :tree-data="fileEntryTree"
          :show-icon="true"
          style="overflow: initial"
          @dblclick="ondblclick"
        />
      </pane>
      <pane
        v-show="selectedEntry && selectedEntry.type === TemplateEntryTypeEnum.TEMPLATE_FILE"
        size="75"
        class="template-content-wrapper"
      >
        <highlightjs :language="language" :code="code" style="overflow: initial" />
      </pane>
      <pane
        v-show="!selectedEntry || selectedEntry.type === TemplateEntryTypeEnum.BINARY_FILE"
        size="75"
        class="template-content-wrapper flex"
      >
        <div v-show="!selectedEntry">
          <h3>双击文件查看代码信息</h3>
        </div>
        <h3 v-show="selectedEntry">二进制文件无法预览</h3>
        <a-button v-show="selectedEntry" type="link" @click="handleBinaryDownload">
          下载文件
        </a-button>
      </pane>
    </splitpanes>
  </div>
</template>
<script setup lang="ts">
import { ref, toRaw } from 'vue'

import { listToTree } from '@/utils/tree-util'
import type { DataNode } from 'ant-design-vue/es/vc-tree/interface'
import type { PreviewFile } from '@/api/gen/template-entry/types'
import { TemplateEntryTypeEnum } from '@/api/gen/template-entry/types'
import { Pane, Splitpanes } from 'splitpanes'
import 'splitpanes/dist/splitpanes.css'
import { CheckOutlined, CopyOutlined, DownloadOutlined } from '@ant-design/icons-vue'
import { useClipboard } from '@vueuse/core'
import { fileDownload, remoteFileDownload } from '@/utils/file-util'
import type { GenerateStepInstance } from './types'
import { doRequest } from '@/utils/axios/request'
import { preview } from '@/api/gen/generate'
import { useGeneratorConfigStore } from '@/store'
import { binaryFileDownload } from '@/api/gen/template-entry'

// 当前选中的 entry
const selectedEntry = ref<PreviewFile>()

// vueuse copy
const { copy, copied } = useClipboard({ legacy: true })

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

/** 二进制文件下载 */
const handleBinaryDownload = () => {
  const entry = selectedEntry.value
  if (entry) {
    binaryFileDownload(entry.id).then(response => {
      remoteFileDownload(response)
    })
  }
}

const language = ref<string>('javascript')
const code = ref<string>('')
const modalTitle = ref<string>('代码预览')
const fileEntryTree = ref<PreviewFile[]>()

const ondblclick = (e: Event, node: { dataRef: PreviewFile }) => {
  // 非文件类型不加载
  const entry = node.dataRef
  selectedEntry.value = entry
  if (entry.type === TemplateEntryTypeEnum.TEMPLATE_FILE) {
    language.value = entry.filename
    code.value = entry.templateContent ? entry.templateContent : ''
    modalTitle.value = '代码预览 - ' + entry.filename
  }
}

function buildTree(fileEntryList: PreviewFile[]) {
  fileEntryList.sort((a, b) =>
    // @ts-ignore
    a.type === b.type ? a.filename.localeCompare(b.filename) : a.type - b.type
  )
  return listToTree(fileEntryList, '', {
    idKey: 'filePath',
    parentIdKey: 'parentFilePath',
    attributeMapping: treeNode => {
      const dataNode = treeNode as unknown as DataNode
      dataNode.key = treeNode.filePath
      dataNode.isLeaf = treeNode.type !== TemplateEntryTypeEnum.FOLDER
      dataNode.title = treeNode.filename
    }
  })
}

// 加载状态
const loading = ref(false)

defineExpose<GenerateStepInstance>({
  enter: () => {
    loading.value = true
    const generatorConfigStore = useGeneratorConfigStore()
    doRequest({
      request: preview(generatorConfigStore.dsName, toRaw(generatorConfigStore.options)),
      onSuccess: res => {
        fileEntryTree.value = res.data ? buildTree(res.data) : []
        code.value = '双击文件查看代码信息'
        selectedEntry.value = undefined
      },
      onFinally: () => (loading.value = false)
    })
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

.flex {
  /*flex 布局*/
  display: flex;
  // 垂直布局
  flex-direction: column;
  /*实现垂直居中*/
  align-items: center;
  /*实现水平居中*/
  justify-content: center;
  text-align: justify;
}
</style>
