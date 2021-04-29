// https://eslint.vuejs.org/user-guide
module.exports = {
  parser: 'vue-eslint-parser',
  parserOptions: {
    'parser': 'babel-eslint',
    'ecmaVersion': 2020,
    'sourceType': 'module'
  },
  extends: [
    // add more generic rulesets here, such as:
    // 'eslint:recommended',
    'plugin:vue/base',
    // 'plugin:vue/vue3-recommended'
    'plugin:vue/recommended' // Use this if you are using Vue.js 2.x.
  ],
  rules: {
    // override/add rules settings here, such as:
    // 'vue/no-unused-vars': 'error'
    "no-console": process.env.NODE_ENV === "production" ? 2 : 0,
    "no-debugger": process.env.NODE_ENV === "production" ? 2 : 0,

    // 'vue/require-default-prop': 'off',

    // 关闭使用 v-html 的检查
    'vue/no-v-html': 'off',
    // 3个属性以上才进行换行
    'vue/max-attributes-per-line': ['error', {
      'singleline': 3,
      'multiline': {
        'max': 1,
        'allowFirstLine': false
      }
    }],
    'vue/singleline-html-element-content-newline': 'off',
    'vue/multiline-html-element-content-newline': 'off'
  }
}
