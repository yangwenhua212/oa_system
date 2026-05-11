import request from '@/utils/request'

// 获取笔记列表
export function getNotes(params) {
  return request({
    url: '/note/list',
    method: 'get',
    params
  })
}

// 获取笔记详情
export function getNoteById(id) {
  return request({
    url: `/note/${id}`,
    method: 'get'
  })
}

// 创建笔记
export function createNote(data) {
  return request({
    url: '/note',
    method: 'post',
    data
  })
}

// 更新笔记
export function updateNote(id, data) {
  return request({
    url: `/note/${id}`,
    method: 'put',
    data
  })
}

// 删除笔记
export function deleteNote(id) {
  return request({
    url: `/note/${id}`,
    method: 'delete'
  })
}

// 获取笔记分类
export function getNoteCategories() {
  return request({
    url: '/note/catalog',
    method: 'get'
  })
}

// 创建笔记分类
export function createNoteCategory(data) {
  return request({
    url: '/note/catalog',
    method: 'post',
    data
  })
}

// 删除笔记分类
export function deleteNoteCategory(id) {
  return request({
    url: `/note/catalog/${id}`,
    method: 'delete'
  })
}
