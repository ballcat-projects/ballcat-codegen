<template>
  <div>
    <template v-if="!initialized">
      <a-row type="flex" justify="center" :gutter="[20, 20]">
        <a-col v-for="item in [1, 2, 3, 4]" :key="item">
          <a-card style="width: 300px; height: 120px" :body-style="{ padding: '0 24px' }">
            <a-skeleton size="small" :paragraph="{ rows: 2 }" />
          </a-card>
        </a-col>
      </a-row>
    </template>

    <template v-if="initialized">
      <a-result
        v-show="!hasTemplateGroup"
        status="warning"
        title="请先创建一个模板组"
        sub-title="在项目的 /template 文件夹下有预置的模板 SQL 文件，可以选择需要的模板进行初始化"
      >
        <template #extra>
          <a-button key="console" type="primary" @click="goToTemplateGroupPage"
            >创建模板组</a-button
          >
        </template>
      </a-result>

      <a-row v-show="hasTemplateGroup" type="flex" justify="center" :gutter="[20, 20]">
        <a-col v-for="item in templateGroupSelectData" :key="item.value">
          <a-card
            hoverable
            style="width: 300px; height: 120px"
            :class="item.value === templateGroupKey ? 'template-group-card-checked' : ''"
            @click="selectGroup(item)"
          >
            <a-card-meta :title="item.name" :description="item.attributes?.remarks">
              <template #avatar>
                <a-avatar :src="item.attributes?.icon" />
              </template>
            </a-card-meta>
          </a-card>
        </a-col>
      </a-row>
    </template>
  </div>
</template>

<script setup lang="ts">
// 模板组的选择数据
import { computed, ref } from 'vue'
import type { SelectData } from '@/api/types'
import { doRequest } from '@/utils/axios/request'
import { listSelectData } from '@/api/gen/template-group'
import { useGeneratorConfigStore } from '@/store'
import type { GenerateStepInstance } from '@/views/gen/codegen/types'
import { useRouter } from 'vue-router'

const initialized = ref(false)

const generatorConfigStore = useGeneratorConfigStore()

// 当前选中的模板组 key
const templateGroupKey = ref<string>()
function selectGroup(groupItem: SelectData) {
  templateGroupKey.value = groupItem.value
  // @ts-ignore
  generatorConfigStore.useTable = groupItem.attributes.useTable
}

const templateGroupSelectData = ref<SelectData[]>([])
const hasTemplateGroup = computed(
  () => templateGroupSelectData.value && templateGroupSelectData.value.length > 0
)
doRequest({
  request: listSelectData(),
  onSuccess: res => {
    initialized.value = true
    const data = res.data as SelectData[]
    templateGroupSelectData.value = data
    if (data && data.length > 0) {
      selectGroup(data[0])
    }
  }
})

const router = useRouter()
const goToTemplateGroupPage = () => {
  router.push({ name: 'TemplateGroup' })
}

defineExpose<GenerateStepInstance>({
  validate: () => {
    return templateGroupKey.value
      ? Promise.resolve()
      : Promise.reject({ message: '请选择一个模板组' })
  },
  next: () => {
    generatorConfigStore.options.templateGroupKey = templateGroupKey.value
  }
})
</script>

<script lang="ts">
export default {
  name: 'TemplateGroupSelectStep'
}
</script>

<style scoped lang="less">
.template-group-card-checked {
  background-color: #bae0ff;
  border-color: #1677ff;
}

.template-group-card-checked:after {
  position: absolute;
  inset-block-start: 2px;
  inset-inline-end: 2px;
  width: 0;
  height: 0;
  border: 6px solid #1677ff;
  border-block-end: 6px solid transparent;
  border-inline-start: 6px solid transparent;
  border-start-end-radius: 2px;
  content: '';
}
</style>
