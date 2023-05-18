<template>
  <a-card
    :bordered="false"
    :body-style="{ minHeight: '600px', display: 'flex', flexDirection: 'column' }"
    class="card-class"
  >
    <div class="steps-title">
      <a-steps :current="currentStepNumber" :items="stepInfos" />
    </div>

    <div style="flex: 1; padding: 24px 48px 0">
      <!-- 模板组选择 -->
      <template-group-select-step
        v-show="currentStepNumber === 0"
        ref="templateGroupSelectStepRef"
      />

      <!-- 模板配置 -->
      <template-config-step v-show="currentStepNumber === 1" ref="templateConfigStepRef" />

      <!-- 数据源选择 -->
      <table-select-step v-show="currentStepNumber === 2" ref="tableSelectStepRef" />

      <!-- 代码生成 -->
      <generate-step v-show="currentStepNumber === 3" ref="generateStepRef" />
    </div>

    <a-row type="flex" class="steps-action" justify="center">
      <a-space :size="12">
        <a-button v-if="currentStepNumber > 0" @click="prev"> 上一步 </a-button>
        <a-button v-if="currentStepNumber < stepInfos.length - 2" type="primary" @click="next">
          下一步
        </a-button>
        <a-button v-if="currentStepNumber === stepInfos.length - 2" type="primary" @click="next">
          代码生成
        </a-button>
        <a-button
          v-if="currentStepNumber === stepInfos.length - 1"
          type="primary"
          @click="download"
        >
          打包下载
        </a-button>
      </a-space>
    </a-row>
  </a-card>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import TemplateConfigStep from './TemplateConfigStep.vue'
import type { GenerateStepInstance } from '@/views/gen/codegen/types'
import TableSelectStep from '@/views/gen/codegen/TableSelectStep.vue'
import GenerateStep from '@/views/gen/codegen/GenerateStep.vue'
import { useGeneratorConfigStore } from '@/store'
import TemplateGroupSelectStep from '@/views/gen/codegen/TemplateGroupSelectStep.vue'
import { message } from 'ant-design-vue'
import { generate } from '@/api/gen/generate'
import { remoteFileDownload } from '@/utils/file-util'

// 进页面的时候先重置下，代码生成的配置
useGeneratorConfigStore().$reset()

const templateGroupSelectStepRef = ref<GenerateStepInstance>()
const templateConfigStepRef = ref<GenerateStepInstance>()
const tableSelectStepRef = ref<GenerateStepInstance>()
const generateStepRef = ref<GenerateStepInstance>()

interface StepInfo {
  title: string
  componentRef: GenerateStepInstance | undefined
}

const stepInfos = computed<StepInfo[]>(() => [
  {
    title: '模板选择',
    componentRef: templateGroupSelectStepRef.value
  },
  {
    title: '模板配置',
    componentRef: templateConfigStepRef.value
  },
  {
    title: '数据源选择',
    componentRef: tableSelectStepRef.value
  },
  {
    title: '代码生成',
    componentRef: generateStepRef.value
  }
])

const currentStepNumber = ref<number>(0)

const next = () => {
  const stepInfo = stepInfos.value[currentStepNumber.value]
  if (stepInfo.componentRef?.validate) {
    stepInfo.componentRef
      ?.validate()
      .then(() => {
        enterNext(stepInfo)
      })
      .catch(e => {
        message.error(e.message || '请将当前页面选项填写完整')
      })
  } else {
    enterNext(stepInfo)
  }
}
const prev = () => {
  currentStepNumber.value--
}

function enterNext(stepInfo: StepInfo) {
  stepInfo.componentRef?.next?.()
  const nextStepNumber = currentStepNumber.value + 1
  const nextStepInfo = stepInfos.value[nextStepNumber]
  nextStepInfo.componentRef?.enter?.()
  currentStepNumber.value++
}

function download() {
  const generatorConfigStore = useGeneratorConfigStore()
  generate(generatorConfigStore.dsName, generatorConfigStore.options)
    .then(response => {
      remoteFileDownload(response, 'BallCat-CodeGen.zip')
    })
    .catch(() => {
      message.error('代码生成异常')
    })
}
</script>

<style scoped lang="less">
.card-class {
  display: flex;
  flex-direction: column;
}
.steps-title {
  width: 60%;
  margin: auto;
  margin-bottom: 24px;
}
.steps-action {
  margin-top: 24px;
}
</style>
