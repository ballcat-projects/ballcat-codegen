import { AxiosError } from 'axios'
import type { AxiosResponse } from 'axios'
import { message } from 'ant-design-vue'
import type { R } from '@/utils/axios/types'
import { ResponseHandler } from '@/utils/axios/types'

// 默认的响应处理器
const defaultResponseHandler: ResponseHandler = {
  checkRequestSuccess: (res: R) => res.code === 200,
  // 成功消息提示
  successMessage: true,
  // 失败消息提示
  failMessage: true,
  // 错误消息提示
  errorMessage: true
}

function handleSuccess<T>(finalHandler: ResponseHandler<T>, res: R<T>) {
  const successMessage = finalHandler.successMessage
  if (successMessage) {
    // 如果 successMessage 是字符串类型，则使用 successMessage 做为提示语
    if (typeof successMessage == 'string') {
      message.success(successMessage)
    } else {
      message.success(res.message)
    }
  }
  finalHandler.onSuccess && finalHandler.onSuccess(res)
}

function handleFail<T>(finalHandler: ResponseHandler<T>, res: R<T>) {
  const failMessage = finalHandler.failMessage
  if (failMessage) {
    // 如果 failMessage 是字符串类型，则使用 failMessage 做为提示语
    if (typeof failMessage == 'string') {
      message.error(failMessage)
    } else {
      message.error(res.message)
    }
  }
  finalHandler.onFail && finalHandler.onFail(res)
}

function handleError<T>(finalHandler: ResponseHandler<T>, e: AxiosError & { resolved: boolean }) {
  // 未被 axios拦截器处理过，则在这里继续处理
  if (finalHandler.errorMessage && !e.resolved) {
    const errorMessage = e.response?.data?.message || e.message || 'error request'
    message.error(errorMessage)
  }
  finalHandler.onError && finalHandler.onError(e)
}

/**
 * 请求方法封装
 * @param request promise
 * @param responseHandler 用户设置的 handler
 */
export function doRequest<T>(
  request: Promise<AxiosResponse<R<T>>>,
  responseHandler: ResponseHandler<T> = {}
) {
  // 填充默认值
  const finalHandler = Object.assign({}, defaultResponseHandler, responseHandler)
  request
    .then(response => {
      // 这里由于上面的拦截器处理过了，所以直接强转
      const res = response as never as R<T>
      if (finalHandler.checkRequestSuccess && finalHandler.checkRequestSuccess(res)) {
        handleSuccess(finalHandler, res)
      } else {
        handleFail(finalHandler, res)
      }
    })
    .catch(e => {
      handleError(finalHandler, e)
    })
    .finally(() => {
      finalHandler.onFinally && finalHandler.onFinally()
    })
}
