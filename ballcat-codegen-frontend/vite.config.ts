import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import Components from 'unplugin-vue-components/vite'
import { AntDesignVueResolver } from 'unplugin-vue-components/resolvers'
import { fileURLToPath, URL } from 'url'
import { splitVendorChunkPlugin } from 'vite'

const serverAddress = 'http://localhost:7777'
// const serverAddress = 'http://ballcat-admin:7777'
//const serverAddress = 'http://codegen.ballcat.cn'

// https://vitejs.dev/config/
export default defineConfig({
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  base: '/',
  plugins: [
    vue(),
    // 按需加载 ant-design-vue 组件
    Components({
      types: [
        {
          from: 'vue-router',
          names: ['RouterLink', 'RouterView']
        }
      ],
      resolvers: [
        AntDesignVueResolver({
          resolveIcons: true,
          importStyle: false
        })
      ]
    }),
    // 分块插件
    splitVendorChunkPlugin()
  ],
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: serverAddress,
        changeOrigin: true
      }
    }
  },
  optimizeDeps: {
    include: [
      'ant-design-vue',
      '@ant-design/icons-vue',
      'axios',
      'vue',
      'vue-router',
      '@vueuse/core',
      '@codemirror/commands',
      '@codemirror/lang-html',
      '@codemirror/lang-java',
      '@codemirror/lang-javascript',
      '@codemirror/language',
      '@codemirror/state',
      '@codemirror/theme-one-dark',
      '@codemirror/view',
      '@highlightjs/vue-plugin',
      'highlight.js',
      'splitpanes'
    ]
  },
  build: {
    outDir: 'target/dist',
    assetsDir: 'static',
    cssCodeSplit: false
  }
})
