export function copyProperties<T>(target: T, source: any, excludes?: string[]): T {
  if (!source) return target
  Object.keys(target as any).forEach(key => {
    const result = target as any
    if (!excludes?.includes(key)) {
      result[key] = source[key]
    }
  })
  return target
}
