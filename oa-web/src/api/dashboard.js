import request from '@/utils/request'

// 获取工作台数据
export function getDashboardData() {
  return request({
    url: '/dashboard/data',
    method: 'get'
  })
}

// 获取某周的系统使用量统计
export function getWeeklyUsage(weekStart) {
  return request({
    url: '/dashboard/weekly-usage',
    method: 'get',
    params: weekStart ? { weekStart } : {}
  })
}
