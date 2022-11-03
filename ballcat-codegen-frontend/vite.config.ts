import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import Components from 'unplugin-vue-components/vite'
import { AntDesignVueResolver } from 'unplugin-vue-components/resolvers'
import { createStyleImportPlugin, AndDesignVueResolve } from 'vite-plugin-style-import'
import { antdvStyleDeps } from './src/utils/resolvers'
import { fileURLToPath, URL } from 'url'

const serverAddress = 'http://ballcat-admin:7777'
// const serverAddress = 'http://codegen.ballcat.cn'

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
    // 组件按需引入
    Components({
      resolvers: [
        AntDesignVueResolver({
          importStyle: 'less'
        })
      ]
    }),
    // 样式按需引入
    createStyleImportPlugin({
      resolves: [AndDesignVueResolve()],
      libs: [
        {
          libraryName: 'ant-design-vue',
          esModule: true,
          resolveStyle: name => {
            return `ant-design-vue/es/${name}/style/index`
          }
        }
      ]
    })
  ],
  server: {
    proxy: {
      '/api': {
        target: serverAddress,
        changeOrigin: true
      }
    }
  },
  css: {
    preprocessorOptions: {
      less: {
        javascriptEnabled: true,
        modifyVars: {
          'root-entry-name': 'default',
          'primary-color': '#9f6ae0'
        }
      }
    }
  },
  optimizeDeps: {
    include: [
      ...antdvStyleDeps,
      'ant-design-vue',
      'ant-design-vue/es',
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
    assetsDir: 'static'
  }
})
