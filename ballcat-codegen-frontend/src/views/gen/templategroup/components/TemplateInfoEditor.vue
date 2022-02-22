<template>
  <div v-show="templateInfoMap.size === 0" class="pane-content pane-scroll">
    <code-gen-tips :template-group-id="templateGroupId" />
  </div>
  <div v-show="templateInfoMap.size !== 0" class="pane-scroll">
    <a-spin tip="保存中..." :spinning="fileSaving">
      <a-tabs
        v-model:activeKey="activeKey"
        :tab-bar-style="{ margin: 0 }"
        type="editable-card"
        hide-add
        class="file-editor-tab"
        @change="handlePaneChange"
        @edit="handlePaneEdit"
      >
        <a-tab-pane v-for="[key, info] in templateInfoMap" :key="key">
          <template #tab>
            <a-badge
              v-if="info.content !== contentStage.get(key)"
              color="#978d8d"
              status="processing"
            />
            {{ info.fileName }}
          </template>
        </a-tab-pane>
      </a-tabs>
      <div style="height: 100%; overflow: auto">
        <div ref="editorBox" :class="fullScreen && 'editor-fullscreen'" />
      </div>
    </a-spin>
  </div>
</template>

<script setup lang="ts">
  import { onMounted, reactive, ref, watch } from 'vue'
  import { doRequest } from '@/utils/axios/request'
  import { getTemplateInfo, updateTemplateInfoContent } from '@/api/gen/templateinfo'
  import { TemplateInfo } from '@/api/gen/model/templateinfo'
  import { TemplateDirectoryEntry } from '@/api/gen/model/templatedirectoryentry'
  import CodeGenTips from '@/views/gen/templategroup/components/CodeGenTips.vue'
  import Editor from '@/components/editor'
  import { ViewUpdate } from '@codemirror/view'
  import { message, Modal } from 'ant-design-vue'
  import {
    TemplateContent,
    TemplateInfoEditorInstance
  } from '@/views/gen/templategroup/components/types'

  let props = defineProps<{
    templateGroupId?: number
  }>()

  // 模板信息存储 map
  const templateInfoMap = reactive(new Map<number, TemplateContent>())
  // 内容暂存区
  const contentStage = reactive(new Map<number, string>())
  // 切换模板组时清空以上两项
  watch(
    () => props.templateGroupId,
    () => {
      templateInfoMap.clear()
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
    editor = new Editor(editorBox.value, '', handleUpdate, [
      {
        // 保存快捷键
        key: 'MOD-S',
        preventDefault: true,
        run: () => {
          save()
          return true
        }
      },
      {
        // 保存快捷键，由于 CTRL + S, 偶尔无法绑定，所以留个备份用
        key: 'F2',
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
    ])
  })

  // 选中节点后更新编辑器的内容
  watch(
    () => activeKey.value,
    () => {
      const key = activeKey.value
      if (editor) {
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
      if (templateInfoMap.get(targetKey).content === contentStage.get(targetKey)) {
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
      for (let key of templateInfoMap.keys()) {
        if (key === targetKey) {
          break
        }
        preKey = key
      }
    }
    // 删除标签
    templateInfoMap.delete(targetKey)
    contentStage.delete(targetKey)
    // 当全部标签删除时，显示提示
    if (templateInfoMap.size === 0) {
      activeKey.value = 0
    } else if (closeCurrent) {
      // 当关闭标签为第一个的时候，默认打开现在的第一个标签
      activeKey.value = preKey ? preKey : templateInfoMap.keys().next().value
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
    const templateInfo = templateInfoMap.get(id) as TemplateContent
    if (content === templateInfo.content) {
      return
    }

    // 服务端发起请求保存
    fileSaving.value = true
    const formData = { directoryEntryId: id, content: content }
    doRequest(updateTemplateInfoContent(formData), {
      successMessage: '保存成功！',
      onSuccess() {
        // 同步更新本地内容
        templateInfo.content = content
      },
      onFinally() {
        fileSaving.value = false
      }
    })
  }

  defineExpose<TemplateInfoEditorInstance>({
    checkSaveState(): boolean {
      for (let key of contentStage.keys()) {
        if (contentStage.get(key) !== templateInfoMap.get(key)?.content) {
          return false
        }
      }
      return true
    },
    editTemplateInfo(entry: TemplateDirectoryEntry): void {
      const targetKey = entry.id as number
      if (templateInfoMap.has(targetKey)) {
        activeKey.value = targetKey
        return
      }
      // 远程加载模板文件详情信息
      doRequest(getTemplateInfo(entry.id), {
        successMessage: false,
        onSuccess(res) {
          const templateInfo = res.data as TemplateInfo
          const content = templateInfo.content || ''
          templateInfoMap.set(targetKey, {
            id: entry.id as number,
            content: content,
            fileName: entry.fileName as string
          })
          contentStage.set(targetKey, content)
          activeKey.value = targetKey
        }
      })
    }
  })
</script>

<style scoped lang="less">
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
