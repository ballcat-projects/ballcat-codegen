<template>
  <a-modal
    :title="title"
    :width="1100"
    :visible="visible"
    :footer="null"
    :centered="true"
    :body-style="{ padding: 0 }"
    class="preview-modal"
    @cancel="handleClose"
  >
    <a-row>
      <a-col :span="6" class="template-entry-tree-wrapper">
        <a-directory-tree
          :tree-data="templateEntryTree"
          :show-icon="true"
          :draggable="true"
          @dblclick="ondblclick"
        />
      </a-col>
      <a-col :span="18" class="template-content-wrapper">
        <pre style="overflow: initial">
          <code class="hljs" style="overflow: initial" v-html="codeContent" />
        </pre>
      </a-col>
    </a-row>
  </a-modal>
</template>
<script>
import hljs from 'highlight.js'
import 'highlight.js/styles/github-gist.css'
import { listToTree } from '@/utils/treeUtil'


hljs.registerLanguage('java', require('highlight.js/lib/languages/java'))
hljs.registerLanguage('xml', require('highlight.js/lib/languages/xml'))
hljs.registerLanguage('html', require('highlight.js/lib/languages/xml'))
hljs.registerLanguage('vue', require('highlight.js/lib/languages/xml'))
hljs.registerLanguage('javascript', require('highlight.js/lib/languages/javascript'))
hljs.registerLanguage('sql', require('highlight.js/lib/languages/sql'))

export default {
  name: 'GeneratePreviewModal',
  data() {
    return {
      // 预览参数
      title: '代码预览',
      visible: false,
      templateEntryTree: [],
      codeContent: '双击文件预览代码',
      defaultActiveKey: null
    }
  },
  methods: {
    /** 高亮显示 */
    highlightedCode(code, key) {
      const language = key.substring(key.lastIndexOf('.') + 1)
      const result = hljs.highlight(code || '', {
        language: language,
        ignoreIllegals: true
      })
      return result.value || '&nbsp;'
    },
    show(data) {
      this.visible = true
      this.templateEntryTree = listToTree(data, 0, (treeNode, item) => {
        treeNode.isLeaf = item.type === 2
        treeNode.title = item.fileName
      })
    },
    handleClose() {
      this.visible = false
      this.templateEntryTree = []
      this.title = '代码预览'
      this.codeContent = '双击文件预览代码'
    },
    ondblclick(e, node) {
      // 非文件类型不加载
      const entry = node.dataRef
      if (entry.type === 2) {
        this.codeContent = this.highlightedCode(entry.content, entry.fileName)
        this.title = '代码预览 - ' + entry.fileName
      }
    }
  }
}
</script>

<style scoped>
.preview-modal >>> .ant-modal-header {
  padding: 8px 24px !important;
}

.preview-modal >>> .ant-modal-close-x {
  height: 40px;
  line-height: 40px;
}

.preview-modal >>> .ant-tabs-bar {
  margin: 0 !important;
}

.preview-modal >>> .ant-tabs-bar {
  margin: 0 !important;
}

.preview-modal >>> .ant-tabs-nav-container {
  line-height: 1;
}

.preview-modal >>> .ant-tabs-nav .ant-tabs-tab {
  margin: 0;
}

.preview-modal >>> .ant-tabs {
  line-height: 1.25;
}

.template-entry-tree-wrapper {
  height: 650px;
  overflow: auto;
  border-right-style: solid;
  border-right-width: 1px;
  border-right-color: #dcdcdc;
  background-color: #f5f5f5;
}

.template-content-wrapper {
  padding-left: 8px;
  height: 650px;
  overflow: auto
}
</style>
