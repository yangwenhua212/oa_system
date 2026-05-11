import request from '@/utils/request'

// 获取收件箱
export function getInbox(params) {
  return request({
    url: '/mail/inbox',
    method: 'get',
    params
  })
}

// 获取发件箱
export function getSent(params) {
  return request({
    url: '/mail/sent',
    method: 'get',
    params
  })
}

// 获取草稿箱
export function getDrafts(params) {
  return request({
    url: '/mail/draft',
    method: 'get',
    params
  })
}

// 发送邮件
export function sendMail(data) {
  return request({
    url: '/mail',
    method: 'post',
    data
  })
}

// 保存草稿
export function saveDraft(data) {
  return request({
    url: '/mail/draft',
    method: 'post',
    data
  })
}

// 获取邮件详情
export function getMailById(id) {
  return request({
    url: `/mail/${id}`,
    method: 'get'
  })
}

// 删除邮件
export function deleteMail(id) {
  return request({
    url: `/mail/${id}`,
    method: 'delete'
  })
}

// 标记已读
export function markAsRead(id) {
  return request({
    url: `/mail/read/${id}`,
    method: 'put'
  })
}

// 获取未读数量
export function getUnreadCount() {
  return request({
    url: '/mail/unread',
    method: 'get'
  })
}
