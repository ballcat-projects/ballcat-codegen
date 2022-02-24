import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import { h, resolveComponent } from 'vue'

const menuRouters: Array<RouteRecordRaw> = [
  {
    path: '/gen',
    redirect: '/gen/codegen',
    meta: { icon: 'SettingOutlined', title: '代码生成器' },
    component: {
      render() {
        return h(resolveComponent('router-view'))
      }
    },
    children: [
      {
        path: '/gen/codegen',
        name: 'CodeGen',
        meta: { title: '代码生成器' },
        component: () => import('@/views/gen/codegen/GeneratePage.vue')
      },
      {
        path: '/gen/template/group',
        name: 'TemplateGroup',
        meta: { title: '模板组管理' },
        component: () => import('@/views/gen/template-group/TemplateGroupPage.vue')
      },
      {
        path: '/gen/datasouce',
        name: 'DataSource',
        meta: { title: '数据源配置' },
        component: () => import('@/views/gen/datasource-config/DataSourceConfigPage.vue')
      }
    ]
  },
  {
    path: '404',
    name: 'notFound',
    component: () => import('../views/404.vue'),
    meta: { hiddenInMenu: true }
  }
]
const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../layouts/BasicLayout.vue'),
    children: [...menuRouters],
    redirect: '/gen'
  },
  {
    path: '/:catchAll(.*)',
    name: '404',
    // hiddenInMenu: true,
    component: () => import('../views/404.vue')
  }
]

const router = createRouter({
  history: createWebHistory('/'),
  routes
})

export { menuRouters }

export default router
