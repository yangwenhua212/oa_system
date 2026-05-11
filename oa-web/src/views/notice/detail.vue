<template>
  <div class="notice-detail">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-button @click="goBack" text>
              <el-icon><ArrowLeft /></el-icon>
              返回
            </el-button>
            <el-tag :type="getTypeColor(notice.noticeType)">
              {{ getTypeName(notice.noticeType) }}
            </el-tag>
            <el-tag v-if="notice.isTop === 1" type="danger">置顶</el-tag>
          </div>
          <div class="header-right" v-if="canEdit">
            <el-button type="primary" @click="handleEdit">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button type="danger" @click="handleDelete">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </div>
        </div>
      </template>

      <div class="notice-content">
        <h1 class="notice-title">{{ notice.title }}</h1>
        
        <div class="notice-meta">
          <span class="meta-item">
            <el-icon><User /></el-icon>
            发布人：{{ notice.publisherName || '-' }}
          </span>
          <span class="meta-item">
            <el-icon><Clock /></el-icon>
            发布时间：{{ notice.publishTime ? dayjs(notice.publishTime).format('YYYY-MM-DD HH:mm') : '-' }}
          </span>
          <span class="meta-item">
            <el-icon><View /></el-icon>
            浏览量：{{ notice.viewCount || 0 }}
          </span>
        </div>

        <el-divider />

        <div class="notice-body" v-html="formattedContent"></div>

        <template v-if="notice.attachments?.length">
          <el-divider>附件下载</el-divider>
          <div class="attachments">
            <div
              v-for="(file, index) in notice.attachments"
              :key="index"
              class="attachment-item"
              @click="downloadFile(file)"
            >
              <el-icon><Document /></el-icon>
              <span>{{ file.name }}</span>
              <el-tag size="small">{{ file.size }}</el-tag>
            </div>
          </div>
        </template>

        <div class="notice-actions">
          <el-button-group>
            <el-button @click="handleLike" :type="notice.isLiked ? 'primary' : 'default'">
              <el-icon><Star /></el-icon>
              {{ notice.likeCount || 0 }}
            </el-button>
            <el-button @click="handleCollect" :type="notice.isCollected ? 'warning' : 'default'">
              <el-icon><Collection /></el-icon>
              收藏
            </el-button>
            <el-button @click="handleShare">
              <el-icon><Share /></el-icon>
              分享
            </el-button>
          </el-button-group>
        </div>
      </div>
    </el-card>

    <!-- 评论区域 -->
    <el-card class="box-card comment-card">
      <template #header>
        <span>评论 ({{ comments.length }})</span>
      </template>

      <!-- 发表评论 -->
      <div class="comment-form">
        <el-input
          v-model="commentText"
          type="textarea"
          :rows="3"
          placeholder="请输入评论内容..."
        />
        <div class="comment-form-footer">
          <el-button type="primary" size="small" @click="submitComment" :disabled="!commentText">
           发表评论
          </el-button>
        </div>
      </div>

      <!-- 评论列表 -->
      <div class="comment-list">
        <div
          v-for="comment in comments"
          :key="comment.id"
          class="comment-item"
        >
          <div class="comment-avatar">
            <el-avatar :size="40" :src="comment.avatar" />
          </div>
          <div class="comment-body">
            <div class="comment-header">
              <span class="comment-author">{{ comment.author }}</span>
              <span class="comment-time">{{ comment.time }}</span>
            </div>
            <div class="comment-content">{{ comment.content }}</div>
            <div class="comment-actions">
              <el-button type="primary" link size="small" @click="handleReply(comment)">
                回复
              </el-button>
              <el-button type="danger" link size="small" @click="deleteComment(comment.id)">
                删除
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getNoticeById, deleteNotice, getNoticeComments, addNoticeComment, deleteNoticeComment } from '@/api/notice'
import dayjs from 'dayjs'

const route = useRoute()
const router = useRouter()

const commentText = ref('')
const canEdit = ref(true)

const notice = ref({})
const comments = ref([])
const loading = ref(false)

const formattedContent = computed(() => {
  return (notice.value.content || '').replace(/\n/g, '<br>')
})

const getTypeColor = (type) => {
  const colors = { system: 'primary', activity: 'success', meeting: 'warning', important: 'danger' }
  return colors[type] || 'info'
}

const getTypeName = (type) => {
  const names = { system: '系统通知', activity: '活动公告', meeting: '会议通知', important: '重要提醒' }
  return names[type] || '公告'
}

const goBack = () => {
  router.push('/notice/list')
}

const handleEdit = () => {
  router.push(`/notice/publish?id=${notice.value.id}`)
}

const handleDelete = async () => {
  try {
    await ElMessageBox.confirm('确定要删除这条公告吗？', '提示', { type: 'warning' })
    await deleteNotice(notice.value.id)
    ElMessage.success('删除成功')
    router.push('/notice/list')
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('删除失败')
  }
}

const loadNotice = async () => {
  const id = route.query.id
  if (!id) return
  loading.value = true
  try {
    const res = await getNoticeById(id)
    if (res.code === 200 && res.data) {
      notice.value = res.data
    }
  } catch (error) {
    console.error('加载公告详情失败', error)
  } finally {
    loading.value = false
  }
}

const loadComments = async () => {
  const id = route.query.id
  if (!id) return
  try {
    const res = await getNoticeComments(id)
    if (res.code === 200) {
      comments.value = (res.data || []).map(c => ({
        id: c.id,
        noticeId: c.noticeId,
        author: c.userName || '未知',
        avatar: '',
        time: c.createTime ? dayjs(c.createTime).format('YYYY-MM-DD HH:mm') : '-',
        content: c.content
      }))
    }
  } catch (error) {
    console.error('加载评论失败', error)
  }
}

const downloadFile = (file) => {
  if (!file.fileId) {
    ElMessage.info('文件不存在')
    return
  }
  // 使用 fetch 携带 Token 下载
  const token = localStorage.getItem('token')
  fetch(`/api/file/download/${file.fileId}`, {
    headers: { 'Authorization': `Bearer ${token}` }
  })
  .then(res => {
    if (!res.ok) throw new Error('下载失败')
    return res.blob()
  })
  .then(blob => {
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = file.name
    a.click()
    URL.revokeObjectURL(url)
  })
  .catch(() => ElMessage.error('下载失败'))
}

const submitComment = async () => {
  if (!commentText.value.trim()) return
  try {
    await addNoticeComment({
      noticeId: Number(route.query.id),
      content: commentText.value
    })
    commentText.value = ''
    ElMessage.success('评论成功')
    loadComments()
  } catch (error) {
    ElMessage.error('评论失败')
  }
}

const deleteComment = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这条评论吗？', '提示', { type: 'warning' })
    await deleteNoticeComment(id)
    ElMessage.success('删除成功')
    loadComments()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('删除失败')
  }
}

onMounted(() => {
  loadNotice()
  loadComments()
})
</script>

<style scoped lang="scss">
.notice-detail {
  .box-card {
    border-top: 3px solid #00a65a;
    margin-bottom: 15px;
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .header-left {
      display: flex;
      align-items: center;
      gap: 10px;
    }

    .header-right {
      display: flex;
      gap: 10px;
    }
  }

  .notice-content {
    .notice-title {
      font-size: 24px;
      font-weight: bold;
      color: #333;
      text-align: center;
      margin-bottom: 20px;
    }

    .notice-meta {
      display: flex;
      justify-content: center;
      gap: 30px;
      color: #666;
      font-size: 14px;

      .meta-item {
        display: flex;
        align-items: center;
        gap: 5px;
      }
    }

    .notice-body {
      font-size: 16px;
      line-height: 2;
      color: #333;
      white-space: pre-wrap;
      padding: 20px 0;
    }

    .attachments {
      .attachment-item {
        display: flex;
        align-items: center;
        gap: 10px;
        padding: 10px 15px;
        background: #f5f7fa;
        border-radius: 4px;
        margin-bottom: 10px;
        cursor: pointer;
        transition: all 0.3s;

        &:hover {
          background: #e4e7ed;
        }

        span {
          flex: 1;
        }
      }
    }

    .notice-actions {
      margin-top: 30px;
      text-align: center;
    }
  }

  .comment-card {
    .comment-form {
      margin-bottom: 20px;

      .comment-form-footer {
        margin-top: 10px;
        text-align: right;
      }
    }

    .comment-list {
      .comment-item {
        display: flex;
        padding: 15px 0;
        border-bottom: 1px solid #eee;

        &:last-child {
          border-bottom: none;
        }

        .comment-body {
          flex: 1;
          margin-left: 15px;

          .comment-header {
            display: flex;
            justify-content: space-between;
            margin-bottom: 8px;

            .comment-author {
              font-weight: bold;
              color: #333;
            }

            .comment-time {
              color: #999;
              font-size: 12px;
            }
          }

          .comment-content {
            color: #666;
            line-height: 1.6;
          }

          .comment-actions {
            margin-top: 10px;
          }
        }
      }
    }
  }
}
</style>
