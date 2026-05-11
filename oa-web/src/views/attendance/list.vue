<template>
  <div class="attendance-list-page">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>考勤列表</span>
          <el-form :inline="true" :model="searchForm" class="search-form">
            <el-form-item label="日期">
              <el-date-picker
                v-model="searchForm.dateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                style="width: 240px"
              />
            </el-form-item>
            <el-form-item label="姓名">
              <el-input v-model="searchForm.realName" placeholder="请输入姓名" style="width: 140px" clearable />
            </el-form-item>
            <el-form-item label="状态">
              <el-select v-model="searchForm.clockInStatus" placeholder="请选择" clearable style="width: 120px">
                <el-option label="正常" value="normal" />
                <el-option label="迟到" value="late" />
                <el-option label="缺勤" value="absent" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">查询</el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="attendanceDate" label="日期" width="120">
          <template #default="{ row }">
            {{ formatDate(row.attendanceDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="realName" label="姓名" width="100" />
        <el-table-column prop="deptName" label="部门" width="120" />
        <el-table-column prop="clockInTime" label="上班打卡" width="160">
          <template #default="{ row }">
            <span :class="getStatusClass(row.clockInStatus)">
              {{ row.clockInTime ? formatTime(row.clockInTime) : '-' }}
              <el-tag v-if="row.clockInStatus" :type="row.clockInStatus === 'normal' ? 'success' : row.clockInStatus === 'late' ? 'danger' : 'warning'" size="small">
                {{ row.clockInStatus === 'normal' ? '正常' : row.clockInStatus === 'late' ? '迟到' : '缺勤' }}
              </el-tag>
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="clockOutTime" label="下班打卡" width="160">
          <template #default="{ row }">
            <span :class="getStatusClass(row.clockOutStatus)">
              {{ row.clockOutTime ? formatTime(row.clockOutTime) : '-' }}
              <el-tag v-if="row.clockOutStatus" :type="row.clockOutStatus === 'normal' ? 'success' : 'warning'" size="small">
                {{ row.clockOutStatus === 'normal' ? '正常' : '早退' }}
              </el-tag>
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="workDuration" label="工作时长" width="100">
          <template #default="{ row }">
            {{ row.workDuration ? row.workDuration + '小时' : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAttendanceList } from '@/api/attendance/checkin'
import dayjs from 'dayjs'

const searchForm = ref({
  dateRange: [],
  realName: '',
  clockInStatus: ''
})

const loading = ref(false)
const pagination = ref({
  pageNum: 1,
  pageSize: 10,
  total: 0
})
const tableData = ref([])

const formatDate = (d) => d ? dayjs(d).format('YYYY-MM-DD') : '-'
const formatTime = (t) => t ? dayjs(t).format('HH:mm') : '-'

const getStatusClass = (s) => {
  const map = { normal: 'text-normal', late: 'text-late', early: 'text-early', absent: 'text-absent' }
  return map[s] || ''
}

async function loadData() {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.value.pageNum,
      pageSize: pagination.value.pageSize,
      startDate: searchForm.value.dateRange?.[0] || '',
      endDate: searchForm.value.dateRange?.[1] || '',
      realName: searchForm.value.realName || '',
      clockInStatus: searchForm.value.clockInStatus || ''
    }
    const res = await getAttendanceList(params)
    if (res.code === 200 && res.data) {
      tableData.value = res.data.records || []
      pagination.value.total = res.data.total || 0
    }
  } catch (error) {
    console.error('加载考勤列表失败', error)
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  pagination.value.pageNum = 1
  loadData()
}

function handleReset() {
  searchForm.value = { dateRange: [], realName: '', clockInStatus: '' }
  pagination.value.pageNum = 1
  loadData()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.attendance-list-page {
  .box-card {
    border-top: 3px solid #00a65a;
  }
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    flex-wrap: wrap;
    gap: 10px;
  }
  .search-form { margin-bottom: 0; }
  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
  .text-normal { color: #67c23a; }
  .text-late { color: #f56c6c; }
  .text-early { color: #e6a23c; }
  .text-absent { color: #909399; }
}
</style>
