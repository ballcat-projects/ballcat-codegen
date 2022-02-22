export function copyProperties<T>(target: T, source: any, excludes?: string[]): T {
  Object.keys(target).forEach(key => {
    const result = target as any
    if (!excludes?.includes(key) && source[key]) {
      result[key] = source[key]
    }
  })
  return target
}
