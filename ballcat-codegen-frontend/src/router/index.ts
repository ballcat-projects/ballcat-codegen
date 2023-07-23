import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import type { BallcatRouteRecordRaw } from '@/router/types'

const menuRouters: Array<BallcatRouteRecordRaw> = [
  {
    path: '/codegen',
    name: 'CodeGen',
    meta: { title: '代码生成' },
    component: () => import('@/views/gen/codegen/index.vue')
  },
  {
    path: '/template/group',
    name: 'TemplateGroup',
    meta: { title: '模板管理' },
    component: () => import('@/views/gen/template-group/index.vue')
  },
  {
    path: '/field/type',
    name: 'FieldType',
    meta: { title: '数据类型管理' },
    component: () => import('@/views/gen/field-type/index.vue')
  },
  {
    path: '/datasource',
    name: 'DataSource',
    meta: { title: '数据源管理' },
    component: () => import('@/views/gen/datasource-config/index.vue')
  },
  {
    path: '404',
    name: 'notFound',
    component: () => import('../views/404.vue'),
    meta: { hiddenInMenu: true }
  }
]
const routes: Array<BallcatRouteRecordRaw> = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../layouts/BasicLayout.vue'),
    children: [...menuRouters],
    redirect: '/codegen'
  },
  {
    path: '/:catchAll(.*)',
    name: '404',
    // hiddenInMenu: true,
    component: () => import('../views/404.vue')
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: routes as RouteRecordRaw[]
})

export { menuRouters }

export default router
