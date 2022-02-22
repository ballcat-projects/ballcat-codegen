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
    <a-row>
      <a-col :span="6" class="template-entry-tree-wrapper">
        <a-directory-tree
          class="template-entry-tree"
          :tree-data="templateEntryTree"
          :show-icon="true"
          style="overflow: initial"
          @dblclick="ondblclick"
        />
      </a-col>
      <a-col :span="18" class="template-content-wrapper">
        <highlightjs :language="language" :code="code" style="overflow: initial" />
      </a-col>
    </a-row>
  </a-modal>
</template>
<script setup lang="ts">
  import 'highlight.js/lib/common'
  import hljsVuePlugin from '@highlightjs/vue-plugin'
  import { defineComponent, ref } from 'vue'
  import { usePopup } from '@/hooks/popupHooks'
  import { listToTree } from '@/utils/treeUtil'
  import 'highlight.js/styles/github.css'
  import { TemplateEntryTree } from '@/api/gen/model/generate'
  import { DataNode } from 'ant-design-vue/lib/vc-tree/interface'
  import { PreviewModalInstance } from './types'

  // 不能删除
  const highlightjs = defineComponent(hljsVuePlugin.component)

  let { visible, handleOpen, handleClose } = usePopup()

  const language = ref<string>('javascript')
  const code = ref<string>('双击文件查看代码信息')
  const modalTitle = ref<string>('代码预览')
  const templateEntryTree = ref<TemplateEntryTree[]>()

  const ondblclick = (e: Event, node: { dataRef: TemplateEntryTree }) => {
    // 非文件类型不加载
    const entry = node.dataRef
    if (entry.type === 2) {
      language.value = entry.fileName
      code.value = entry.content ? entry.content : ''
      modalTitle.value = '代码预览 - ' + entry.fileName
    }
  }

  function buildTree(templateEntryList: TemplateEntryTree[]) {
    return listToTree(templateEntryList, 0, {
      attributeMapping: treeNode => {
        const dataNode = treeNode as unknown as DataNode
        dataNode.isLeaf = treeNode.type === 2
        dataNode.title = treeNode.fileName
      }
    })
  }

  defineExpose<PreviewModalInstance>({
    open: (templateEntryList?: TemplateEntryTree[]) => {
      templateEntryTree.value = templateEntryList ? buildTree(templateEntryList) : []
      handleOpen()
    }
  })
</script>

<style lang="less">
  .preview-modal-wrapper {
    .ant-modal-header {
      padding: 8px 24px !important;
    }
    .ant-modal-close-x {
      height: 40px;
      line-height: 40px;
    }
  }

  .template-entry-tree {
    background: #f5f5f5;
  }

  .template-entry-tree-wrapper {
    height: 650px;
    overflow: auto;
    border-right-style: solid;
    border-right-width: 1px;
    border-right-color: #dcdcdc;
    background: #f5f5f5;
  }

  .template-content-wrapper {
    padding-left: 8px;
    height: 650px;
    overflow: auto;
  }

  pre code.hljs {
    overflow-x: visible;
  }
</style>
