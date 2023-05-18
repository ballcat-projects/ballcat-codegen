/**
 * 从响应头中解析对应的文件名
 * @param headers
 * @returns {string}
 */
import type { AxiosResponse, AxiosResponseHeaders, RawAxiosResponseHeaders } from 'axios'

function resolveFilename(headers: RawAxiosResponseHeaders | AxiosResponseHeaders): string {
  const match = headers['content-disposition'].match(/filename=(.*)/)
  if (match && match.length > 0) {
    return decodeURI(match[1])
  }
  return ''
}

/**
 * 远程文件下载
 * @param response
 * @param filename
 */
export function remoteFileDownload(response: AxiosResponse, filename?: string) {
  if (response.data) {
    // 构造一个blob对象来处理数据，并设置文件类型
    const headers = response.headers
    const contentType = headers['content-type']
    const blob = new Blob([response.data], { type: contentType })

    // 不存在则从响应头中解析
    if (!filename) {
      filename = resolveFilename(headers)
      console.log(filename)
    }
    fileDownload(blob, filename)
  }
}

/**
 * 根据 blob 和 文件名进行文件下载
 * @param blob
 * @param filename
 */
export function fileDownload(blob: Blob, filename: string) {
  //兼容IE10，后续可以移除
  const navigator = window.navigator as any
  if (navigator.msSaveOrOpenBlob) {
    navigator.msSaveBlob(blob, filename)
  } else {
    const href = URL.createObjectURL(blob) //创建新的URL表示指定的blob对象
    const a = document.createElement('a') //创建a标签
    a.style.display = 'none'
    a.href = href // 指定下载链接
    a.download = filename //指定下载文件名
    a.click() //触发下载
    URL.revokeObjectURL(a.href) //释放URL对象
  }
}
