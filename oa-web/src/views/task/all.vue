<template>
  <div class="task-all-page">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>全部任务</span>
          <el-button type="primary" @click="handleAdd" class="btn-primary-green">
            <el-icon><Plus /></el-icon>
            新建任务
          </el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-form :inline="true" :model="searchForm">
          <el-form-item label="任务名称">
            <el-input v-model="searchForm.taskName" placeholder="搜索任务" clearable @change="loadData" />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="全部状态" clearable @change="loadData" style="width:140px">
              <el-option label="待处理" value="todo" />
              <el-option label="进行中" value="in_progress" />
              <el-option label="测试中" value="testing" />
              <el-option label="已完成" value="completed" />
              <el-option label="已关闭" value="closed" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button @click="resetSearch">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="taskNo" label="编号" width="120" />
        <el-table-column prop="taskName" label="任务名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="handlerName" label="负责人" width="120" />
        <el-table-column label="优先级" width="100" align="center">
          <template #default="{ row }">
            <el-tag size="small" :type="priorityType(row.priority)">
              {{ priorityLabel(row.priority) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="120" align="center">
          <template #default="{ row }">
            <el-tag size="small" :type="statusType(row.status)">
              {{ statusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="进度" width="180">
          <template #default="{ row }">
            <el-progress :percentage="row.progress || 0" :status="row.progress === 100 ? 'success' : ''" />
          </template>
        </el-table-column>
        <el-table-column prop="endTime" label="截止时间" width="170">
          <template #default="{ row }">
            {{ formatDate(row.endTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">查看</el-button>
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>

    <!-- 新建/编辑任务对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="任务名称" prop="taskName">
          <el-input v-model="form.taskName" placeholder="请输入任务名称" />
        </el-form-item>
        <el-form-item label="任务内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="4" placeholder="请输入任务内容" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="优先级" prop="priority">
              <el-select v-model="form.priority" style="width:100%">
                <el-option label="紧急" value="urgent" />
                <el-option label="高" value="high" />
                <el-option label="中" value="medium" />
                <el-option label="低" value="low" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="负责人" prop="handlerId">
              <el-select v-model="form.handlerId" placeholder="请选择" clearable style="width:100%">
                <el-option v-for="u in userList" :key="u.id" :label="u.realName" :value="u.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始时间">
              <el-date-picker v-model="form.startTime" type="datetime" placeholder="选择开始时间" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="截止时间">
              <el-date-picker v-model="form.endTime" type="datetime" placeholder="选择截止时间" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="进度">
          <el-slider v-model="form.progress" :min="0" :max="100" :step="5" style="width:80%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" class="btn-primary-green">确定</el-button>
      </template>
    </el-dialog>

    <!-- 查看任务详情对话框 -->
    <el-dialog v-model="detailVisible" title="任务详情" width="700px" destroy-on-close>
      <template v-if="currentTask">
        <div class="detail-header">
          <h3>{{ currentTask.taskName }}</h3>
          <el-tag :type="statusType(currentTask.status)" size="small">{{ statusLabel(currentTask.status) }}</el-tag>
        </div>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="任务编号">{{ currentTask.taskNo }}</el-descriptions-item>
          <el-descriptions-item label="优先级">
            <el-tag :type="priorityType(currentTask.priority)" size="small">{{ priorityLabel(currentTask.priority) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="负责人">{{ currentTask.handlerName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="创建人">{{ currentTask.creatorName }}</el-descriptions-item>
          <el-descriptions-item label="开始时间">{{ formatDate(currentTask.startTime) }}</el-descriptions-item>
          <el-descriptions-item label="截止时间">{{ formatDate(currentTask.endTime) }}</el-descriptions-item>
          <el-descriptions-item label="进度" :span="2">
            <el-progress :percentage="currentTask.progress || 0" />
          </el-descriptions-item>
          <el-descriptions-item label="任务内容" :span="2">
            <div style="white-space:pre-wrap">{{ currentTask.content || '-' }}</div>
          </el-descriptions-item>
        </el-descriptions>
      </template>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getTaskList, getTaskById, createTask, updateTask, deleteTask } from '@/api/task'
import { getUserList } from '@/api/system/user'

const router = useRouter()
const loading = ref(false)
const tableData = ref([])
const pageNum = ref(1)
const pageSize = ref(20)
const total = ref(0)
const userList = ref([])

// 搜索
const searchForm = reactive({
  taskName: '',
  status: ''
})

// 表单
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref()
const form = reactive({
  id: null,
  taskName: '',
  content: '',
  priority: 'medium',
  handlerId: null,
  status: 'todo',
  progress: 0,
  startTime: null,
  endTime: null
})

const rules = {
  taskName: [{ required: true, message: '请输入任务名称', trigger: 'blur' }]
}

// 详情
const detailVisible = ref(false)
const currentTask = ref(null)

// 优先级
function priorityType(p) {
  const map = { urgent: 'danger', high: 'danger', medium: 'warning', low: 'info' }
  return map[p] || 'info'
}
function priorityLabel(p) {
  const map = { urgent: '紧急', high: '高', medium: '中', low: '低' }
  return map[p] || '未知'
}
function statusType(s) {
  const map = { todo: 'info', in_progress: 'primary', testing: 'warning', completed: 'success', closed: 'info' }
  return map[s] || 'info'
}
function statusLabel(s) {
  const map = { todo: '待处理', in_progress: '进行中', testing: '测试中', completed: '已完成', closed: '已关闭' }
  return map[s] || '未知'
}

function formatDate(dateStr) {
  if (!dateStr) return '-'
  const d = new Date(dateStr)
  return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')} ${String(d.getHours()).padStart(2,'0')}:${String(d.getMinutes()).padStart(2,'0')}`
}

async function loadData() {
  loading.value = true
  try {
    const res = await getTaskList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      params: {
        taskName: searchForm.taskName || null,
        status: searchForm.status || null
      }
    })
    if (res.code === 200 && res.data) {
      tableData.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

async function loadUsers() {
  try {
    const res = await getUserList({ pageNum: 1, pageSize: 999 })
    if (res.code === 200 && res.data) {
      userList.value = res.data.records || []
    }
  } catch (error) {
    console.error(error)
  }
}

function resetSearch() {
  searchForm.taskName = ''
  searchForm.status = ''
  loadData()
}

function handleAdd() {
  router.push('/task/edit')
}

function handleEdit(row) {
  dialogTitle.value = '编辑任务'
  Object.assign(form, row)
  dialogVisible.value = true
}

async function handleView(row) {
  try {
    const res = await getTaskById(row.id)
    if (res.code === 200) {
      currentTask.value = res.data
      detailVisible.value = true
    }
  } catch (error) {
    ElMessage.error('获取任务详情失败')
  }
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  try {
    if (form.id) {
      await updateTask(form)
      ElMessage.success('更新成功')
    } else {
      await createTask(form)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error(form.id ? '更新失败' : '创建失败')
  }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确定要删除任务"${row.taskName}"吗？`, '提示', { type: 'warning' })
    await deleteTask(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('删除失败')
  }
}

onMounted(() => {
  loadData()
  loadUsers()
})
</script>

<style scoped lang="scss">
.task-all-page {
  .box-card {
    border-top: 3px solid #00a65a;
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .search-bar {
    margin-bottom: 15px;
    padding: 10px 15px;
    background: #f5f7fa;
    border-radius: 6px;
  }

  .pagination-wrap {
    margin-top: 20px;
    display: flex;
    justify-content: center;
  }

  .detail-header {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 20px;
    h3 { margin: 0; }
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
