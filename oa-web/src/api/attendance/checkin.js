import request from '@/utils/request'

// 打卡签到
export function clockIn() {
  return request({
    url: '/attendance/clockIn',
    method: 'post'
  })
}

// 打卡签退
export function clockOut() {
  return request({
    url: '/attendance/clockOut',
    method: 'post'
  })
}

// 获取今日考勤状态
export function getTodayAttendance() {
  return request({
    url: '/attendance/today',
    method: 'get'
  })
}

// 分页查询考勤记录
export function getAttendanceList(params) {
  return request({
    url: '/attendance/list',
    method: 'get',
    params
  })
}

// 获取考勤统计
export function getAttendanceStatistics(params) {
  return request({
    url: '/attendance/statistics',
    method: 'get',
    params
  })
}

// 获取考勤日历数据
export function getAttendanceCalendar(params) {
  return request({
    url: '/attendance/calendar',
    method: 'get',
    params
  })
}
