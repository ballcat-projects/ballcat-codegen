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

// 布局 Store：集中管理折叠/抽屉/宽屏与窗口尺寸
interface LayoutState {
  collapsed: boolean
  drawerOpen: boolean
  wideMode: boolean
  vw: number
  manualCollapsedOverride: boolean | null
}

const BP_SM = 1024
const BP_MD = 1366

export const useLayoutStore = defineStore('layoutStore', {
  state: (): LayoutState => ({
    collapsed: localStorage.getItem('layout:collapsed') === '1',
    drawerOpen: false,
    wideMode: localStorage.getItem('layout:wide') === '1',
  vw: typeof window !== 'undefined' ? window.innerWidth : 0,
  manualCollapsedOverride: null
  }),
  getters: {
    isOverlay: state => state.vw <= BP_SM,
    autoCollapsed: state => state.vw <= BP_MD,
    effectiveCollapsed(): boolean {
      // @ts-ignore
      const s = this as unknown as LayoutState & { isOverlay: boolean; autoCollapsed: boolean }
      if (s.isOverlay) return false
      // 如果存在手动覆盖，则优先使用用户选择
      if (this.manualCollapsedOverride !== null) return this.manualCollapsedOverride
      // 否则遵循自动规则或本地存储的折叠状态
      return s.autoCollapsed ? true : this.collapsed
    }
  },
  actions: {
    applyResize() {
      this.vw = typeof window !== 'undefined' ? window.innerWidth : this.vw
      if (!this.isOverlay) this.drawerOpen = false
      // 退出 overlay 时保留用户覆盖；进入 overlay 时清理覆盖
      if (this.isOverlay) {
        this.manualCollapsedOverride = null
      }
    },
    toggleCollapsed() {
      if (this.isOverlay) {
        this.drawerOpen = !this.drawerOpen
        return
      }
      // 在桌面态，切换时记录用户覆盖，并更新存储
      const next = !this.effectiveCollapsed
      this.manualCollapsedOverride = next
      this.collapsed = next
      localStorage.setItem('layout:collapsed', next ? '1' : '0')
    },
    toggleDrawer(next?: boolean) {
      this.drawerOpen = typeof next === 'boolean' ? next : !this.drawerOpen
    },
    toggleWide() {
      this.wideMode = !this.wideMode
      localStorage.setItem('layout:wide', this.wideMode ? '1' : '0')
    }
  }
})
