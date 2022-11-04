import { defineStore } from 'pinia'
import type { GeneratorOption } from '@/api/gen/generate/types'

interface GeneratorConfig {
  dsName: string
  useTable: 1 | 0
  options: GeneratorOption
}

export const useGeneratorConfigStore = defineStore('generatorConfigStore', {
  state: (): GeneratorConfig => ({
    dsName: '',
    useTable: 0,
    options: {
      tableNames: [],
      templateGroupKey: '',
      templateEntryIds: [],
      genProperties: {},
      tablePrefix: ''
    }
  }),
  getters: {
    isUseTable: state => {
      return state.useTable === 1
    }
  }
})
