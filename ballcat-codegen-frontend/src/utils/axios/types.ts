import { AxiosError, AxiosResponse } from 'axios'

/**
 * 服务端统一返回信息
 */
export interface R<T = unknown> {
  // 状态码
  code: number
  // 数据
  data?: T
  // 信息
  message: string
}

/**
 * axios 错误信息
 */
export interface BAxiosError extends AxiosError {
  // 当前错误是否已处理
  resolved?: boolean
}

/**
 * axios 的请求选项
 */
export interface RequestOptions<T = unknown> {
  // 请求 Promise
  request: Promise<AxiosResponse<R<T>>>
  // 判断是否请求成功
  checkRequestSuccess?: (res: R<T>) => boolean
  // 成功消息提示
  successMessage?: boolean | string
  // 失败消息提示
  failMessage?: boolean | string
  // 错误消息提示
  errorMessage?: boolean | string
  // 成功时的处理函数
  onSuccess?: (res: R<T>) => void
  // 失败时的处理函数
  onFail?: (res: R<T>) => void
  // 错误时的处理函数
  onError?: (e: BAxiosError) => void
  // 最终的执行函数
  onFinally?: () => void
}
