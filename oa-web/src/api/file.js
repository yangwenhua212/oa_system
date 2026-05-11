import axios from 'axios'
import { useUserStore } from '@/store/user'
import request from '@/utils/request'

// 获取文件列表
export function getFiles(params) {
  return request({
    url: '/file/list',
    method: 'get',
    params
  })
}

// 获取面包屑导航
export function getBreadcrumb(pathId) {
  return request({
    url: '/file/breadcrumb',
    method: 'get',
    params: { pathId }
  })
}

// 获取文件详情
export function getFileById(id) {
  return request({
    url: `/file/${id}`,
    method: 'get'
  })
}

// 上传文件
export function uploadFile(formData) {
  return request({
    url: '/file/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 删除文件（移到回收站）
export function deleteFile(id) {
  return request({
    url: `/file/${id}`,
    method: 'delete'
  })
}

// 下载文件（跳过响应拦截器，直接返回 blob）
export function downloadFile(id) {
  const userStore = useUserStore()
  return axios({
    baseURL: '/api',
    url: `/file/download/${id}`,
    method: 'get',
    responseType: 'blob',
    headers: {
      Authorization: `Bearer ${userStore.token}`
    }
  })
}

// 重命名文件/文件夹
export function renameFile(data) {
  return request({
    url: '/file/rename',
    method: 'put',
    data
  })
}

// 移动文件
export function moveFile(data) {
  return request({
    url: '/file/move',
    method: 'put',
    data
  })
}

// 创建文件夹
export function createFolder(data) {
  return request({
    url: '/file/folder',
    method: 'post',
    data
  })
}

// 获取回收站文件
export function getTrashFiles() {
  return request({
    url: '/file/trash',
    method: 'get'
  })
}

// 恢复文件
export function restoreFile(id) {
  return request({
    url: `/file/restore/${id}`,
    method: 'put'
  })
}

// 获取文件统计
export function getFileStats() {
  return request({
    url: '/file/stats',
    method: 'get'
  })
}
