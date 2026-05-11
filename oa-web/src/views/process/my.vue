<template>
  <div class="process-my-page">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>我的申请</span>
          <el-button type="primary" class="btn-primary-green" @click="goTo('/process/new')">
            <el-icon><Plus /></el-icon>新建申请
          </el-button>
        </div>
      </template>
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="processName" label="申请类型" width="120" />
        <el-table-column prop="title" label="标题" min-width="200" />
        <el-table-column label="申请时间" width="180">
          <template #default="{ row }">{{ row.startTime ? dayjs(row.startTime).format('YYYY-MM-DD HH:mm') : '-' }}</template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag size="small" :type="row.status === 'approved' ? 'success' : row.status === 'rejected' ? 'danger' : 'warning'">
              {{ row.status === 'approved' ? '已通过' : row.status === 'rejected' ? '已拒绝' : '审批中' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="80" fixed="right">
          <template #default="{ row }">
            <el-link type="primary" :underline="false" @click="router.push(`/process/detail?id=${row.id}`)">查看</el-link>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import dayjs from 'dayjs'
import { getMyProcessList } from '@/api/process'

const router = useRouter()
const tableData = ref([])
const loading = ref(false)

function goTo(path) { router.push(path) }

async function loadData() {
  loading.value = true
  try {
    const res = await getMyProcessList()
    if (res.code === 200) tableData.value = res.data || []
  } catch (e) { console.error('加载失败', e) }
  finally { loading.value = false }
}

onMounted(() => loadData())
</script>

<style scoped lang="scss">
.process-my-page {
  .box-card { border-top: 3px solid #00a65a; }
  .card-header { display: flex; justify-content: space-between; align-items: center; }
}
.btn-primary-green { background-color: #00a65a !important; border-color: #00a65a !important; color: #fff !important; }
</style>
