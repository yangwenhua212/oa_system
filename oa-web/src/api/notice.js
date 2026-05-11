import request from '@/utils/request'

// 分页查询公告列表
export function getNoticeList(params) {
  return request({
    url: '/notice/list',
    method: 'get',
    params
  })
}

// 获取公告详情
export function getNoticeById(id) {
  return request({
    url: `/notice/${id}`,
    method: 'get'
  })
}

// 发布公告
export function publishNotice(data) {
  return request({
    url: '/notice',
    method: 'post',
    data
  })
}

// 更新公告
export function updateNotice(data) {
  return request({
    url: '/notice',
    method: 'put',
    data
  })
}

// 删除公告
export function deleteNotice(id) {
  return request({
    url: `/notice/${id}`,
    method: 'delete'
  })
}

// 获取我的公告列表
export function getMyNotices() {
  return request({
    url: '/notice/my',
    method: 'get'
  })
}

// 获取未读数量
export function getUnreadCount() {
  return request({
    url: '/notice/unread',
    method: 'get'
  })
}

// 标记已读
export function markNoticeRead(id) {
  return request({
    url: `/notice/read/${id}`,
    method: 'put'
  })
}

// ====== 评论 ======

// 获取公告评论
export function getNoticeComments(noticeId) {
  return request({
    url: `/notice/comment/${noticeId}`,
    method: 'get'
  })
}

// 添加评论
export function addNoticeComment(data) {
  return request({
    url: '/notice/comment',
    method: 'post',
    data
  })
}

// 删除评论
export function deleteNoticeComment(id) {
  return request({
    url: `/notice/comment/${id}`,
    method: 'delete'
  })
}
