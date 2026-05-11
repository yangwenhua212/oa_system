import request from '@/utils/request'

// 提交审批
export function submitProcess(data) {
  return request({
    url: '/process',
    method: 'post',
    data
  })
}

// 审批操作
export function approveProcess(id, data) {
  return request({
    url: `/process/approve/${id}`,
    method: 'put',
    data
  })
}

// 获取我的申请列表
export function getMyProcessList() {
  return request({
    url: '/process/my',
    method: 'get'
  })
}

// 获取待我审批列表
export function getPendingProcessList() {
  return request({
    url: '/process/pending',
    method: 'get'
  })
}

// 获取流程详情
export function getProcessById(id) {
  return request({
    url: `/process/${id}`,
    method: 'get'
  })
}

// 获取审批记录
export function getApprovalRecords(id) {
  return request({
    url: `/process/${id}/records`,
    method: 'get'
  })
}
