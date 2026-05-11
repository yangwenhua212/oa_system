import request from '@/utils/request'

// 获取内部通讯录列表
export function getInternalContacts(params) {
  return request({
    url: '/address/internal',
    method: 'get',
    params
  })
}

// 获取外部通讯录列表
export function getExternalContacts(params) {
  return request({
    url: '/address/external',
    method: 'get',
    params
  })
}

// 获取外部联系人分类
export function getContactCategories() {
  return request({
    url: '/address/categories',
    method: 'get'
  })
}

// 创建外部联系人分类
export function createCategory(data) {
  return request({
    url: '/address/categories',
    method: 'post',
    data
  })
}

// 更新外部联系人分类
export function updateCategory(id, data) {
  return request({
    url: `/address/categories/${id}`,
    method: 'put',
    data
  })
}

// 删除外部联系人分类
export function deleteCategory(id) {
  return request({
    url: `/address/categories/${id}`,
    method: 'delete'
  })
}

// 获取联系人详情
export function getContactById(id) {
  return request({
    url: `/address/contacts/${id}`,
    method: 'get'
  })
}

// 创建/更新外部联系人
export function saveContact(data) {
  return request({
    url: '/address/contacts',
    method: 'post',
    data
  })
}

// 删除外部联系人
export function deleteContact(id) {
  return request({
    url: `/address/contacts/${id}`,
    method: 'delete'
  })
}

// 移动联系人到其他分类
export function moveContact(data) {
  return request({
    url: '/address/contacts/move',
    method: 'put',
    data
  })
}

// 获取我分享的联系人
export function getMySharedContacts() {
  return request({
    url: '/address/shared/by-me',
    method: 'get'
  })
}

// 获取分享给我的联系人
export function getSharedWithMeContacts() {
  return request({
    url: '/address/shared/with-me',
    method: 'get'
  })
}

// 分享联系人给其他用户
export function shareContacts(data) {
  return request({
    url: '/address/shared',
    method: 'post',
    data
  })
}

// 取消分享
export function cancelShare(shareId) {
  return request({
    url: `/address/shared/${shareId}`,
    method: 'delete'
  })
}
