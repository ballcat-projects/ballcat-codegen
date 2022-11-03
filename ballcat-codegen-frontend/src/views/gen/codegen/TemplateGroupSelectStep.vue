<template>
  <a-row type="flex" justify="center" :gutter="[20, 20]">
    <a-col v-for="item in templateGroupSelectData" :key="item.value">
      <a-card
        hoverable
        style="width: 300px; height: 120px"
        :class="item.value === templateGroupKey ? 'template-group-card-checked' : ''"
        @click="selectGroupKey(item.value)"
      >
        <a-card-meta :title="item.name" :description="item.value">
          <template #avatar>
            <a-avatar src="https://joeschmoe.io/api/v1/random" />
          </template>
        </a-card-meta>
      </a-card>
    </a-col>
  </a-row>
</template>

<script setup lang="ts">
  // 模板组的选择数据
  import { ref } from 'vue'
  import type { SelectData } from '@/api/types'
  import { doRequest } from '@/utils/axios/request'
  import { listSelectData } from '@/api/gen/template-group'
  import { useGeneratorOptionStore } from '@/store'
  import type { GenerateStepInstance } from '@/views/gen/codegen/types'

  // 当前选中的模板组 key
  const templateGroupKey = ref<string>()
  function selectGroupKey(groupKey: string) {
    templateGroupKey.value = groupKey
  }

  const templateGroupSelectData = ref<SelectData[]>([])
  doRequest({
    request: listSelectData(),
    onSuccess: res => {
      const data = res.data as SelectData[]
      templateGroupSelectData.value = data
      if (data && data.length > 0) {
        selectGroupKey(data[0].value)
      }
    }
  })

  defineExpose<GenerateStepInstance>({
    next: () => {
      useGeneratorOptionStore().templateGroupKey = templateGroupKey.value
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
