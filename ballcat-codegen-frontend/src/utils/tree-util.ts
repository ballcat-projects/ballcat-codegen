/**
 * 数组转树形结构
 * @param list 源数组
 * @param parentId 父ID
 * @param attributeFill 数据处理函数
 */
export declare type Key = string | number

interface ListToTreeOptions<T> {
  /**
   * id字段名称
   */
  idKey?: string | null
  /**
   * parentId字段名称
   */
  parentIdKey?: string | null
  /**
   * 生成的children字段名称
   */
  childrenKey?: string | null
  /**
   * 属性转换的方法
   * @param treeNode 树节点对象
   */
  attributeMapping?: (treeNode: T) => void
}

export const listToTree = <T>(
  list: T[],
  parentId: Key,
  options: ListToTreeOptions<T> = {
    idKey: 'id',
    parentIdKey: 'parentId',
    childrenKey: 'children'
  }
) => {
  const tree: T[] = []
  fillTree(list, tree, parentId, options)
  return tree
}

/**
 * 数组转树形结构
 * @param list 源数组
 * @param tree 树
 * @param parentId 父ID
 * @param options 转换选项
 */
export const fillTree = <T>(list: T[], tree: T[], parentId: Key, options: ListToTreeOptions<T>) => {
  const idField = options.idKey || 'id'
  const parentIdField = options.parentIdKey || 'parentId'
  const childrenField = options.childrenKey || 'children'
  const attributeMapping = options.attributeMapping

  list.forEach(item => {
    const data = item as any

    // 判断是否为父级菜单
    if (data[parentIdField] === parentId) {
      const treeNode = {
        ...data,
        ['key']: data.key || data[idField],
        [childrenField]: [] as T[]
      }

      // 额外的数据转换处理
      if (attributeMapping && typeof attributeMapping === 'function') {
        attributeMapping(treeNode)
      }

      // 迭代 list， 找到当前菜单相符合的所有子菜单
      fillTree(list, treeNode[childrenField], data[idField], options)
      // 删掉不存在 children 值的属性
      if (treeNode[childrenField] && (treeNode[childrenField] as T[]).length <= 0) {
        delete treeNode[childrenField]
      }
      // 加入到树中
      tree.push(treeNode)
    }
  })
}
