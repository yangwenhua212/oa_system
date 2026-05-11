import request from '@/utils/request'

export function login(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

export function getCurrentUser() {
  return request({
    url: '/auth/current',
    method: 'get'
  })
}

export function getUserMenus() {
  return request({
    url: '/auth/menus',
    method: 'get'
  })
}

export function logout() {
  return request({
    url: '/auth/logout',
    method: 'post'
  })
}
