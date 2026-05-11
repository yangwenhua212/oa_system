<template>
  <div class="leave-page">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>请假申请</span>
          <el-button type="primary" class="btn-primary-green" @click="handleCreate">
            <el-icon><Plus /></el-icon>
            新建申请
          </el-button>
        </div>
      </template>
      <el-table :data="tableData" stripe border>
        <el-table-column prop="leaveType" label="请假类型" width="120">
          <template #default="{ row }">
            {{ getLeaveTypeLabel(row.leaveType) }}
          </template>
        </el-table-column>
        <el-table-column prop="startDate" label="开始日期" width="120" />
        <el-table-column prop="endDate" label="结束日期" width="120" />
        <el-table-column prop="days" label="天数" width="80" />
        <el-table-column prop="reason" label="请假原因" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'approved' ? 'success' : row.status === 'rejected' ? 'danger' : 'warning'" size="small">
              {{ row.status === 'approved' ? '已通过' : row.status === 'rejected' ? '已拒绝' : '审批中' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getMyProcessList } from '@/api/process'

const router = useRouter()

const tableData = ref([])
const loading = ref(false)

const leaveTypeMap = {
  annual: '年假',
  personal: '事假',
  sick: '病假',
  marriage: '婚假',
  maternity: '产假',
  funeral: '丧假',
  other: '其他'
}

function getLeaveTypeLabel(type) {
  return leaveTypeMap[type] || type || '未知'
}

function handleCreate() {
  router.push('/attendance/leave-form')
}

const statusMap = {
  pending: '审批中',
  approved: '已通过',
  rejected: '已拒绝'
}

async function loadData() {
  loading.value = true
  try {
    const res = await getMyProcessList()
    if (res.code === 200) {
      // 只显示请假申请
      const allData = res.data || []
      tableData.value = allData
        .filter(item => item.businessType === 'leave')
        .map(item => {
          let form = {}
          try {
            form = typeof item.formData === 'string' ? JSON.parse(item.formData) : (item.formData || {})
          } catch {}
          return {
            id: item.id,
            leaveType: form.leaveType || '',
            startDate: form.startDate || '-',
            endDate: form.endDate || '-',
            days: item.days || form.days || '-',
            reason: form.reason || '-',
            status: item.status || 'pending',
            title: item.title,
            processName: item.processName,
            startTime: item.startTime
          }
        })
    }
  } catch (error) {
    console.error('加载请假列表失败:', error)
    ElMessage.error('加载请假列表失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => loadData())
</script>

<style scoped lang="scss">
.leave-page {
  .box-card {
    border-top: 3px solid #00a65a;
  }
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
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
