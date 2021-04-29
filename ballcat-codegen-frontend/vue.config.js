const path = require('path')
const webpack = require('webpack')
const serverAddress = 'http://ballcat-admin:7777'

function resolve(dir) {
  return path.join(__dirname, dir)
}

/**
 * 判断当前是否是生产环境
 * @returns {boolean}
 */
function isProd() {
  return process.env.NODE_ENV === 'production'
}


const assetsCDN = {
  css: [],
  // https://unpkg.com/browse/vue@2.6.10/
  js: [
    '//cdn.jsdelivr.net/npm/vue@2.6.12/dist/vue.min.js',
    '//cdn.jsdelivr.net/npm/vue-router@3.5.1/dist/vue-router.min.js',
    '//cdn.jsdelivr.net/npm/vuex@3.6.2/dist/vuex.min.js',
    '//cdn.jsdelivr.net/npm/axios@0.21.1/dist/axios.min.js'
  ]
}

// webpack build externals
const prodExternals = {
  vue: 'Vue',
  'vue-router': 'VueRouter',
  vuex: 'Vuex',
  axios: 'axios'
}


const vueConfig = {
  configureWebpack: {
    // webpack plugins
    plugins: [
      // Ignore all locale files of moment.js
      new webpack.IgnorePlugin(/^\.\/locale$/, /moment$/)
    ],
    // if prod is on, add externals
    externals: isProd() ? prodExternals : {}
  },
  chainWebpack: (config) => {
    config.resolve.alias
      .set('@$', resolve('src'))

    // if prod is on
    // assets require on cdn
    if (isProd()) {
      config.plugin('html').tap(args => {
        args[0].cdn = assetsCDN
        return args
      })

      config.optimization.splitChunks({
        chunks: 'all',
        cacheGroups: {
          libs: {
            name: 'chunk-libs',
            test: /[\\/]node_modules[\\/]/,
            priority: 10,
            chunks: 'initial' // 只打包初始时依赖的第三方
          },
          antDesignVue: {
            name: 'chunk-AntDesignVue', // 单独将 AntDesignVue 拆包
            priority: 20,  // 权重要大于 libs 和 app 不然会被打包进 libs 或者 app
            test: /[\\/]node_modules[\\/]_?ant-design-vue(.*)/
          },
          commons: {
            name: 'chunk-commons',
            test: resolve('src/components'), // 可自定义拓展你的规则
            minChunks: 3, //  // 最小共用次数
            priority: 5,
            reuseExistingChunk: true
          }
        }
      })
    }
  },

  css: {
    loaderOptions: {
      less: {
        lessOptions: {
          modifyVars: {
            'primary-color': '#1DA57A',
            'link-color': '#1DA57A',
            'border-radius-base': '2px'
          },
          javascriptEnabled: true
        }
      }
    }
  },
  devServer: {
    port: 8000,
    proxy: {
      '^/api': {
        target: serverAddress,
        changeOrigin: true
      }
    }
  },

  // disable source map in production
  productionSourceMap: false,
  // Change build paths to make them Maven compatible
  // see https://cli.vuejs.org/config/
  outputDir: 'target/dist',
  assetsDir: 'static'
}

module.exports = vueConfig
