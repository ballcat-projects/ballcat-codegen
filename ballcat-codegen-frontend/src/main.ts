import { createApp } from 'vue'
import App from './App.vue'

import 'reset-css'

const app = createApp(App)

// 路由
import router from './router'
app.use(router)

// pinia 状态组件
import { createPinia } from 'pinia'
const pinia = createPinia()
app.use(pinia)

// 代码高亮
import 'highlight.js/lib/common'
import 'highlight.js/styles/github.css'
import hljsVuePlugin from '@highlightjs/vue-plugin'
app.use(hljsVuePlugin)

app.mount('#app')
