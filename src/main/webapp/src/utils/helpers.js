/**
 * 格式化时间为相对时间
 */
export function formatRelativeTime(time) {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return Math.floor(diff / 60000) + ' 分钟前'
  if (diff < 86400000) return Math.floor(diff / 3600000) + ' 小时前'
  if (diff < 604800000) return Math.floor(diff / 86400000) + ' 天前'
  return date.toLocaleDateString('zh-CN')
}

/**
 * 格式化时间为日期
 */
export function formatDate(time) {
  if (!time) return ''
  return new Date(time).toLocaleDateString('zh-CN')
}

/**
 * 格式化时间为日期时间
 */
export function formatDateTime(time) {
  if (!time) return ''
  return new Date(time).toLocaleString('zh-CN')
}

/**
 * 截断文本
 */
export function truncateText(text, maxLength = 100) {
  if (!text) return ''
  return text.length > maxLength ? text.slice(0, maxLength) + '...' : text
}

/**
 * 数字格式化
 */
export function formatNumber(num) {
  if (num == null) return '0'
  if (num >= 10000) return (num / 10000).toFixed(1) + 'w'
  if (num >= 1000) return (num / 1000).toFixed(1) + 'k'
  return String(num)
}
