<template>
  <div class="chat-admin-page">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-button type="success" @click="handleAdd">
              <el-icon><Plus /></el-icon>新增
            </el-button>
            <el-button @click="loadData">
              <el-icon><Refresh /></el-icon>刷新
            </el-button>
          </div>
          <div class="header-right">
            <el-input
              v-model="searchKeyword"
              placeholder="按标题查找"
              style="width: 200px"
              clearable
              @keyup.enter="handleSearch"
            >
              <template #suffix>
                <el-icon @click="handleSearch" class="search-icon"><Search /></el-icon>
              </template>
            </el-input>
          </div>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column type="selection" width="55" />
        <el-table-column label="部门" width="120">
          <template #default="{ row }">
            {{ row.deptName || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="发布人" width="100">
          <template #default="{ row }">
            {{ row.author || row.discussUserName }}
          </template>
        </el-table-column>
        <el-table-column label="类型" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="getTypeType(row.type)" size="small">{{ row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column label="发布时间" width="160">
          <template #default="{ row }">
            {{ row.publishTime || formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="访问量" width="80" align="center">
          <template #default="{ row }">
            {{ row.viewCount || row.visitNum || 0 }}
          </template>
        </el-table-column>
        <el-table-column label="点赞数" width="80" align="center">
          <template #default="{ row }">
            {{ row.likeCount || 0 }}
          </template>
        </el-table-column>
        <el-table-column label="评论数" width="80" align="center">
          <template #default="{ row }">
            {{ row.commentCount || row.replyCount || 0 }}
          </template>
        </el-table-column>
        <el-table-column label="附件" width="100" align="center">
          <template #default="{ row }">
            <el-button v-if="row.attachmentId" link type="primary" size="small" @click="handleDownload(row)">
              <el-icon><Paperclip /></el-icon>附件
            </el-button>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleEdit(row)">
              <el-icon><Edit /></el-icon>修改
            </el-button>
            <el-button link type="success" size="small" @click="handleView(row)">
              <el-icon><View /></el-icon>查看
            </el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">
              <el-icon><Delete /></el-icon>删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-bar">
        <span class="pagination-info">
          共 {{ total }} 条 | 每页 {{ pageSize }} 条 | 共 {{ Math.ceil(total / pageSize) }} 页
        </span>
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="prev, pager, next, jumper"
          @current-change="handlePageChange"
          @size-change="handleSizeChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑帖子' : '新增帖子'" width="700px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入标题" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="6" placeholder="请输入内容" />
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
          <span v-if="form.fileName" class="file-name">当前文件：{{ form.fileName }}</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>

    <!-- 查看详情对话框 -->
    <el-dialog v-model="viewDialogVisible" title="帖子详情" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="发布人">{{ currentRow?.author || currentRow?.discussUserName }}</el-descriptions-item>
        <el-descriptions-item label="部门">{{ currentRow?.deptName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="发布时间">{{ currentRow?.publishTime || formatTime(currentRow?.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="访问量">{{ currentRow?.viewCount || currentRow?.visitNum || 0 }}</el-descriptions-item>
        <el-descriptions-item label="点赞数">{{ currentRow?.likeCount || 0 }}</el-descriptions-item>
        <el-descriptions-item label="评论数">{{ currentRow?.commentCount || currentRow?.replyCount || 0 }}</el-descriptions-item>
        <el-descriptions-item label="附件">
          <el-button v-if="currentRow?.attachmentId" link type="primary" @click="handleDownload(currentRow)">
            <el-icon><Paperclip /></el-icon>下载附件
          </el-button>
          <span v-else>无</span>
        </el-descriptions-item>
      </el-descriptions>
      <div class="detail-content">
        <h4>标题</h4>
        <p>{{ currentRow?.title }}</p>
        <h4>内容</h4>
        <p>{{ currentRow?.content }}</p>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Refresh, Search, Edit, View, Delete, Paperclip, Upload } from '@element-plus/icons-vue'
import { getPosts, createPost, updatePost, deletePost, getPostById } from '@/api/chat'
import { uploadFile, downloadFile, getFileById } from '@/api/file'
import dayjs from 'dayjs'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchKeyword = ref('')
const allPosts = ref([])

const dialogVisible = ref(false)
const viewDialogVisible = ref(false)
const isEdit = ref(false)
const currentRow = ref(null)
const formRef = ref(null)

const form = reactive({
  id: null,
  title: '',
  content: '',
  file: null,
  fileName: '',
  attachmentId: null
})

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

function getTypeType(type) {
  const map = { '公告': 'danger', '投票': 'warning', '讨论': 'primary' }
  return map[type] || 'info'
}

function formatTime(t) {
  return t ? dayjs(t).format('YYYY-MM-DD HH:mm') : '-'
}

function normalizePost(item) {
  return {
    ...item,
    id: item.id || item.discussId,
    author: item.author || item.discussUserName,
    deptName: item.deptName || '-',
    viewCount: item.viewCount || item.visitNum || 0,
    replyCount: item.replyCount || 0
  }
}

async function loadData() {
  loading.value = true
  try {
    const res = await getPosts()
    let list = res.data || []
    allPosts.value = list.map(normalizePost)
    applyFilter()
  } catch (error) {
    console.error('加载帖子失败', error)
    allPosts.value = []
    tableData.value = []
  } finally {
    loading.value = false
  }
}

function applyFilter() {
  let data = [...allPosts.value]
  if (searchKeyword.value) {
    data = data.filter(item => item.title?.includes(searchKeyword.value))
  }
  total.value = data.length
  const start = (pageNum.value - 1) * pageSize.value
  const end = start + pageSize.value
  tableData.value = data.slice(start, end)
}

function handleSearch() {
  pageNum.value = 1
  applyFilter()
}

function handleAdd() {
  isEdit.value = false
  Object.assign(form, { id: null, title: '', content: '', file: null, fileName: '', attachmentId: null })
  dialogVisible.value = true
}

function handleEdit(row) {
  isEdit.value = true
  currentRow.value = row
  Object.assign(form, {
    id: row.id || row.discussId,
    title: row.title,
    content: row.content,
    file: null,
    fileName: row.fileName || '',
    attachmentId: row.attachmentId || null
  })
  dialogVisible.value = true
}

function handleView(row) {
  currentRow.value = row
  viewDialogVisible.value = true
}

async function handleDelete(row) {
  const id = row.id || row.discussId
  if (!id) return
  try {
    await ElMessageBox.confirm('确定删除该帖子吗？', '提示', { type: 'warning' })
    await deletePost(id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('删除失败')
  }
}

function handleFileChange(file) {
  form.file = file.raw
  form.fileName = file.name
}

function handleFileRemove() {
  form.file = null
  form.fileName = ''
  form.attachmentId = null
}

async function handleSubmit() {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    submitLoading.value = true
    try {
      let attachmentId = form.attachmentId
      // 如果有新文件，先上传
      if (form.file) {
        const fd = new FormData()
        fd.append('file', form.file)
        fd.append('model', 'discuss')
        const uploadRes = await uploadFile(fd)
        if (uploadRes.code === 200 && uploadRes.data?.fileId) {
          attachmentId = uploadRes.data.fileId
        }
      }

      const postData = { title: form.title, content: form.content }
      if (attachmentId) {
        postData.attachmentId = attachmentId
      }

      if (isEdit.value) {
        await updatePost(form.id, postData)
        ElMessage.success('修改成功')
      } else {
        await createPost(postData)
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      loadData()
    } catch (error) {
      ElMessage.error('操作失败')
    } finally {
      submitLoading.value = false
    }
  })
}

function handleDownload(row) {
  const attachmentId = row.attachmentId
  if (!attachmentId) {
    ElMessage.info('无附件')
    return
  }
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

function handlePageChange(val) {
  pageNum.value = val
  applyFilter()
}

function handleSizeChange(val) {
  pageSize.value = val
  pageNum.value = 1
  applyFilter()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.chat-admin-page {
  .box-card {
    border-top: 3px solid #00a65a;
  }
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  .header-left {
    display: flex;
    gap: 10px;
  }
  .search-icon {
    cursor: pointer;
    color: #909399;
    &:hover { color: #409eff; }
  }
  .pagination-bar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: 15px;
    padding-top: 15px;
    border-top: 1px solid #ebeef5;
  }
  .pagination-info {
    font-size: 13px;
    color: #606266;
  }
  .detail-content {
    margin-top: 20px;
    h4 { margin: 15px 0 10px; color: #303133; }
    p {
      color: #606266;
      line-height: 1.6;
      padding: 10px;
      background: #f5f7fa;
      border-radius: 4px;
    }
  }
  .file-name {
    font-size: 12px;
    color: #909399;
    margin-left: 10px;
  }
}

.el-button--primary {
  color: #fff;
}
</style>
