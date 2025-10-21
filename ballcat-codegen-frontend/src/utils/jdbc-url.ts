// Reusable JDBC URL parser and helpers

export type JdbcVendor = 'MySQL' | 'PostgreSQL' | 'SQLServer' | 'Oracle' | 'SQLite' | 'H2' | 'Other'

export interface JdbcHost { host: string; port?: number }

export interface JdbcParsed {
  vendor: JdbcVendor
  hosts: JdbcHost[]
  database?: string
  params: Record<string, string>
  raw: string
}

export function getDefaultPort(vendor: JdbcVendor): number | undefined {
  switch (vendor) {
    case 'MySQL': return 3306
    case 'PostgreSQL': return 5432
    case 'SQLServer': return 1433
    case 'Oracle': return 1521
    default: return undefined
  }
}

function parseKv(str: string, sep: RegExp = /[&;]/): Record<string, string> {
  const out: Record<string, string> = {}
  if (!str) return out
  str.split(sep).forEach(pair => {
    if (!pair) return
    const i = pair.indexOf('=')
    if (i === -1) {
      const k = pair.trim()
      if (k) out[k] = ''
      return
    }
    const k = pair.slice(0, i).trim()
    const v = pair.slice(i + 1).trim()
    if (k) out[k] = v
  })
  return out
}

function parseHostPort(token: string): JdbcHost {
  // IPv6: [fe80::1]:5432
  const ipv6 = token.match(/^\[([^\]]+)\](?::(\d+))?$/)
  if (ipv6) {
    return { host: `[${ipv6[1]}]`, port: ipv6[2] ? Number(ipv6[2]) : undefined }
  }
  const idx = token.lastIndexOf(':')
  if (idx > -1 && token.indexOf(':') === idx) {
    const host = token.slice(0, idx)
    const port = Number(token.slice(idx + 1))
    return isNaN(port) ? { host: token } : { host, port }
  }
  return { host: token }
}

export function parseJdbcUrl(url?: string): JdbcParsed {
  const raw = url ?? ''
  if (!raw) return { vendor: 'Other', hosts: [], database: undefined, params: {}, raw }

  // MySQL / PostgreSQL
  let m = raw.match(/^jdbc:(mysql|postgresql):\/\/([^\/]+)\/([^?;]+)(?:[?;](.*))?$/i)
  if (m) {
    const vendor: JdbcVendor = m[1].toLowerCase() === 'mysql' ? 'MySQL' : 'PostgreSQL'
    const hosts = m[2].split(',').map(h => parseHostPort(h.trim())).filter(Boolean)
    const database = decodeURIComponent(m[3])
    const params = parseKv(m[4] || '')
    const def = getDefaultPort(vendor)
    hosts.forEach(h => { if (!h.port && def) h.port = def })
    return { vendor, hosts, database, params, raw }
  }

  // SQLServer
  m = raw.match(/^jdbc:sqlserver:\/\/([^;]+)(?:;(.*))?$/i)
  if (m) {
    const host = parseHostPort(m[1].trim())
    const params = parseKv(m[2] || '', /[;]+/)
    const database = params.databaseName || params['database'] || undefined
    if (!host.port) host.port = getDefaultPort('SQLServer')
    return { vendor: 'SQLServer', hosts: [host], database, params, raw }
  }

  // Oracle Thin (SID)
  m = raw.match(/^jdbc:oracle:thin:@([^:\/]+)(?::(\d+))?:(.+)$/i)
  if (m) {
    const host = { host: m[1], port: m[2] ? Number(m[2]) : getDefaultPort('Oracle') }
    const database = m[3]
    return { vendor: 'Oracle', hosts: [host], database, params: {}, raw }
  }
  // Oracle Thin (Service)
  m = raw.match(/^jdbc:oracle:thin:@\/\/([^\/:]+)(?::(\d+))?\/(.+)$/i)
  if (m) {
    const host = { host: m[1], port: m[2] ? Number(m[2]) : getDefaultPort('Oracle') }
    const database = m[3]
    return { vendor: 'Oracle', hosts: [host], database, params: {}, raw }
  }

  // SQLite
  m = raw.match(/^jdbc:sqlite:(.+)$/i)
  if (m) {
    return { vendor: 'SQLite', hosts: [], database: m[1], params: {}, raw }
  }

  // H2
  m = raw.match(/^jdbc:h2:(mem|file):(.+)$/i)
  if (m) {
    return { vendor: 'H2', hosts: [], database: `${m[1]}:${m[2]}`, params: {}, raw }
  }

  return { vendor: 'Other', hosts: [], database: undefined, params: {}, raw }
}

export function getJdbcSummary(url?: string): string {
  const p = parseJdbcUrl(url)
  const v = p.vendor
  if (v === 'SQLite' || v === 'H2') {
    return p.database ? `${v} · ${p.database}` : v
  }
  if (p.hosts.length) {
    const first = p.hosts[0]
    const hostPort = `${first.host}${first.port ? `:${first.port}` : ''}`
    const multi = p.hosts.length > 1 ? ' 等' : ''
    const db = p.database ? ` · ${p.database}` : ''
    return `${v} · ${hostPort}${multi}${db}`
  }
  return p.raw || ''
}

export function getJdbcTooltip(url?: string): string {
  const p = parseJdbcUrl(url)
  const parts: string[] = []
  parts.push(`类型：${p.vendor}`)
  if (p.hosts.length) {
    parts.push(`主机：${p.hosts.map(h => `${h.host}${h.port ? `:${h.port}` : ''}`).join(', ')}`)
  }
  if (p.database) parts.push(`库名：${p.database}`)
  const paramKeys = Object.keys(p.params || {})
  if (paramKeys.length) parts.push(`参数：${paramKeys.length} 项`)
  parts.push(`完整：${p.raw}`)
  return parts.join('\n')
}

export function hasSsl(url?: string): boolean {
  const p = parseJdbcUrl(url)
  const lower: Record<string, string> = {}
  Object.keys(p.params).forEach(k => {
    lower[k.toLowerCase()] = (p.params[k] ?? '').toString().toLowerCase()
  })
  if ('usessl' in lower) return lower['usessl'] === 'true'
  if ('ssl' in lower) return lower['ssl'] === 'true'
  if ('sslmode' in lower) return !!lower['sslmode'] && lower['sslmode'] !== 'disable'
  if ('encrypt' in lower) return lower['encrypt'] === 'true'
  return false
}
