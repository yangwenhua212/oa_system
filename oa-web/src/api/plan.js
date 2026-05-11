import request from '@/utils/request'

// 获取计划列表
export function getPlans(params) {
  return request({
    url: '/plan/list',
    method: 'get',
    params
  })
}

// 获取计划详情
export function getPlanById(id) {
  return request({
    url: `/plan/${id}`,
    method: 'get'
  })
}

// 创建计划
export function createPlan(data) {
  return request({
    url: '/plan',
    method: 'post',
    data
  })
}

// 更新计划
export function updatePlan(id, data) {
  return request({
    url: `/plan/${id}`,
    method: 'put',
    data
  })
}

// 删除计划
export function deletePlan(id) {
  return request({
    url: `/plan/${id}`,
    method: 'delete'
  })
}

// 获取计划类型列表
export function getPlanTypes() {
  return request({
    url: '/plan/types',
    method: 'get'
  })
}

// 获取计划状态列表
export function getPlanStatuses() {
  return request({
    url: '/plan/statuses',
    method: 'get'
  })
}
