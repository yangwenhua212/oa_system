import request from '@/utils/request'

// ============ 职位管理 ============
export function getPositions(params) {
  return request({
    url: '/system/position/list',
    method: 'get',
    params
  })
}

export function getPositionById(id) {
  return request({
    url: `/system/position/${id}`,
    method: 'get'
  })
}

export function createPosition(data) {
  return request({
    url: '/system/position',
    method: 'post',
    data
  })
}

export function updatePosition(data) {
  return request({
    url: '/system/position',
    method: 'put',
    data
  })
}

export function deletePosition(id) {
  return request({
    url: `/system/position/${id}`,
    method: 'delete'
  })
}

// ============ 类型管理 ============
export function getTypes(params) {
  return request({
    url: '/system/type/list',
    method: 'get',
    params
  })
}

export function createType(data) {
  return request({
    url: '/system/type',
    method: 'post',
    data
  })
}

export function updateType(id, data) {
  return request({
    url: `/system/type/${id}`,
    method: 'put',
    data
  })
}

export function deleteType(id) {
  return request({
    url: `/system/type/${id}`,
    method: 'delete'
  })
}

// ============ 状态管理 ============
export function getStatuses(params) {
  return request({
    url: '/system/status/list',
    method: 'get',
    params
  })
}

export function createStatus(data) {
  return request({
    url: '/system/status',
    method: 'post',
    data
  })
}

export function updateStatus(id, data) {
  return request({
    url: `/system/status/${id}`,
    method: 'put',
    data
  })
}

export function deleteStatus(id) {
  return request({
    url: `/system/status/${id}`,
    method: 'delete'
  })
}

// ============ 用户日志 ============
export function getUserLogs(params) {
  return request({
    url: '/system/log/list',
    method: 'get',
    params
  })
}

export function getOnlineUsers() {
  return request({
    url: '/system/user/online',
    method: 'get'
  })
}
