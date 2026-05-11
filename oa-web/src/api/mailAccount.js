import request from '@/utils/request'

export function getAccountList(params) {
  return request({
    url: '/mail/account/list',
    method: 'get',
    params
  })
}

export function getAccountById(id) {
  return request({
    url: `/mail/account/${id}`,
    method: 'get'
  })
}

export function createAccount(data) {
  return request({
    url: '/mail/account',
    method: 'post',
    data
  })
}

export function updateAccount(data) {
  return request({
    url: '/mail/account',
    method: 'put',
    data
  })
}

export function deleteAccount(id) {
  return request({
    url: `/mail/account/${id}`,
    method: 'delete'
  })
}
