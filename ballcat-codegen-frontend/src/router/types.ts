import { RouteRecordRaw } from 'vue-router'

export interface BallcatRouteRecordRaw extends Omit<RouteRecordRaw, 'meta' | 'children'> {
  meta?: {
    // 菜单 title
    title?: string
    // 菜单 icon
    icon?: string
    // 在菜单中隐藏
    hiddenInMenu?: boolean
  }
  children?: BallcatRouteRecordRaw[]
}
