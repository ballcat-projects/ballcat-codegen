import { ref, unref } from 'vue'
import type { Ref } from 'vue'
import { doRequest } from '@/utils/axios/request'
import { AxiosResponse } from 'axios'
import { R } from '@/utils/axios/types'
import { PageParam, PageResult } from '@/api/types'
import { PaginationProps } from 'ant-design-vue'
import { Key, SorterResult } from 'ant-design-vue/es/table/interface'

interface TableOptions<RecordType> {
  pageRequest: (pageParams: PageParam) => Promise<AxiosResponse<R<PageResult<RecordType>>>>
  queryParam?: object
  onPageLoadSuccess?: (pageResult: PageResult<RecordType>) => void
}

export default function <RecordType>(tableOptions: TableOptions<RecordType>) {
  // 表格加载状态
  const loading = ref<boolean>(false)

  // 表格数据
  const dataSource = ref<RecordType[]>([]) as Ref<RecordType[]>

  // 分页器设置
  const pagination = ref<PaginationProps>({
    total: 0,
    current: 1,
    pageSize: 10,
    showSizeChanger: true,
    showTotal: (total: number, range: number[]) => {
      return range[0] + '-' + range[1] + ' 共' + total + '条'
    }
  })

  // 筛选参数字段
  const filters = ref({})
  // 默认排序字段
  let sortField: Key | readonly Key[] = ''
  // 升序 asc/降序 desc
  let sortOrder: Key = ''

  /**
   * 合并查询参数，分页参数，排序参数，过滤参数
   * @returns {{current: number, size: number} & {sortOrders: null, sortFields: null}}
   */
  const pageParams = (): PageParam => {
    return Object.assign(
      {},
      tableOptions.queryParam,
      {
        current: pagination.value.current,
        size: pagination.value.pageSize
      },
      {
        // TODO 多列排序支持
        sortFields: sortField,
        sortOrders: sortOrder
      },
      unref(filters)
    )
  }

  /**
   * 表格数据加载方法
   */
  const loadData = function () {
    loading.value = true
    const params = pageParams()

    doRequest({
      request: tableOptions.pageRequest(params),
      onSuccess: res => {
        const pageResult = res.data as PageResult<RecordType>
        // 为防止删除数据后导致页面当前页面数据长度为 0 ,自动翻页到上一页
        if (
          pageResult.records.length === 0 &&
          pagination.value.current &&
          pagination.value.current > 1
        ) {
          pagination.value.current--
          loadData()
          return
        }
        dataSource.value = pageResult.records
        pagination.value.total = pageResult.total
        tableOptions.onPageLoadSuccess?.(pageResult)
      },
      onError: () => (dataSource.value = []),
      onFinally: () => {
        loading.value = false
      }
    })
  }

  /**
   * 表格重新加载方法
   * 如果参数为 true, 则强制刷新到第一页
   * @param bool
   */
  const reloadTable = (bool = true): void => {
    bool && (pagination.value.current = 1)
    loadData()
  }

  /* 分页、排序、筛选变化时进行数据更新  */
  const handleTableChange = (
    newPagination: PaginationProps,
    newFilters: object,
    newSorter: SorterResult<RecordType>
  ): void => {
    filters.value = newFilters
    if (newSorter && newSorter.field) {
      if (newSorter.order) {
        sortField = newSorter.field
        sortOrder = newSorter.order === 'ascend' ? 'asc' : 'desc'
      } else {
        sortField = ''
        sortOrder = ''
      }
    }
    pagination.value = newPagination
    loadData()
  }

  // 已选择的行 key 集合
  const selectedRowKeys = ref<Key[]>([])
  // 已选择的行数据集合
  const selectedRows = ref<RecordType[]>([]) as Ref<RecordType[]>

  // 选择
  const onSelectChange = (newSelectedRowKeys: Key[], newSelectedRows: RecordType[]) => {
    selectedRowKeys.value = newSelectedRowKeys
    selectedRows.value = newSelectedRows
  }

  // 清空选项
  const clearSelected = (): void => {
    selectedRowKeys.value = []
    selectedRows.value = []
  }

  return {
    dataSource,
    pagination,
    filters,
    sortField,
    sortOrder,
    loading,
    loadData,
    handleTableChange,
    reloadTable,
    selectedRowKeys,
    selectedRows,
    onSelectChange,
    clearSelected
  }
}
