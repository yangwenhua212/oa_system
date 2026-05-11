<template>
  <div class="mail-page">
    <!-- 面包屑 -->
    <div class="page-breadcrumb">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>邮箱管理</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <div class="mail-container">
      <!-- 左侧导航栏 -->
      <div class="mail-sidebar">
        <el-button type="primary" class="write-btn" @click="composeVisible = true">
          <el-icon><Edit /></el-icon>
          写信
        </el-button>

        <div class="sidebar-section">
          <div class="section-title">文件夹</div>
          <div class="folder-list">
            <div
              v-for="f in folders"
              :key="f.key"
              class="folder-item"
              :class="{ active: activeFolder === f.key }"
              @click="activeFolder = f.key"
            >
              <el-icon><component :is="f.icon" /></el-icon>
              <span>{{ f.label }}</span>
              <el-tag v-if="f.badge" size="small" type="danger" class="folder-badge">{{ f.badge }}</el-tag>
            </div>
          </div>
        </div>

        <div class="sidebar-section">
          <div class="section-title">类型</div>
          <div class="type-list">
            <div
              v-for="t in types"
              :key="t.key"
              class="type-item"
              :class="{ active: activeType === t.key }"
              @click="activeType = t.key"
            >
              <span>{{ t.label }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧邮件列表区 -->
      <div class="mail-content">
        <el-card class="content-card">
          <!-- 标题与搜索 -->
          <div class="content-header">
            <h3>{{ folderTitle }}</h3>
            <el-input
              v-model="searchKeyword"
              placeholder="类型，主题，状态，收件人查询"
              clearable
              class="search-input"
              @keyup.enter="handleSearch"
            >
              <template #suffix>
                <el-icon class="search-icon" @click="handleSearch"><Search /></el-icon>
              </template>
            </el-input>
          </div>

          <!-- 批量操作栏 -->
          <div class="batch-bar">
            <el-checkbox v-model="checkAll" :indeterminate="isIndeterminate" @change="handleCheckAll">全选</el-checkbox>
            <el-button size="small" text @click="handleBatchDelete">删除</el-button>
            <el-button size="small" text @click="handleBatchPreview">预览</el-button>
            <el-button size="small" text @click="handleBatchStar">标星</el-button>
            <el-button size="small" text @click="loadData" class="refresh-btn">
              <el-icon><Refresh /></el-icon>
            </el-button>
          </div>

          <!-- 邮件列表 -->
          <el-table
            :data="tableData"
            stripe
            border
            v-loading="loading"
            @selection-change="handleSelectionChange"
          >
            <el-table-column type="selection" width="40" align="center" />
            <el-table-column label="星标" width="50" align="center">
              <template #default="{ row }">
                <el-icon
                  :color="row.starred ? '#e6a23c' : '#ddd'"
                  style="cursor:pointer"
                  @click="row.starred = !row.starred"
                >
                  <StarFilled v-if="row.starred" />
                  <Star v-else />
                </el-icon>
              </template>
            </el-table-column>
            <el-table-column prop="mailType" label="类型" width="80" align="center">
              <template #default="{ row }">
                <span :style="{ color: row.mailType === '公告' ? '#f56c6c' : '#333' }">{{ row.mailType }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="to" label="收件人" width="160" show-overflow-tooltip />
            <el-table-column prop="subject" label="主题" min-width="200" show-overflow-tooltip />
            <el-table-column prop="time" label="时间" width="170" />
            <el-table-column label="附件" width="60" align="center">
              <template #default="{ row }">
                <el-icon v-if="row.hasAttachment" color="#409eff"><Paperclip /></el-icon>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="80" align="center">
              <template #default="{ row }">
                <el-tag size="small" type="info" plain>{{ row.status }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="80" align="center">
              <template #default="{ row }">
                <el-button type="primary" link size="small">查看</el-button>
              </template>
            </el-table-column>
          </el-table>

          <!-- 分页 -->
          <div class="pagination-wrap">
            <span class="pagination-info">共{{ total }}条 | 每页{{ pageSize }}条 | 共{{ totalPages }}页</span>
            <el-pagination
              v-model:current-page="pageNum"
              v-model:page-size="pageSize"
              :total="total"
              :page-sizes="[10, 20, 50]"
              layout="prev, pager, next"
              background
              size="small"
              @current-change="loadData"
            />
          </div>
        </el-card>
      </div>
    </div>

    <!-- 写信对话框 -->
    <el-dialog v-model="composeVisible" title="写信" width="600px" destroy-on-close>
      <el-form :model="mailForm" label-width="80px">
        <el-form-item label="收件人">
          <el-input v-model="mailForm.to" placeholder="请输入收件人" />
        </el-form-item>
        <el-form-item label="主题">
          <el-input v-model="mailForm.subject" placeholder="请输入主题" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="mailForm.content" type="textarea" :rows="8" placeholder="请输入邮件内容" />
        </el-form-item>
        <el-form-item label="附件">
          <el-upload class="upload-demo" :auto-upload="false">
            <el-button size="small" text>
              <el-icon><Paperclip /></el-icon>
              添加附件
            </el-button>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="composeVisible = false">取消</el-button>
        <el-button type="primary" class="btn-primary-green" @click="handleSendMail">发送</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, watchEffect, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Edit, Refresh, StarFilled, Star, Paperclip } from '@element-plus/icons-vue'
import { getInbox, getSent, getDrafts, sendMail, getUnreadCount, getMailById, deleteMail } from '@/api/mail'

const loading = ref(false)
const activeFolder = ref('inbox')
const activeType = ref('announce')
const searchKeyword = ref('')
const composeVisible = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const selectedRows = ref([])
const checkAll = ref(false)
const isIndeterminate = ref(false)
const rawData = ref([])

const totalPages = computed(() => Math.ceil(total.value / pageSize.value))

const folders = [
  { key: 'inbox', label: '收件箱', icon: 'Message', badge: 0 },
  { key: 'sent', label: '发件箱', icon: 'Message', badge: null },
  { key: 'draft', label: '草稿箱', icon: 'Document', badge: null },
  { key: 'trash', label: '垃圾箱', icon: 'Delete', badge: null }
]

const types = [
  { key: 'all', label: '全部' },
  { key: 'mail', label: '邮件' },
  { key: 'notice', label: '通知' },
  { key: 'announce', label: '公告' }
]

const mailForm = reactive({
  to: '',
  subject: '',
  content: ''
})

const folderTitle = computed(() => {
  const map = { inbox: '收件箱', sent: '发件箱', draft: '草稿箱', trash: '垃圾箱' }
  return map[activeFolder.value] || '收件箱'
})

// 加载数据
async function loadData() {
  loading.value = true
  try {
    let res
    if (activeFolder.value === 'inbox') res = await getInbox({ pageNum: pageNum.value, pageSize: pageSize.value })
    else if (activeFolder.value === 'sent') res = await getSent({ pageNum: pageNum.value, pageSize: pageSize.value })
    else if (activeFolder.value === 'draft') res = await getDrafts({ pageNum: pageNum.value, pageSize: pageSize.value })
    else res = { code: 200, data: { records: [], total: 0 } }

    if (res.code === 200) {
      rawData.value = (res.data?.records || res.data || []).map(d => ({
        id: d.id,
        starred: d.isStar === 1,
        mailType: '邮件',
        to: d.toNames || '',
        subject: d.subject || '',
        time: d.sendTime || d.createTime || '',
        hasAttachment: !!d.attachmentIds,
        status: d.folder === 'draft' ? '草稿' : '一般',
        senderName: d.senderName
      }))
      total.value = res.data?.total || rawData.value.length
    }
  } catch (error) {
    console.error(error)
    rawData.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 加载未读数量
async function loadUnread() {
  try {
    const res = await getUnreadCount()
    if (res.code === 200) folders[0].badge = res.data
  } catch (e) { /* ignore */ }
}

// 根据类型过滤
const tableData = computed(() => {
  let data = rawData.value
  if (activeType.value === 'mail') data = data.filter(d => d.mailType === '邮件')
  else if (activeType.value === 'notice') data = data.filter(d => d.mailType === '通知')
  else if (activeType.value === 'announce') data = data.filter(d => d.mailType === '公告')
  return data
})

watch(activeFolder, () => {
  selectedRows.value = []
  checkAll.value = false
  isIndeterminate.value = false
  pageNum.value = 1
  loadData()
})

watch(activeType, () => { pageNum.value = 1 })

onMounted(() => {
  loadData()
  loadUnread()
})

async function handleSendMail() {
  if (!mailForm.to || !mailForm.subject) return ElMessage.warning('请填写收件人和主题')
  try {
    await sendMail({
      toNames: mailForm.to,
      subject: mailForm.subject,
      content: mailForm.content
    })
    ElMessage.success('发送成功')
    composeVisible.value = false
    mailForm.to = ''
    mailForm.subject = ''
    mailForm.content = ''
    loadData()
  } catch (error) {
    ElMessage.error('发送失败')
  }
}

function handleSearch() {
  loadData()
}
function handleCheckAll(val) { isIndeterminate.value = false }
function handleSelectionChange(val) {
  selectedRows.value = val
  checkAll.value = val.length === tableData.value.length
  isIndeterminate.value = val.length > 0 && val.length < tableData.value.length
}
async function handleBatchDelete() {
  if (selectedRows.value.length === 0) {
    return ElMessage.warning('请先选择要删除的邮件')
  }
  try {
    for (const row of selectedRows.value) {
      await deleteMail(row.id)
    }
    ElMessage.success('删除成功')
    selectedRows.value = []
    checkAll.value = false
    isIndeterminate.value = false
    loadData()
  } catch (error) {
    ElMessage.error('删除失败')
  }
}
function handleBatchPreview() {}
function handleBatchStar() {}
</script>

<style scoped lang="scss">
.mail-page {
  min-height: 100%;

  .page-breadcrumb {
    margin-bottom: 16px;
  }

  .mail-container {
    display: flex;
    gap: 16px;
    align-items: flex-start;
  }

  // ===== 左侧导航栏 =====
  .mail-sidebar {
    width: 200px;
    flex-shrink: 0;
    background: #fff;
    border-radius: 4px;
    padding: 16px;
    box-shadow: 0 1px 3px rgba(0,0,0,0.06);

    .write-btn {
      width: 100%;
      height: 40px;
      font-size: 15px;
      margin-bottom: 20px;
    }

    .sidebar-section {
      margin-bottom: 20px;

      .section-title {
        font-size: 12px;
        color: #999;
        text-transform: uppercase;
        margin-bottom: 8px;
        padding-left: 4px;
      }
    }

    .folder-list, .type-list {
      .folder-item, .type-item {
        display: flex;
        align-items: center;
        gap: 8px;
        padding: 8px 10px;
        border-radius: 4px;
        cursor: pointer;
        font-size: 14px;
        color: #333;
        position: relative;

        &:hover {
          background: #f0f2f5;
        }

        &.active {
          background: #e8f5e9;
          color: #00a65a;
          font-weight: 500;
        }

        .folder-badge {
          position: absolute;
          right: 8px;
        }
      }
    }

    .type-list {
      .type-item {
        padding-left: 10px;
      }
    }
  }

  // ===== 右侧邮件列表 =====
  .mail-content {
    flex: 1;
    min-width: 0;

    .content-card {
      border-top: 3px solid #00a65a;

      .content-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 12px;

        h3 {
          margin: 0;
          font-size: 16px;
          color: #333;
        }

        .search-input {
          width: 320px;

          .search-icon {
            cursor: pointer;
            color: #999;

            &:hover {
              color: #00a65a;
            }
          }
        }
      }

      .batch-bar {
        display: flex;
        align-items: center;
        gap: 12px;
        padding: 8px 0;
        border-bottom: 1px solid #ebeef5;
        margin-bottom: 0;

        .refresh-btn {
          margin-left: auto;
        }
      }
    }

    .pagination-wrap {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-top: 16px;
      padding-top: 12px;
      border-top: 1px solid #ebeef5;

      .pagination-info {
        font-size: 13px;
        color: #666;
      }
    }
  }
}

.btn-primary-green {
  background-color: #00a65a !important;
  border-color: #00a65a !important;
  color: #fff !important;
  &:hover {
    background-color: #008d4c !important;
    border-color: #008d4c !important;
  }
}
</style>
