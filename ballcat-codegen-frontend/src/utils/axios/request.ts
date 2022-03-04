import { message } from 'ant-design-vue'
import type { BAxiosError, R, RequestOptions } from '@/utils/axios/types'

/**
 * 请求方法封装
 * @param requestOptions 请求配置
 */
export function doRequest<T>(requestOptions: RequestOptions<T>) {
  // 填充默认值
  const finalOptions: RequestOptions<T> = Object.assign(
    {
      checkRequestSuccess: (res: R) => res.code === 200,
      // 成功消息提示
      successMessage: false,
      // 失败消息提示
      failMessage: true,
      // 错误消息提示
      errorMessage: true
    },
    requestOptions
  )
  finalOptions.request
    .then(response => {
      // 这里由于上面的拦截器处理过了，所以直接强转
      const res = response as never as R<T>
      if (finalOptions.checkRequestSuccess && finalOptions.checkRequestSuccess(res)) {
        handleSuccess(finalOptions, res)
      } else {
        handleFail(finalOptions, res)
      }
    })
    .catch(e => {
      handleError(finalOptions, e)
    })
    .finally(() => {
      finalOptions.onFinally && finalOptions.onFinally()
    })
}

function handleSuccess<T>(requestOptions: RequestOptions<T>, res: R<T>) {
  const successMessage = requestOptions.successMessage
  if (successMessage) {
    // 如果 successMessage 是字符串类型，则使用 successMessage 做为提示语
    if (typeof successMessage == 'string') {
      message.success(successMessage)
    } else {
      message.success(res.message)
    }
  }
  requestOptions.onSuccess && requestOptions.onSuccess(res)
}

function handleFail<T>(requestOptions: RequestOptions<T>, res: R<T>) {
  const failMessage = requestOptions.failMessage
  if (failMessage) {
    // 如果 failMessage 是字符串类型，则使用 failMessage 做为提示语
    if (typeof failMessage == 'string') {
      message.error(failMessage)
    } else {
      message.error(res.message)
    }
  }
  requestOptions.onFail && requestOptions.onFail(res)
}

function handleError<T>(requestOptions: RequestOptions<T>, e: BAxiosError) {
  // 未被 axios拦截器处理过，则在这里继续处理
  if (requestOptions.errorMessage && !e.resolved) {
    const errorMessage = e.response?.data?.message || e.message || 'error request'
    message.error(errorMessage)
  }
  requestOptions.onError && requestOptions.onError(e)
}
