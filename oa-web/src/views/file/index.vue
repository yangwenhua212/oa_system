<template>
  <div class="file-page">
    <el-card class="main-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-breadcrumb separator="/">
              <el-breadcrumb-item 
                v-for="(folder, index) in breadcrumb" 
                :key="index"
                @click="navigateToFolder(index)"
                class="breadcrumb-item"
              >
                {{ folder.name }}
              </el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          <div class="header-actions">
            <el-button type="primary" size="small" @click="handleNewFolder">
              <el-icon><FolderAdd /></el-icon>
              新建文件夹
            </el-button>
            <el-button type="success" size="small" @click="handleUpload">
              <el-icon><Upload /></el-icon>
              上传文件
            </el-button>
          </div>
        </div>
      </template>

      <!-- 文件统计 -->
      <div class="stats-container">
        <div class="stat-item">
          <el-icon><Document /></el-icon>
          <span>文档: {{ stats.doc || 0 }}</span>
        </div>
        <div class="stat-item">
          <el-icon><Picture /></el-icon>
          <span>图片: {{ stats.image || 0 }}</span>
        </div>
        <div class="stat-item">
          <el-icon><VideoCamera /></el-icon>
          <span>视频: {{ stats.video || 0 }}</span>
        </div>
        <div class="stat-item">
          <el-icon><Microphone /></el-icon>
          <span>音频: {{ stats.audio || 0 }}</span>
        </div>
        <div class="stat-item">
          <el-icon><Files /></el-icon>
          <span>其他: {{ stats.other || 0 }}</span>
        </div>
        <div class="stat-item">
          <el-icon><Folder /></el-icon>
          <span>文件夹: {{ folderCount }}</span>
        </div>
      </div>

      <!-- 文件列表 -->
      <el-table 
        :data="filteredFiles" 
        stripe 
        v-loading="loading"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column label="文件名" min-width="300">
          <template #default="{ row }">
            <div class="file-name" @click="handleOpen(row)">
              <el-icon class="file-icon" :style="{ color: getFileIcon(row).color }">
                <component :is="getFileIcon(row).icon" />
              </el-icon>
              <span>{{ row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="size" label="大小" width="120">
          <template #default="{ row }">
            {{ row.isFolder ? '-' : formatSize(row.size) }}
          </template>
        </el-table-column>
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            {{ row.isFolder ? '文件夹' : (row.type || '未知') }}
          </template>
        </el-table-column>
        <el-table-column prop="uploadTime" label="修改时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.uploadTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <template v-if="row.isFolder">
              <el-button type="primary" link @click="handleOpen(row)">打开</el-button>
            </template>
            <template v-else>
              <el-button type="primary" link @click="handleDownload(row)">下载</el-button>
            </template>
            <el-button type="primary" link @click="handleRename(row)">重命名</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 上传对话框 -->
    <el-dialog v-model="uploadDialogVisible" title="上传文件" width="500px">
      <el-upload
        ref="uploadRef"
        class="upload-container"
        drag
        :action="uploadUrl"
        :headers="headers"
        :on-success="handleUploadSuccess"
        :on-error="handleUploadError"
        :on-progress="handleUploadProgress"
        :before-upload="beforeUpload"
        :file-list="fileList"
        multiple
        accept=".doc,.docx,.pdf,.jpg,.png,.xls,.xlsx,.ppt,.pptx,.zip,.rar,.txt,.gif,.bmp"
      >
        <el-icon class="upload-icon"><UploadFilled /></el-icon>
        <div class="upload-text">将文件拖到此处，或<em>点击上传</em></div>
        <template #tip>
          <div class="upload-tip">
            支持扩展名: .doc, .docx, .pdf, .jpg, .png, .xls, .xlsx, .ppt, .pptx, .zip, .rar, .txt, .gif, .bmp<br/>
            单个文件大小限制: 50MB
          </div>
        </template>
      </el-upload>
      <template #footer>
        <el-button @click="uploadDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 新建文件夹对话框 -->
    <el-dialog v-model="folderDialogVisible" title="新建文件夹" width="400px">
      <el-form :model="folderForm" ref="folderFormRef">
        <el-form-item label="文件夹名称" prop="name">
          <el-input v-model="folderForm.name" placeholder="请输入文件夹名称" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="folderDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCreateFolder">创建</el-button>
      </template>
    </el-dialog>

    <!-- 重命名对话框 -->
    <el-dialog v-model="renameDialogVisible" title="重命名" width="400px">
      <el-form :model="renameForm" ref="renameFormRef">
        <el-form-item label="新名称" prop="newName">
          <el-input v-model="renameForm.newName" placeholder="请输入新名称" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="renameDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirmRename">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getFiles,
  uploadFile,
  deleteFile,
  downloadFile,
  createFolder,
  renameFile,
  getBreadcrumb,
  getFileStats
} from '@/api/file'
import { useUserStore } from '@/store/user'
import {
  Document,
  DocumentCopy,
  DocumentChecked,
  FolderRemove,
  Picture,
  VideoCamera,
  Microphone,
  Folder,
  Files,
  FolderAdd,
  UploadFilled
} from '@element-plus/icons-vue'

const userStore = useUserStore()

// 状态变量
const loading = ref(false)
const files = ref([])
const currentPathId = ref(0)
const breadcrumb = ref([{ id: 0, name: '全部文件' }])
const selectedFiles = ref([])
const uploadDialogVisible = ref(false)
const folderDialogVisible = ref(false)
const renameDialogVisible = ref(false)
const uploadRef = ref(null)
const fileList = ref([])
const stats = ref({})

// 上传URL和headers
const uploadUrl = '/api/file/upload'
const headers = computed(() => ({
  Authorization: `Bearer ${userStore.token || localStorage.getItem('token')}`
}))

// 文件夹表单
const folderForm = reactive({
  name: ''
})

// 重命名表单
const renameForm = reactive({
  id: null,
  name: '',
  newName: '',
  type: ''
})

// 统计文件夹数量
const folderCount = computed(() => files.value.filter(f => f.isFolder).length)

// 过滤文件（当前目录下的）
const filteredFiles = computed(() => {
  return files.value.filter(f => {
    const pid = f.parentId || 0
    return pid === currentPathId.value || (currentPathId.value === 0 && pid === 0)
  })
})

// 获取文件图标
function getFileIcon(file) {
  if (file.isFolder) {
    return { icon: Folder, color: '#e6a23c' }
  }
  
  const ext = file.type?.toLowerCase() || file.name?.split('.').pop()?.toLowerCase() || ''
  
  if (['doc', 'docx'].includes(ext)) {
    return { icon: DocumentCopy, color: '#409eff' }
  }
  if (['xls', 'xlsx'].includes(ext)) {
    return { icon: DocumentChecked, color: '#67c23a' }
  }
  if (['ppt', 'pptx'].includes(ext)) {
    return { icon: DocumentCopy, color: '#e6a23c' }
  }
  if (ext === 'pdf') {
    return { icon: Document, color: '#f56c6c' }
  }
  if (['jpg', 'jpeg', 'png', 'gif', 'bmp'].includes(ext)) {
    return { icon: Picture, color: '#909399' }
  }
  if (['mp4', 'avi', 'mov', 'wmv'].includes(ext)) {
    return { icon: VideoCamera, color: '#9c27b0' }
  }
  if (['mp3', 'wav', 'flac'].includes(ext)) {
    return { icon: Microphone, color: '#f44336' }
  }
  if (['zip', 'rar', '7z', 'tar', 'gz'].includes(ext)) {
    return { icon: FolderRemove, color: '#795548' }
  }

  return { icon: Document, color: '#909399' }
}

// 格式化文件大小
function formatSize(size) {
  if (!size) return '0 B'
  const units = ['B', 'KB', 'MB', 'GB', 'TB']
  let index = 0
  while (size >= 1024 && index < units.length - 1) {
    size /= 1024
    index++
  }
  return `${size.toFixed(2)} ${units[index]}`
}

// 格式化日期
function formatDate(dateStr) {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hour = String(date.getHours()).padStart(2, '0')
  const minute = String(date.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hour}:${minute}`
}

// 加载面包屑
async function loadBreadcrumb() {
  try {
    const res = await getBreadcrumb(currentPathId.value)
    if (res.code === 200 && res.data) {
      breadcrumb.value = res.data
    }
  } catch (error) {
    console.error('加载面包屑失败:', error)
    breadcrumb.value = [{ id: 0, name: '全部文件' }]
  }
}

// 加载文件列表
async function loadFiles() {
  loading.value = true
  try {
    const res = await getFiles({ pathId: currentPathId.value })
    if (res.code === 200 && res.data) {
      files.value = res.data.files || []
      currentPathId.value = res.data.currentPathId || 0
    } else {
      files.value = []
    }
    await loadBreadcrumb()
    await loadStats()
  } catch (error) {
    console.error('加载文件失败:', error)
    files.value = []
  } finally {
    loading.value = false
  }
}

// 加载统计
async function loadStats() {
  try {
    const res = await getFileStats()
    if (res.code === 200 && res.data) {
      stats.value = res.data
    }
  } catch (error) {
    console.error('加载统计失败:', error)
  }
}

// 打开文件/文件夹
function handleOpen(row) {
  if (row.isFolder) {
    currentPathId.value = row.id
    loadFiles()
  } else if (row.fileId) {
    handleDownload(row)
  }
}

// 导航到指定目录
function navigateToFolder(index) {
  const targetFolder = breadcrumb.value[index]
  if (!targetFolder) return
  currentPathId.value = targetFolder.id
  loadFiles()
}

// 返回上一级
function goBack() {
  if (breadcrumb.value.length > 1) {
    navigateToFolder(breadcrumb.value.length - 2)
  }
}

// 下载文件
async function handleDownload(row) {
  try {
    ElMessage.info(`正在下载: ${row.name}`)
    const token = userStore.token || localStorage.getItem('token')
    const fileId = row.fileId || row.id
    const response = await fetch(`/api/file/download/${fileId}`, {
      headers: { Authorization: `Bearer ${token}` }
    })
    if (!response.ok) throw new Error('下载失败')
    const blob = await response.blob()
    const url = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = row.name
    link.click()
    URL.revokeObjectURL(url)
  } catch (error) {
    console.error('下载失败:', error)
    ElMessage.error('下载失败')
  }
}

// 重命名
function handleRename(row) {
  renameForm.id = row.id
  renameForm.name = row.name
  renameForm.newName = row.name
  renameForm.type = row.isFolder ? 'folder' : 'file'
  renameDialogVisible.value = true
}

async function handleConfirmRename() {
  if (!renameForm.newName.trim()) {
    ElMessage.warning('请输入名称')
    return
  }
  try {
    await renameFile({
      id: renameForm.id,
      newName: renameForm.newName,
      type: renameForm.type
    })
    ElMessage.success('重命名成功')
    renameDialogVisible.value = false
    loadFiles()
  } catch (error) {
    ElMessage.error('重命名失败')
  }
}

// 删除
async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(
      row.isFolder ? '确定要删除该文件夹及其所有内容吗？' : '确定要删除该文件吗？',
      '提示',
      { type: 'warning' }
    )
    await deleteFile(row.id)
    ElMessage.success('删除成功')
    loadFiles()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 新建文件夹
function handleNewFolder() {
  folderForm.name = ''
  folderDialogVisible.value = true
}

async function handleCreateFolder() {
  if (!folderForm.name.trim()) {
    ElMessage.warning('请输入文件夹名称')
    return
  }
  try {
    await createFolder({ 
      name: folderForm.name, 
      parentId: currentPathId.value 
    })
    ElMessage.success('创建成功')
    folderDialogVisible.value = false
    loadFiles()
  } catch (error) {
    console.error('创建失败:', error)
    ElMessage.error('创建失败')
  }
}

// 上传文件
function handleUpload() {
  fileList.value = []
  uploadDialogVisible.value = true
}

// 上传前验证
function beforeUpload(file) {
  const maxSize = 50 * 1024 * 1024 // 50MB
  if (file.size > maxSize) {
    ElMessage.error('文件大小不能超过 50MB')
    return false
  }
  return true
}

// 上传进度
function handleUploadProgress(event, file) {
  console.log('上传进度:', event, file)
}

// 上传成功
function handleUploadSuccess(response, file) {
  if (response.code === 200) {
    ElMessage.success(`${file.name} 上传成功`)
    loadFiles()
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

// 上传失败
function handleUploadError(err, file) {
  console.error('上传失败:', err)
  ElMessage.error(`${file.name} 上传失败`)
}

// 选择变化
function handleSelectionChange(selection) {
  selectedFiles.value = selection
}

// 初始化
onMounted(() => {
  loadFiles()
})
</script>

<style scoped lang="scss">
.file-page {
  .main-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .header-actions {
        display: flex;
        gap: 10px;
      }
    }
  }

  .breadcrumb-item {
    cursor: pointer;
    &:hover {
      color: #00a65a;
    }
  }

  .stats-container {
    display: flex;
    gap: 30px;
    padding: 15px 20px;
    background: #f5f7fa;
    border-radius: 8px;
    margin-bottom: 20px;

    .stat-item {
      display: flex;
      align-items: center;
      gap: 8px;
      color: #606266;
      font-size: 14px;

      .el-icon {
        font-size: 18px;
      }
    }
  }

  .file-name {
    display: flex;
    align-items: center;
    gap: 10px;
    cursor: pointer;

    &:hover {
      color: #00a65a;
    }

    .file-icon {
      font-size: 20px;
    }
  }

  .upload-container {
    .upload-icon {
      font-size: 67px;
      color: #409eff;
    }

    .upload-text {
      margin-top: 10px;
      em {
        color: #409eff;
        font-style: normal;
      }
    }

    .upload-tip {
      margin-top: 10px;
      color: #909399;
      font-size: 12px;
      line-height: 1.6;
    }
  }
}

.el-button--primary {
  color: #fff;
}
</style>
