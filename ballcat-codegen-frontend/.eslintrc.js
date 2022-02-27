const { defineConfig } = require('eslint-define-config')

module.exports = defineConfig({
  root: true, // 当前配置为根配置，将不再从上级文件夹查找配置
  env: {
    browser: true,
    node: true,
    es2021: true
  },
  extends: [
    'eslint:recommended',
    'plugin:vue/vue3-recommended',
    'plugin:@typescript-eslint/recommended',
    'plugin:prettier/recommended'
  ],
  parser: 'vue-eslint-parser',
  parserOptions: {
    ecmaVersion: 'latest',
    ecmaFeatures: {
      jsx: true
    },
    parser: '@typescript-eslint/parser',
    sourceType: 'module'
  },
  plugins: [
    'vue',
    '@typescript-eslint',
    'prettier'
  ],
  rules: {
    // 换行符检测关闭，避免跨系统时的冲突
    "prettier/prettier": ["error", {"endOfLine": "auto"}],
    'linebreak-style': 'off'
  },
  globals: {
    defineProps: 'readonly',
    defineEmits: 'readonly',
    defineExpose: 'readonly',
    withDefaults: 'readonly'
  }

})
