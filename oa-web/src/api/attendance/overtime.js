import request from '@/utils/request'

// 提交加班申请
export function submitOvertime(data) {
  return request({
    url: '/overtime',
    method: 'post',
    data
  })
}

// 更新加班申请
export function updateOvertime(data) {
  return request({
    url: '/overtime',
    method: 'put',
    data
  })
}

// 取消加班申请
export function cancelOvertime(id) {
  return request({
    url: `/overtime/cancel/${id}`,
    method: 'put'
  })
}

// 获取加班详情
export function getOvertimeById(id) {
  return request({
    url: `/overtime/${id}`,
    method: 'get'
  })
}

// 分页查询加班列表
export function getOvertimeList(params) {
  return request({
    url: '/overtime/list',
    method: 'get',
    params
  })
}

// 获取我的加班列表
export function getMyOvertimeList() {
  return request({
    url: '/overtime/my',
    method: 'get'
  })
}
