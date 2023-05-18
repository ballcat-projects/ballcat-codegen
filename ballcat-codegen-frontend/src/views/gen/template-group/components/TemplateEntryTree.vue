<template>
  <div
    class="pane-content pane-scroll"
    style="height: 100%; overflow: auto"
    @contextmenu.prevent="clearSelectAndShowMenu"
  >
    <div style="padding: 10px; width: fit-content; min-width: 100%">
      <h1 style="text-align: center; white-space: nowrap">右键即可创建文件或文件夹</h1>

      <a-skeleton v-if="treeLoading" style="margin: 16px; width: 260px" :paragraph="{ rows: 8 }" />
      <a-directory-tree
        v-else
        v-model:selectedKeys="selectedKeys"
        v-model:expandedKeys="expandedKeys"
        :tree-data="treeData"
        :show-icon="true"
        :draggable="true"
        @drop="handleDrop"
        @dblclick="handleDblClick"
        @right-click="selectAndShowMenu"
      />

      <a-menu v-if="menuVisible" :style="menuStyle">
        <template v-if="selectedEntry">
          <a-menu-item key="1" :style="menuItemStyle" @click="updateEntry()">
            <edit-outlined />
            <span>编辑</span>
          </a-menu-item>
          <a-menu-item key="2" :style="menuItemStyle" @click="removeEntry()">
            <delete-outlined />
            <span>删除</span>
          </a-menu-item>
        </template>
        <template v-if="!selectedEntry || selectedEntry.type !== 2">
          <a-menu-item
            key="3"
            :style="menuItemStyle"
            @click="createdEntry(TemplateEntryTypeEnum.FOLDER)"
          >
            <diff-outlined />
            <span>新建文件夹</span>
          </a-menu-item>
          <a-menu-item
            key="4"
            :style="menuItemStyle"
            @click="createdEntry(TemplateEntryTypeEnum.TEMPLATE_FILE)"
          >
            <file-add-outlined />
            <span>新建模板文件</span>
          </a-menu-item>
          <a-menu-item
            key="5"
            :style="menuItemStyle"
            @click="createdEntry(TemplateEntryTypeEnum.BINARY_FILE)"
          >
            <file-add-outlined />
            <span>新建二进制文件</span>
          </a-menu-item>
        </template>
      </a-menu>
    </div>
  </div>

  <!-- 模板目录项删除弹窗 -->
  <template-entry-remove-modal ref="templateEntryRemoveModalRef" @done="treeLoad" />
  <!-- 模板目录项表单弹窗 -->
  <template-entry-form-modal ref="templateEntryFormModalRef" @done="treeLoad" />
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { listToTree } from '@/utils/tree-util'
import { doRequest } from '@/utils/axios/request'
import { listTemplateEntry, moveEntry } from '@/api/gen/template-entry'
import type { TemplateEntry } from '@/api/gen/template-entry/types'
import { TemplateEntryTypeEnum } from '@/api/gen/template-entry/types'
import type { DataNode, EventDataNode, Key } from 'ant-design-vue/es/vc-tree/interface'
import { message } from 'ant-design-vue'
import { DeleteOutlined, DiffOutlined, EditOutlined, FileAddOutlined } from '@ant-design/icons-vue'
import type { NodeDragEventParams } from 'ant-design-vue/es/vc-tree/contextTypes'
import TemplateEntryRemoveModal from '@/views/gen/template-group/components/TemplateEntryRemoveModal.vue'
import TemplateEntryFormModal from '@/views/gen/template-group/components/TemplateEntryFormModal.vue'
import type {
  TemplateEntryFormModalInstance,
  TemplateEntryRemoveModalInstance
} from '@/views/gen/template-group/components/types'

const emits = defineEmits<{
  (e: 'edit-template-info', entry: TemplateEntry): void
}>()

const templateGroupKey = ref<string>()

// 删除弹窗
const templateEntryRemoveModalRef = ref<TemplateEntryRemoveModalInstance>()
// 新建修改的表单弹窗
const templateEntryFormModalRef = ref<TemplateEntryFormModalInstance>()

// 目录项的树结构数据
const treeData = ref<TemplateEntry[]>([])

// 当前已选中的 keys
const selectedKeys = ref<string[]>([])
// 当前已展开的 keys
const expandedKeys = ref<string[]>([])
// 当前选中的目录项
const selectedEntry = ref<TemplateEntry>()

// 右键菜单是否展示
const menuVisible = ref<boolean>(false)
// 菜单的样式
const menuStyle = reactive({
  boxShadow: '0 2px 4px rgba(0, 0, 0, 0.12), 0 0 2px rgba(0, 0, 0, 0.04)',
  borderRadius: '2px',
  fontFamily: 'arial',
  zIndex: 999,
  padding: '2px 0 8px 0',
  position: 'fixed',
  top: '0',
  left: '0',
  border: '1px solid #eee'
})
// 菜单项的样式
const menuItemStyle = {
  height: '32px',
  lineHeight: '32px',
  fontSize: '15px',
  marginBottom: '0'
}

const treeLoading = ref(false)

/**
 * 加载 Entry Tree
 */
function treeLoad(expand = false) {
  treeLoading.value = true
  doRequest({
    // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
    request: listTemplateEntry(templateGroupKey.value!),
    onSuccess(res) {
      const list = res.data as TemplateEntry[]
      list.sort((a, b) =>
        // @ts-ignore
        a.type === b.type ? a.filename.localeCompare(b.filename) : a.type - b.type
      )
      treeData.value = listToTree(list, '0', {
        attributeMapping: treeNode => {
          const dataNode = treeNode as unknown as DataNode
          dataNode.isLeaf = treeNode.type !== TemplateEntryTypeEnum.FOLDER
          dataNode.title = treeNode.filename
          dataNode.style = { whiteSpace: 'nowrap' }
          if (!dataNode.templateContent) {
            dataNode.templateContent = ''
          }
        }
      })
      if (expand) {
        expandedKeys.value = treeData.value.map(item => item.id) as string[]
      }
    },
    onFinally: () => (treeLoading.value = false)
  })
}

/** 选中属性，显示右键菜单 */
function selectAndShowMenu(info: { event: MouseEvent; node: EventDataNode }) {
  const event = info.event
  const node = info.node
  const dataRef = node.dataRef as unknown as TemplateEntry
  // 设置选中数据
  selectedKeys.value = [dataRef.id] as string[]
  selectedEntry.value = dataRef

  // 防止冒泡
  event.cancelBubble = true

  // 右键菜单属性
  menuVisible.value = true
  menuStyle.top = event.clientY + 'px'
  menuStyle.left = event.clientX + 'px'
  document.body.addEventListener('click', closeMenu)
}

/** 取消选中，显示右键菜单 */
function clearSelectAndShowMenu(event: MouseEvent) {
  // 清空选中数据
  selectedKeys.value = []
  selectedEntry.value = undefined

  menuVisible.value = true
  menuStyle.top = event.clientY + 'px'
  menuStyle.left = event.clientX + 'px'
  document.body.addEventListener('click', closeMenu)
}

/** 关闭右键菜单 */
function closeMenu() {
  menuVisible.value = false
  document.body.removeEventListener('click', closeMenu)
}

/**
 * 双击目录项的处理逻辑
 */
function handleDblClick(e: MouseEvent, node: EventDataNode) {
  // 非文件类型不加载
  const entry = node.dataRef as unknown as TemplateEntry
  selectedEntry.value = entry
  if (entry.type !== TemplateEntryTypeEnum.FOLDER) {
    emits('edit-template-info', entry)
  }
}

/**
 * 处理目录项的移动
 */
function handleDrop(
  info: NodeDragEventParams & {
    dragNode: EventDataNode
    dragNodesKeys: Key[]
    dropPosition: number
    dropToGap: boolean
  }
) {
  // 被移动的目录项
  const entry = info.dragNode.dataRef as unknown as TemplateEntry
  // 目标目录项
  const targetEntry = info.node.dataRef as unknown as TemplateEntry
  // 是否移动到其子节点，否则是平级
  const horizontalMove = info.dropToGap
  // 无需移动
  const parentId = horizontalMove ? targetEntry.parentId : targetEntry.id
  if (entry.parentId === parentId) {
    return
  }
  if (!horizontalMove && targetEntry.type !== 1) {
    message.error('不能移动到非文件夹目标内部')
    return
  }
  doRequest({
    request: moveEntry(entry.id, targetEntry.id, horizontalMove),
    successMessage: '移动成功',
    onSuccess() {
      treeLoad()
    }
  })
}

/**
 * 删除
 */
function removeEntry() {
  if (selectedEntry.value && selectedEntry.value.id) {
    templateEntryRemoveModalRef.value?.open(selectedEntry.value.id)
  } else {
    message.warning('请选择一个目录项')
  }
}

/**
 * 新建
 * @param entryType 文件类型
 */
function createdEntry(entryType: TemplateEntryTypeEnum) {
  let parentId = '0'
  let parentFilename = '根目录'
  if (selectedEntry.value) {
    const dataRef = selectedEntry.value
    parentFilename = dataRef?.filename as string
    parentId = dataRef.id as string
  }
  // 打开弹窗
  templateEntryFormModalRef.value?.add(parentFilename, {
    groupKey: templateGroupKey.value,
    parentId: parentId,
    type: entryType,
    templateContent: ''
  })
}

/* 更新目录项 */
function updateEntry() {
  if (selectedEntry.value) {
    templateEntryFormModalRef.value?.update(selectedEntry.value)
  } else {
    message.warning('请选择一个目录项')
  }
}

defineExpose({
  updateEntry: updateEntry,
  load(key: string) {
    // 只在第一次加载时默认展开第一级的文件，防止用户提交表单后tree被折叠
    const expand = templateGroupKey.value !== key
    templateGroupKey.value = key
    treeLoad(expand)
  }
})
</script>
