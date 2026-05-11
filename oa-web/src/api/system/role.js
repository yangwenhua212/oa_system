import request from '@/utils/request'

// 分页查询角色列表
export function getRoleList(params) {
  return request({
    url: '/system/role/list',
    method: 'get',
    params
  })
}

// 获取所有角色（下拉选择）
export function getAllRoles() {
  return request({
    url: '/system/role/all',
    method: 'get'
  })
}

// 获取角色详情
export function getRoleById(id) {
  return request({
    url: `/system/role/${id}`,
    method: 'get'
  })
}

// 新增角色
export function addRole(data) {
  return request({
    url: '/system/role',
    method: 'post',
    data
  })
}

// 修改角色
export function updateRole(data) {
  return request({
    url: '/system/role',
    method: 'put',
    data
  })
}

// 删除角色
export function deleteRole(id) {
  return request({
    url: `/system/role/${id}`,
    method: 'delete'
  })
}
