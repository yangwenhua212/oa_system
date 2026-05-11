<template>
  <div class="task-page">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>我的任务</span>
        </div>
      </template>
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="title" label="任务标题" min-width="200" />
        <el-table-column prop="priority" label="优先级" width="100">
          <template #default="{ row }">
            <el-tag size="small" :type="row.priority === 'urgent' || row.priority === 'high' ? 'danger' : row.priority === 'medium' ? 'warning' : 'info'">
              {{ row.priority === 'urgent' ? '紧急' : row.priority === 'high' ? '高' : row.priority === 'medium' ? '中' : '低' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag size="small" :type="row.status === 'completed' ? 'success' : row.status === 'in_progress' ? 'primary' : 'info'">
              {{ row.status === 'completed' ? '已完成' : row.status === 'in_progress' ? '进行中' : row.status === 'todo' ? '待处理' : '待开始' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="截止日期" width="120">
          <template #default="{ row }">
            {{ row.endDate ? dayjs(row.endDate).format('YYYY-MM-DD') : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button type="primary" link @click="goTo(`/task/edit?id=${row.id}`)">编辑</el-button>
            <el-button v-if="row.status !== 'completed'" type="success" link @click="handleComplete(row)">完成</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination">
        <el-pagination v-model:current-page="pageNum" v-model:page-size="pageSize" :total="total" layout="total, prev, pager, next" @current-change="loadData" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'
import { getMyTasks, updateTaskStatus } from '@/api/task'

const router = useRouter()
const tableData = ref([])
const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

function goTo(path) { router.push(path) }

async function loadData() {
  loading.value = true
  try {
    const res = await getMyTasks()
    if (res.code === 200) {
      tableData.value = res.data || []
      total.value = res.data?.length || 0
    }
  } catch (error) {
    console.error('加载任务失败', error)
  } finally {
    loading.value = false
  }
}

async function handleComplete(row) {
  try {
    await updateTaskStatus(row.id, { status: 'completed', progress: 100 })
    ElMessage.success('任务已完成')
    loadData()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

onMounted(() => { loadData() })
</script>

<style scoped lang="scss">
.task-page {
  .box-card { border-top: 3px solid #00a65a; }
  .card-header { display: flex; justify-content: space-between; align-items: center; }
  .pagination { margin-top: 20px; display: flex; justify-content: flex-end; }
}
.btn-primary-green { background-color: #00a65a !important; border-color: #00a65a !important; color: #fff !important; }
</style>
