import axios from 'axios'
import type { AxiosResponse } from 'axios'
import notification from 'ant-design-vue/es/notification'
import type { BAxiosError } from '@/utils/axios/types'
import qs from 'qs'

// 创建 axios 实例
const axiosInstance = axios.create({
  baseURL: import.meta.env.BASE_URL + 'api', // api base_url
  timeout: 6000, // 请求超时时间
  paramsSerializer: function (params: any) {
    return qs.stringify(params, {
      // 数组的格式化方式为重复参数，例如 { a: ['1', '2']} => a=1&a=2
      arrayFormat: 'repeat',
      filter: (prefix: string, value: any) => {
        // 空字符串不进行提交
        if (typeof value == 'string' && value.length === 0) {
          return
        }
        return value
      }
    })
  }
})

// 请求失败处理函数
const onRejected = (error: BAxiosError) => {
  if (error.response) {
    const data = error.response.data
    if (error.response.status === 403) {
      notification.error({
        message: 'Forbidden',
        // @ts-ignore
        description: data.message
      })
      error.resolved = true
    }
    if (error.response.status === 401) {
      notification.error({
        message: 'Unauthorized',
        description: 'Authorization verification failed'
      })
      error.resolved = true
    }
  }
  return Promise.reject(error)
}

// response interceptor
axiosInstance.interceptors.response.use((response: AxiosResponse) => {
  const headers = response.headers
  if (
    headers != null &&
    headers['content-type'] &&
    headers['content-type'].startsWith('application/json')
  ) {
    return response.data
  } else {
    return response
  }
}, onRejected)

export default axiosInstance
