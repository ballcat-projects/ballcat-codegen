/* eslint-env node */
require('@rushstack/eslint-patch/modern-module-resolution')

module.exports = {
  root: true, // 当前配置为根配置，将不再从上级文件夹查找配置
  env: {
    browser: true,
    node: true,
  },
  extends: [
    'eslint:recommended',
    'plugin:vue/vue3-recommended',
    '@vue/eslint-config-typescript/recommended',
    '@vue/eslint-config-prettier',
  ],
  rules: {
    // 换行符检测关闭，避免跨系统时的冲突
    "prettier/prettier": ["error", {"endOfLine": "auto"}],
    'linebreak-style': 'off',
    // 允许忽略 ts 检测的注释
    '@typescript-eslint/ban-ts-comment': 'off'
  },
  globals: {
    defineProps: 'readonly',
    defineEmits: 'readonly',
    defineExpose: 'readonly',
    withDefaults: 'readonly'
  }
}
