<template>
  <div v-show="templateEntryMap.size === 0" class="pane-content pane-scroll" style="height: 100%; overflow: auto">
    <code-gen-tips :template-group-id="templateGroupId" />
  </div>
  <div
    v-show="templateEntryMap.size !== 0"
    class="pane-scroll"
    style="height: 100%;"
  >
    <a-spin wrapperClassName="spin-box" tip="保存中..." :spinning="fileSaving">
      <a-tabs
        v-model:activeKey="activeKey"
        :tab-bar-style="{ margin: 0 }"
        type="editable-card"
        hide-add
        class="file-editor-tab"
        @change="handlePaneChange"
        @edit="handlePaneEdit"
      >
        <a-tab-pane v-for="[key, info] in templateEntryMap" :key="key">
          <template #tab>
            <a-badge
              v-if="info.content !== contentStage.get(key)"
              color="#978d8d"
              status="processing"
            />
            {{ info.filename }}
          </template>
        </a-tab-pane>
      </a-tabs>
      <div style="height: calc(100% - 32px)" ref="editorBox" :class="fullScreen && 'editor-fullscreen'" />
    </a-spin>
  </div>
</template>

<script setup lang="ts">
  import { onMounted, reactive, ref, watch } from 'vue'
  import { doRequest } from '@/utils/axios/request'
  import { TemplateEngines, TemplateEntry } from '@/api/gen/template-entry/types'
  import CodeGenTips from '@/views/gen/template-group/components/CodeGenTips.vue'
  import Editor from '@/components/editor'
  import { ViewUpdate } from '@codemirror/view'
  import { message, Modal } from 'ant-design-vue'
  import { TemplateContentEditorInstance } from '@/views/gen/template-group/components/types'
  import { updateTemplateEntryContent } from '@/api/gen/template-entry'

  let props = defineProps<{
    templateGroupId?: number
  }>()

  // 模板信息存储 map
  const templateEntryMap = reactive(new Map<number, TemplateEntry>())
  // 内容暂存区
  const contentStage = reactive(new Map<number, string>())
  // 切换模板组时清空以上两项
  watch(
    () => props.templateGroupId,
    () => {
      templateEntryMap.clear()
      contentStage.clear()
    }
  )

  // 文件保存中
  const fileSaving = ref<boolean>(false)
  // 当前选中的节点 key
  const activeKey = ref<number>(0)
  // 是否全屏
  const fullScreen = ref<boolean>(false)

  // 编辑器
  const editorBox = ref()
  let editor: Editor
  onMounted(() => {
    const handleUpdate = (v: ViewUpdate) => {
      if (v.docChanged) {
        contentStage.set(activeKey.value, editor?.getEditorDoc() || '')
      }
    }
    editor = new Editor(editorBox.value, '', handleUpdate,
      [
        {
          // 保存快捷键
          key: 'MOD-s',
          preventDefault: true,
          run: () => {
            save()
            return true
          }
        },
        {
          // 全屏/退出全屏快捷键，ESC 无法绑定
          key: 'F11',
          preventDefault: true,
          run: () => {
            fullScreen.value = !fullScreen.value
            return true
          }
        }
      ],
      {
        spec: {
          "&.cm-editor": {height: "100%"},
          "& .cm-scroller": { overflow: 'auto !important' },
        }
      }
    )
  })

  // 选中节点后更新编辑器的内容
  watch(
    () => activeKey.value,
    () => {
      const key = activeKey.value
      if (editor) {
        const entry = templateEntryMap.get(key)
        if (entry) {
          editor.templateEngine = TemplateEngines.getEngineName(entry.engineType)
        }
        editor.setEditorDoc(contentStage.get(key) || '')
      }
    },
    { immediate: true }
  )

  function handlePaneChange(newActiveKey: number) {
    activeKey.value = newActiveKey
  }

  function handlePaneEdit(targetKey: number, action: string) {
    if (action === 'remove') {
      if (templateEntryMap.get(targetKey)?.content === contentStage.get(targetKey)) {
        handleRemove(targetKey)
      } else {
        Modal.confirm({
          content: '该文件改动未保存，是否确定关闭？',
          onOk: () => handleRemove(targetKey)
        })
      }
    }
  }

  function handleRemove(targetKey: number) {
    // 是否关闭当前标签
    const closeCurrent = activeKey.value === targetKey
    // 获取关闭标签的前一个标签
    let preKey = null
    if (closeCurrent) {
      for (let key of templateEntryMap.keys()) {
        if (key === targetKey) {
          break
        }
        preKey = key
      }
    }
    // 删除标签
    templateEntryMap.delete(targetKey)
    contentStage.delete(targetKey)
    // 当全部标签删除时，显示提示
    if (templateEntryMap.size === 0) {
      activeKey.value = 0
    } else if (closeCurrent) {
      // 当关闭标签为第一个的时候，默认打开现在的第一个标签
      activeKey.value = preKey ? preKey : templateEntryMap.keys().next().value
    }
  }

  function save() {
    // 修改模板文件内容
    let id = activeKey.value
    const content = contentStage.get(id)
    if (!content) {
      message.error('未找到需要保存的文件内容！')
      return
    }

    // 文件内容没改，则不更新
    const templateEntry = templateEntryMap.get(id) as TemplateEntry
    if (content === templateEntry.content) {
      return
    }

    // 服务端发起请求保存
    fileSaving.value = true
    doRequest({
      request: updateTemplateEntryContent(id, content),
      successMessage: '保存成功！',
      onSuccess() {
        // 同步更新本地内容
        templateEntry.content = content
      },
      onFinally() {
        fileSaving.value = false
      }
    })
  }

  defineExpose<TemplateContentEditorInstance>({
    checkSaveState(): boolean {
      // 检查是否有未保存的文件
      for (let key of contentStage.keys()) {
        let templateEntry = templateEntryMap.get(key)
        if (templateEntry && templateEntry.content !== contentStage.get(key)) {
          return false
        }
      }
      return true
    },
    editContent(entry: TemplateEntry): void {
      const targetKey = entry.id as number
      if (templateEntryMap.has(targetKey)) {
        activeKey.value = targetKey
        return
      }
      templateEntryMap.set(targetKey, entry)
      contentStage.set(targetKey, entry.content || '')
      activeKey.value = targetKey
    }
  })
</script>

<style scoped lang="less">
  .spin-box {
    height: 100%;
    :deep(.ant-spin-container) {
      height: 100%;
    }
  }
  .badge-status-unsaved {
    position: relative;
    background-color: #919191;
    top: -1px;
    display: inline-block;
    width: 6px;
    height: 6px;
    vertical-align: middle;
    border-radius: 50%;
    margin-right: 5px;
  }
  .badge-status-unsaved:after {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    border: 1px solid #764475;
    border-radius: 50%;
    -webkit-animation: antStatusProcessing 1.2s infinite ease-in-out;
    animation: antStatusProcessing 1.2s infinite ease-in-out;
    content: '';
  }

  .file-editor-tab {
    :deep(.ant-tabs-tab) {
      height: 32px !important;
      line-height: 32px !important;
      padding: 0 6px 0 12px !important;
      border-radius: 0 !important;
      margin-left: 0 !important;
    }
    :deep(.ant-tabs-tab-remove) {
      margin-left: 0 !important;
    }
    :deep(.ant-tabs-nav-container) {
      height: 32px !important;
    }
    :deep(.ant-tabs-tab-active) {
      height: 32px !important;
      line-height: 30px !important;
    }
    :deep(.ant-tabs-ink-bar) {
      visibility: visible;
    }
  }

  :deep(.cm-content) {
    min-height: 467px !important;
  }
  :deep(.cm-gutter) {
    min-height: 467px !important;
  }
  :deep(.cm-scroller) {
    overflow: unset;
  }

  .editor-fullscreen {
    position: fixed;
    top: 0;
    left: 0;
    width: 100vw;
    height: 100vh;
    overflow: auto;
    box-sizing: border-box;
    z-index: 9999;
    background: white;
  }
</style>
