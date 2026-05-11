<template>
  <div class="process-detail-page">
    <el-card class="detail-card">
      <template #header>
        <div class="card-header">
          <span class="header-title">流程详情</span>
          <el-button @click="goBack">返回</el-button>
        </div>
      </template>

      <el-descriptions :column="2" border v-if="process">
        <el-descriptions-item label="申请类型">{{ process.processName }}</el-descriptions-item>
        <el-descriptions-item label="标题">{{ process.title }}</el-descriptions-item>
        <el-descriptions-item label="申请人">{{ process.applicantName || process.applicant || '-' }}</el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ process.startTime ? dayjs(process.startTime).format('YYYY-MM-DD HH:mm') : '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag size="small" :type="process.status === 'approved' ? 'success' : process.status === 'rejected' ? 'danger' : 'warning'">
            {{ process.status === 'approved' ? '已通过' : process.status === 'rejected' ? '已拒绝' : '审批中' }}
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card class="timeline-card">
      <template #header>
        <span class="header-title">审批记录</span>
      </template>
      <el-timeline v-if="records.length > 0">
        <el-timeline-item
          v-for="(record, index) in records"
          :key="index"
          :timestamp="record.endTime ? dayjs(record.endTime).format('YYYY-MM-DD HH:mm') : '-'"
          :color="record.action === 'approve' ? '#00a65a' : record.action === 'reject' ? '#f56c6c' : '#909399'"
        >
          <div class="timeline-content">
            <div class="timeline-action">
              <el-tag size="small" :type="record.action === 'approve' ? 'success' : record.action === 'reject' ? 'danger' : 'info'">
                {{ record.action === 'approve' ? '通过' : record.action === 'reject' ? '拒绝' : '提交' }}
              </el-tag>
              <span class="approver-name">{{ record.approverName || '-' }}</span>
            </div>
            <div v-if="record.comment" class="timeline-comment">{{ record.comment }}</div>
          </div>
        </el-timeline-item>
      </el-timeline>
      <el-empty v-else description="暂无审批记录" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'
import { getProcessById, getApprovalRecords } from '@/api/process'

const route = useRoute()
const router = useRouter()
const process = ref(null)
const records = ref([])

async function loadDetail() {
  const id = route.query.id
  if (!id) {
    ElMessage.warning('参数错误')
    goBack()
    return
  }
  try {
    const [processRes, recordsRes] = await Promise.all([
      getProcessById(id),
      getApprovalRecords(id)
    ])
    if (processRes.code === 200) process.value = processRes.data
    if (recordsRes.code === 200) records.value = recordsRes.data || []
  } catch (e) {
    console.error('加载流程详情失败', e)
  }
}

function goBack() {
  router.back()
}

onMounted(() => loadDetail())
</script>

<style scoped lang="scss">
.process-detail-page {
  max-width: 800px;
  margin: 0 auto;

  .detail-card {
    border-top: 3px solid #00a65a;
    margin-bottom: 20px;
  }

  .timeline-card {
    border-top: 3px solid #00a65a;
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .header-title {
    font-weight: 600;
    font-size: 16px;
  }

  .timeline-content {
    .timeline-action {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-bottom: 4px;

      .approver-name {
        font-weight: 500;
        color: #333;
      }
    }

    .timeline-comment {
      color: #666;
      font-size: 13px;
      margin-top: 4px;
    }
  }
}
</style>
