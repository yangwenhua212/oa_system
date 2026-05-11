<template>
  <div class="chat-page">
    <el-row :gutter="20">
      <!-- 左侧边栏 -->
      <el-col :span="5">
        <el-card class="sidebar-card">
          <template #header>
            <div class="card-header">
              <span>讨论区</span>
              <el-button type="primary" size="small" @click="handleNewPost">
                <el-icon><Plus /></el-icon>
              </el-button>
            </div>
          </template>
          
          <div class="filter-section">
            <h4>筛选</h4>
            <el-menu :default-active="filterType" @select="handleFilterChange" class="filter-menu">
              <el-menu-item index="all">
                <el-icon><List /></el-icon>
                <span>全部帖子</span>
              </el-menu-item>
              <el-menu-item index="my">
                <el-icon><User /></el-icon>
                <span>我的帖子</span>
              </el-menu-item>
              <el-menu-item index="participated">
                <el-icon><ChatDotRound /></el-icon>
                <span>我参与的</span>
              </el-menu-item>
            </el-menu>
          </div>
        </el-card>
      </el-col>

      <!-- 帖子列表 -->
      <el-col :span="19">
        <el-card class="post-list-card">
          <template #header>
            <div class="card-header">
              <span>帖子列表</span>
              <el-input 
                v-model="searchKeyword" 
                placeholder="搜索帖子..." 
                size="small" 
                style="width: 200px"
                clearable
              >
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
              </el-input>
            </div>
          </template>

          <div class="post-list">
            <div 
              v-for="post in filteredPosts" 
              :key="post.id"
              class="post-item"
              @click="handleViewPost(post)"
            >
              <div class="post-header">
                <el-avatar :size="40" :src="post.avatar">{{ post.author?.charAt(0) }}</el-avatar>
                <div class="post-info">
                  <div class="post-author">{{ post.author }}</div>
                  <div class="post-time">{{ post.createTime }}</div>
                </div>
                <el-tag size="small" type="primary" v-if="post.hasVote">投票</el-tag>
              </div>
              <div class="post-title">{{ post.title }}</div>
              <div class="post-content">{{ post.content?.substring(0, 150) }}...</div>
              <div class="post-footer">
                <span class="post-stat">
                  <el-icon><View /></el-icon>
                  {{ post.viewCount }} 浏览
                </span>
                <span class="post-stat">
                  <el-icon><ChatLineSquare /></el-icon>
                  {{ post.replyCount }} 回复
                </span>
                <span class="post-stat" :class="{ liked: post.isLiked }" @click.stop="handleLike(post)">
                  <el-icon><Star /></el-icon>
                  {{ post.likeCount }} 点赞
                </span>
              </div>
            </div>
            <el-empty v-if="filteredPosts.length === 0" description="暂无帖子" />
          </div>

          <!-- 分页 -->
          <div class="pagination">
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :total="total"
              :page-sizes="[10, 20, 50]"
              layout="total, sizes, prev, pager, next"
              @size-change="handleSizeChange"
              @current-change="handlePageChange"
            />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 发帖对话框 -->
    <el-dialog v-model="postDialogVisible" :title="isEdit ? '编辑帖子' : '发布新帖'" width="700px">
      <el-form :model="postForm" :rules="postRules" ref="postFormRef" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="postForm.title" placeholder="请输入帖子标题" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input 
            v-model="postForm.content" 
            type="textarea" 
            :rows="6" 
            placeholder="请输入帖子内容..." 
            maxlength="5000"
          />
        </el-form-item>
        <el-form-item label="附件">
          <el-upload
            :auto-upload="false"
            :show-file-list="true"
            :limit="1"
            :on-change="handleFileChange"
            :on-remove="handleFileRemove"
            accept="*"
          >
            <el-button size="small" type="primary">
              <el-icon><Upload /></el-icon>选择文件
            </el-button>
          </el-upload>
          <span v-if="postForm.fileName" class="file-name">当前文件：{{ postForm.fileName }}</span>
        </el-form-item>
        <el-form-item label="开启投票">
          <el-switch v-model="postForm.hasVote" @change="handleVoteToggle" />
        </el-form-item>
        <template v-if="postForm.hasVote">
          <el-form-item label="投票选项">
            <div class="vote-options">
              <div v-for="(option, index) in postForm.voteOptions" :key="index" class="vote-option">
                <el-input v-model="option.text" placeholder="选项内容" />
                <el-icon @click="removeVoteOption(index)" v-if="postForm.voteOptions.length > 2"><Delete /></el-icon>
              </div>
              <el-button type="primary" link @click="addVoteOption">添加选项</el-button>
            </div>
          </el-form-item>
        </template>
      </el-form>
      <template #footer>
        <el-button @click="postDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitPost" :loading="submitLoading">发布</el-button>
      </template>
    </el-dialog>

    <!-- 帖子详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="帖子详情" width="800px">
      <div class="post-detail" v-if="currentPost">
        <div class="detail-header">
          <h2>{{ currentPost.title }}</h2>
          <div class="detail-meta">
            <el-avatar :size="40">{{ currentPost.author?.charAt(0) }}</el-avatar>
            <span>{{ currentPost.author }}</span>
            <span>{{ currentPost.createTime }}</span>
          </div>
        </div>
        
        <div class="detail-content" v-html="currentPost.content"></div>

        <!-- 投票区域 -->
        <div class="vote-section" v-if="currentPost.hasVote && currentPost.voteOptions">
          <h4>投票</h4>
          <div class="vote-options">
            <div 
              v-for="option in currentPost.voteOptions" 
              :key="option.id"
              class="vote-option-item"
              :class="{ voted: option.isVoted, selected: option.isSelected }"
              @click="handleVote(option)"
            >
              <el-radio v-model="selectedVote" :label="option.id" :disabled="option.hasVoted || !canVote">
                {{ option.text }}
              </el-radio>
              <div class="vote-bar" v-if="option.voteCount > 0">
                <div class="vote-progress" :style="{ width: option.percentage + '%' }"></div>
              </div>
              <span class="vote-count">{{ option.voteCount }} 票 ({{ option.percentage }}%)</span>
            </div>
          </div>
        </div>

        <div class="detail-actions">
          <el-button :type="currentPost.isLiked ? 'primary' : 'default'" @click="handleLike(currentPost)">
            <el-icon><Star /></el-icon>
            {{ currentPost.likeCount }} 点赞
          </el-button>
          <el-button v-if="currentPost.attachmentId" type="success" @click="handleDownload(currentPost)">
            <el-icon><Paperclip /></el-icon>下载附件
          </el-button>
        </div>

        <!-- 回复列表 -->
        <div class="reply-section">
          <h4>回复 ({{ currentPost.replyCount }})</h4>
          <div class="reply-list">
            <div v-for="reply in replies" :key="reply.id" class="reply-item">
              <el-avatar :size="36">{{ reply.author?.charAt(0) }}</el-avatar>
              <div class="reply-content">
                <div class="reply-header">
                  <span class="reply-author">{{ reply.author }}</span>
                  <span class="reply-time">{{ reply.createTime }}</span>
                </div>
                <div class="reply-text" v-html="reply.content"></div>
                <div class="reply-actions">
                  <span class="reply-action" @click="handleLikeReply(reply)">
                    <el-icon><Star /></el-icon>
                    {{ reply.likeCount }}
                  </span>
                  <span class="reply-action" @click="handleReplyReply(reply)">
                    <el-icon><ChatLineSquare /></el-icon>
                    回复
                  </span>
                </div>
              </div>
            </div>
          </div>

          <!-- 回复输入 -->
          <div class="reply-input">
            <el-input
              v-model="replyContent"
              type="textarea"
              :rows="3"
              placeholder="写下你的回复..."
            />
            <el-button type="primary" @click="handleSubmitReply">提交回复</el-button>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/user'
import {
  getPosts,
  getPostById,
  createPost,
  updatePost,
  deletePost,
  getReplies,
  createReply,
  likePost,
  unlikePost,
  likeReply,
  vote
} from '@/api/chat'
import { uploadFile, downloadFile, getFileById } from '@/api/file'

const userStore = useUserStore()

// 状态变量
const loading = ref(false)
const submitLoading = ref(false)
const posts = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchKeyword = ref('')
const filterType = ref('all')
const isEdit = ref(false)
const canVote = ref(true)
const selectedVote = ref(null)

// 对话框状态
const postDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const currentPost = ref(null)
const replies = ref([])
const replyContent = ref('')
const postFormRef = ref(null)

// 帖子表单
const postForm = reactive({
  id: null,
  title: '',
  content: '',
  hasVote: false,
  voteOptions: [
    { text: '' },
    { text: '' }
  ],
  file: null,
  fileName: '',
  attachmentId: null
})

// 表单验证
const postRules = {
  title: [{ required: true, message: '请输入帖子标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入帖子内容', trigger: 'blur' }]
}

// 过滤后的帖子
const filteredPosts = computed(() => {
  let result = posts.value
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(p => 
      p.title?.toLowerCase().includes(keyword) ||
      p.content?.toLowerCase().includes(keyword)
    )
  }
  return result
})

// 规范化后端数据（discussId → id, discussUserName → author, visitNum → viewCount）
function normalizePost(item) {
  if (!item) return item
  return {
    ...item,
    id: item.id || item.discussId,
    discussId: item.discussId || item.id,
    author: item.author || item.discussUserName || '未知',
    viewCount: item.viewCount || item.visitNum || 0,
    replyCount: item.replyCount || 0,
    likeCount: item.likeCount || 0,
    isLiked: item.isLiked || false,
    hasVote: item.hasVote || false
  }
}

// 加载帖子列表
async function loadPosts() {
  loading.value = true
  try {
    const res = await getPosts()
    let list = res.data || []
    // 规范化数据字段
    list = list.map(normalizePost)

    // 客户端筛选
    if (filterType.value === 'my') {
      const raw = userStore.userInfo || {}
      const info = raw.userInfo || raw
      const currentUserId = info.userId || info.id
      const currentUserName = info.realName || info.username
      if (currentUserId) {
        list = list.filter(p => p.discussUserId === currentUserId)
      } else if (currentUserName) {
        list = list.filter(p => (p.discussUserName || p.author) === currentUserName)
      }
    }
    // 'participated' 筛选需要后端支持，暂不过滤显示全部

    posts.value = list
    total.value = list.length
  } catch (error) {
    console.error('加载帖子失败:', error)
    posts.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 筛选变化
function handleFilterChange(index) {
  filterType.value = index
  currentPage.value = 1
  loadPosts()
}

// 附件
function handleFileChange(file) {
  postForm.file = file.raw
  postForm.fileName = file.name
}

function handleFileRemove() {
  postForm.file = null
  postForm.fileName = ''
  postForm.attachmentId = null
}

// 新建帖子
function handleNewPost() {
  isEdit.value = false
  Object.assign(postForm, {
    id: null,
    title: '',
    content: '',
    hasVote: false,
    voteOptions: [{ text: '' }, { text: '' }],
    file: null,
    fileName: '',
    attachmentId: null
  })
  postDialogVisible.value = true
}

// 投票开关变化
function handleVoteToggle(enabled) {
  if (enabled) {
    postForm.voteOptions = [{ text: '' }, { text: '' }]
  }
}

// 添加投票选项
function addVoteOption() {
  postForm.voteOptions.push({ text: '' })
}

// 删除投票选项
function removeVoteOption(index) {
  if (postForm.voteOptions.length > 2) {
    postForm.voteOptions.splice(index, 1)
  }
}

// 提交帖子
async function handleSubmitPost() {
  try {
    await postFormRef.value.validate()
    if (postForm.hasVote && postForm.voteOptions.some(o => !o.text.trim())) {
      ElMessage.warning('请填写所有投票选项')
      return
    }
    submitLoading.value = true

    let attachmentId = postForm.attachmentId
    if (postForm.file) {
      const fd = new FormData()
      fd.append('file', postForm.file)
      fd.append('model', 'discuss')
      const uploadRes = await uploadFile(fd)
      if (uploadRes.code === 200 && uploadRes.data?.fileId) {
        attachmentId = uploadRes.data.fileId
      }
    }

    const postData = {
      title: postForm.title,
      content: postForm.content,
      hasVote: postForm.hasVote,
      voteOptions: postForm.voteOptions
    }
    if (attachmentId) postData.attachmentId = attachmentId

    const formId = postForm.id || postForm.discussId
    if (formId) {
      await updatePost(formId, postData)
    } else {
      await createPost(postData)
    }
    ElMessage.success(formId ? '更新成功' : '发布成功')
    postDialogVisible.value = false
    loadPosts()
  } catch (error) {
    if (error !== false) {
      ElMessage.error('操作失败')
    }
  } finally {
    submitLoading.value = false
  }
}

// 查看帖子
async function handleViewPost(post) {
  currentPost.value = post
  detailDialogVisible.value = true
  canVote.value = !post.voteOptions?.some(o => o.hasVoted)
  
  const postId = post.id || post.discussId
  if (!postId) {
    replies.value = []
    return
  }
  try {
    const res = await getReplies(postId)
    replies.value = res.data || []
  } catch (error) {
    console.warn('获取回复失败（后端可能未实现该接口）:', error)
    replies.value = []
  }
}

// 点赞帖子
async function handleLike(post) {
  const postId = post.id || post.discussId
  if (!postId) return
  try {
    if (post.isLiked) {
      await unlikePost(postId)
    } else {
      await likePost(postId)
    }
    post.isLiked = !post.isLiked
    post.likeCount += post.isLiked ? 1 : -1
  } catch (error) {
    post.isLiked = !post.isLiked
    post.likeCount += post.isLiked ? 1 : -1
  }
}

// 下载附件
function handleDownload(row) {
  const attachmentId = row.attachmentId
  if (!attachmentId) {
    ElMessage.info('无附件')
    return
  }
  // 先获取文件信息（原始文件名），再下载
  getFileById(attachmentId).then(fileRes => {
    const fileName = fileRes.data?.fileName || '附件'
    return downloadFile(attachmentId).then(res => {
      const blob = new Blob([res.data])
      const url = URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = fileName
      a.click()
      URL.revokeObjectURL(url)
    })
  }).catch(() => ElMessage.error('下载失败'))
}

// 删除帖子
async function handleDeletePost(post) {
  const postId = post.id || post.discussId
  if (!postId) return
  try {
    await ElMessageBox.confirm('确定删除该帖子吗？', '提示', { type: 'warning' })
    await deletePost(postId)
    ElMessage.success('删除成功')
    loadPosts()
    if (currentPost.value?.id === postId || currentPost.value?.discussId === postId) {
      detailDialogVisible.value = false
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 点赞回复
async function handleLikeReply(reply) {
  try {
    await likeReply(reply.id)
    reply.likeCount++
  } catch (error) {
    reply.likeCount++
  }
}

// 回复帖子
function handleReplyReply(reply) {
  replyContent.value = `@${reply.author} `
}

// 提交回复
async function handleSubmitReply() {
  if (!replyContent.value.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }
  try {
    await createReply(currentPost.value.id, { content: replyContent.value })
    ElMessage.success('回复成功')
    replyContent.value = ''
    const res = await getReplies(currentPost.value.id)
    replies.value = res.data || []
    currentPost.value.replyCount++
  } catch (error) {
    ElMessage.error('回复失败')
  }
}

// 投票
async function handleVote(option) {
  if (!canVote.value) return
  try {
    await vote(currentPost.value.id, option.id)
    canVote.value = false
    option.hasVoted = true
    option.isSelected = true
    // 更新投票统计
    const total = currentPost.value.voteOptions.reduce((sum, o) => sum + o.voteCount, 0) + 1
    currentPost.value.voteOptions.forEach(o => {
      if (o.id === option.id) {
        o.voteCount++
      }
      o.percentage = Math.round((o.voteCount / total) * 100)
    })
    ElMessage.success('投票成功')
  } catch (error) {
    ElMessage.error('投票失败')
  }
}

// 分页
function handlePageChange(page) {
  currentPage.value = page
  loadPosts()
}

function handleSizeChange(size) {
  pageSize.value = size
  loadPosts()
}

// 初始化
onMounted(() => {
  loadPosts()
})
</script>

<style scoped lang="scss">
.chat-page {
  .sidebar-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .filter-section {
      h4 {
        margin: 0 0 10px 0;
        font-size: 14px;
        color: #909399;
      }
    }
  }

  .post-list-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  }

  .post-list {
    .post-item {
      padding: 20px;
      border-bottom: 1px solid #ebeef5;
      cursor: pointer;
      transition: all 0.3s;

      &:hover {
        background-color: #f5f7fa;
      }

      .post-header {
        display: flex;
        align-items: center;
        margin-bottom: 15px;

        .post-info {
          flex: 1;
          margin-left: 12px;

          .post-author {
            font-weight: 600;
            color: #303133;
          }

          .post-time {
            font-size: 12px;
            color: #909399;
          }
        }
      }

      .post-title {
        font-size: 18px;
        font-weight: 600;
        color: #303133;
        margin-bottom: 10px;
      }

      .post-content {
        font-size: 14px;
        color: #606266;
        line-height: 1.6;
        margin-bottom: 15px;
      }

      .post-footer {
        display: flex;
        gap: 20px;

        .post-stat {
          display: flex;
          align-items: center;
          gap: 5px;
          font-size: 13px;
          color: #909399;

          &.liked {
            color: #f56c6c;
          }
        }
      }
    }
  }

  .pagination {
    display: flex;
    justify-content: flex-end;
    margin-top: 15px;
  }

  .post-detail {
    .detail-header {
      border-bottom: 1px solid #ebeef5;
      padding-bottom: 20px;
      margin-bottom: 20px;

      h2 {
        margin: 0 0 15px 0;
      }

      .detail-meta {
        display: flex;
        align-items: center;
        gap: 10px;
        color: #909399;
        font-size: 14px;
      }
    }

    .detail-content {
      font-size: 15px;
      line-height: 1.8;
      color: #303133;
      margin-bottom: 20px;
    }

    .vote-section {
      background: #f5f7fa;
      padding: 20px;
      border-radius: 8px;
      margin-bottom: 20px;

      h4 {
        margin: 0 0 15px 0;
      }

      .vote-option-item {
        padding: 10px;
        margin-bottom: 10px;
        background: #fff;
        border-radius: 4px;
        cursor: pointer;

        .vote-bar {
          height: 6px;
          background: #e4e7ed;
          border-radius: 3px;
          margin-top: 8px;

          .vote-progress {
            height: 100%;
            background: #409eff;
            border-radius: 3px;
            transition: width 0.3s;
          }
        }

        .vote-count {
          font-size: 12px;
          color: #909399;
          margin-top: 5px;
          display: block;
        }

        &.selected {
          border: 2px solid #409eff;
        }
      }
    }

    .detail-actions {
      margin-bottom: 20px;
    }

    .reply-section {
      h4 {
        margin: 0 0 15px 0;
        padding-bottom: 10px;
        border-bottom: 1px solid #ebeef5;
      }

      .reply-list {
        .reply-item {
          display: flex;
          gap: 12px;
          padding: 15px 0;
          border-bottom: 1px solid #f0f2f5;

          .reply-content {
            flex: 1;

            .reply-header {
              margin-bottom: 8px;

              .reply-author {
                font-weight: 600;
                color: #303133;
                margin-right: 10px;
              }

              .reply-time {
                font-size: 12px;
                color: #909399;
              }
            }

            .reply-text {
              font-size: 14px;
              color: #606266;
              line-height: 1.6;
            }

            .reply-actions {
              display: flex;
              gap: 15px;
              margin-top: 8px;

              .reply-action {
                display: flex;
                align-items: center;
                gap: 5px;
                font-size: 12px;
                color: #909399;
                cursor: pointer;

                &:hover {
                  color: #409eff;
                }
              }
            }
          }
        }
      }

      .reply-input {
        margin-top: 20px;

        .el-textarea {
          margin-bottom: 10px;
        }
      }
    }
  }

  .vote-options {
    .vote-option {
      display: flex;
      align-items: center;
      gap: 10px;
      margin-bottom: 10px;

      .el-input {
        flex: 1;
      }
    }
  }
}
</style>
