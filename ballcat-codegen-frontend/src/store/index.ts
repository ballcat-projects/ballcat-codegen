import { defineStore } from 'pinia'
import type { GeneratorOption } from '@/api/gen/generate/types'

export const useGeneratorOptionStore = defineStore('counter', {
  state: (): GeneratorOption => ({
    dsName: '',
    tableNames: [],
    templateGroupKey: '',
    templateEntryIds: [],
    genProperties: {},
    tablePrefix: ''
  })
})
