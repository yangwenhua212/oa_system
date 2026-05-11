import request from '@/utils/request'

// 分页查询任务列表
export function getTaskList(params) {
  return request({
    url: '/task/list',
    method: 'get',
    params
  })
}

// 获取我的任务
export function getMyTasks() {
  return request({
    url: '/task/my',
    method: 'get'
  })
}

// 获取任务详情
export function getTaskById(id) {
  return request({
    url: `/task/${id}`,
    method: 'get'
  })
}

// 创建任务
export function createTask(data) {
  return request({
    url: '/task',
    method: 'post',
    data
  })
}

// 更新任务
export function updateTask(data) {
  return request({
    url: '/task',
    method: 'put',
    data
  })
}

// 删除任务
export function deleteTask(id) {
  return request({
    url: `/task/${id}`,
    method: 'delete'
  })
}

// 更新任务状态
export function updateTaskStatus(id, data) {
  return request({
    url: `/task/status/${id}`,
    method: 'put',
    data
  })
}

// 添加任务评论
export function addTaskComment(data) {
  return request({
    url: '/task/comment',
    method: 'post',
    data
  })
}

// 获取任务完成排行
export function getTaskCompletionRank() {
  return request({
    url: '/task/completion-rank',
    method: 'get'
  })
}
