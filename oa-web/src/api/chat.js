import request from '@/utils/request'

// 获取帖子列表
export function getPosts(params) {
  return request({
    url: '/chat/posts',
    method: 'get',
    params
  })
}

// 获取帖子详情
export function getPostById(id) {
  return request({
    url: `/chat/posts/${id}`,
    method: 'get'
  })
}

// 创建帖子
export function createPost(data) {
  return request({
    url: '/chat/posts',
    method: 'post',
    data
  })
}

// 更新帖子
export function updatePost(id, data) {
  return request({
    url: `/chat/posts/${id}`,
    method: 'put',
    data
  })
}

// 删除帖子
export function deletePost(id) {
  return request({
    url: `/chat/posts/${id}`,
    method: 'delete'
  })
}

// 获取回帖列表
export function getReplies(postId, params) {
  return request({
    url: `/chat/posts/${postId}/replies`,
    method: 'get',
    params
  })
}

// 发表评论/回复
export function createReply(postId, data) {
  return request({
    url: `/chat/posts/${postId}/replies`,
    method: 'post',
    data
  })
}

// 删除回复
export function deleteReply(id) {
  return request({
    url: `/chat/replies/${id}`,
    method: 'delete'
  })
}

// 点赞
export function likePost(postId) {
  return request({
    url: `/chat/posts/${postId}/like`,
    method: 'post'
  })
}

// 取消点赞
export function unlikePost(postId) {
  return request({
    url: `/chat/posts/${postId}/unlike`,
    method: 'post'
  })
}

// 点赞回复
export function likeReply(replyId) {
  return request({
    url: `/chat/replies/${replyId}/like`,
    method: 'post'
  })
}

// 获取投票选项
export function getVoteOptions(postId) {
  return request({
    url: `/chat/posts/${postId}/vote`,
    method: 'get'
  })
}

// 投票
export function vote(postId, optionId) {
  return request({
    url: `/chat/posts/${postId}/vote`,
    method: 'post',
    data: { optionId }
  })
}
