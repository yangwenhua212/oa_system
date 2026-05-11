<template>
  <div class="attendance-monthly-page">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>考勤月报表</span>
          <el-form :inline="true" :model="searchForm" class="search-form">
            <el-form-item label="月份">
              <el-date-picker
                v-model="searchForm.month"
                type="month"
                placeholder="选择月份"
                format="YYYY年MM月"
                value-format="YYYY-MM"
                style="width: 200px"
                @change="handleSearch"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">查询</el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="realName" label="姓名" width="100" />
        <el-table-column prop="deptName" label="部门" width="120" />
        <el-table-column label="应出勤(天)" width="140">
          <template #default="{ row }">
            <el-input-number
              v-model="row.workDays"
              :min="1"
              :max="31"
              size="small"
              controls-position="right"
              :style="{ width: '80px' }"
            />
          </template>
        </el-table-column>
        <el-table-column label="实际出勤(天)" width="120">
          <template #default="{ row }">
            {{ row.actualDays || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="迟到次数" width="100">
          <template #default="{ row }">
            <span :class="{ 'text-danger': row.lateCount > 0 }">{{ row.lateCount }}</span>
          </template>
        </el-table-column>
        <el-table-column label="早退次数" width="100">
          <template #default="{ row }">
            <span :class="{ 'text-danger': row.earlyCount > 0 }">{{ row.earlyCount }}</span>
          </template>
        </el-table-column>
        <el-table-column label="缺勤天数" width="100">
          <template #default="{ row }">
            <span :class="{ 'text-danger': row.absentDays > 0 }">{{ row.absentDays }}</span>
          </template>
        </el-table-column>
        <el-table-column label="加班时长" width="100">
          <template #default="{ row }">
            {{ row.overtimeHours || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="请假天数" width="100">
          <template #default="{ row }">
            {{ row.leaveDays || '-' }}
          </template>
        </el-table-column>
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
import { ref, onMounted, watch } from 'vue'
import { getAttendanceStatistics, getAttendanceList } from '@/api/attendance/checkin'
import dayjs from 'dayjs'

const searchForm = ref({
  month: dayjs().format('YYYY-MM')
})

const loading = ref(false)
const pagination = ref({
  pageNum: 1,
  pageSize: 10,
  total: 0
})
const tableData = ref([])

function getMonthRange() {
  const m = searchForm.value.month
  if (!m) return {}
  const start = dayjs(m).startOf('month').format('YYYY-MM-DD')
  const end = dayjs(m).endOf('month').format('YYYY-MM-DD')
  return { start, end }
}

async function loadData() {
  loading.value = true
  try {
    const range = getMonthRange()
    if (!range.start) return

    // 获取该月份的所有考勤记录
    const res = await getAttendanceList({
      pageNum: pagination.value.pageNum,
      pageSize: pagination.value.pageSize,
      startDate: range.start,
      endDate: range.end
    })

    if (res.code === 200 && res.data) {
      const records = res.data.records || []
      // 按用户聚合统计
      const userMap = {}
      for (const r of records) {
        const key = r.realName || r.username
        if (!userMap[key]) {
          userMap[key] = {
            realName: r.realName,
            deptName: r.deptName,
            total: 0,
            lateCount: 0,
            earlyCount: 0,
            absentDays: 0,
            overtimeHours: 0,
            leaveDays: 0,
            actualDays: 0
          }
        }
        const u = userMap[key]
        u.total++
        if (r.clockInStatus === 'late') u.lateCount++
        if (r.clockOutStatus === 'early') u.earlyCount++
        if (r.clockInStatus === 'absent' || (!r.clockInTime && !r.clockOutTime)) u.absentDays++
        if (r.clockInTime) u.actualDays++
        if (r.overtimeDuration) u.overtimeHours += parseFloat(r.overtimeDuration)
        if (r.remark && r.remark.includes('请假')) u.leaveDays++
      }

      const WORKDAYS_KEY = 'attendance_workdays_' + (searchForm.value.month || dayjs().format('YYYY-MM'))
      const savedWorkDays = JSON.parse(localStorage.getItem(WORKDAYS_KEY) || '{}')
      tableData.value = Object.values(userMap).map(u => ({
        ...u,
        workDays: savedWorkDays[u.realName] || 22,
        overtimeHours: u.overtimeHours ? u.overtimeHours.toFixed(1) + 'h' : '-',
        leaveDays: u.leaveDays || '-'
      }))
      pagination.value.total = tableData.value.length
    }
  } catch (error) {
    console.error('加载考勤月报失败', error)
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  pagination.value.pageNum = 1
  loadData()
}

function handleReset() {
  searchForm.value.month = dayjs().format('YYYY-MM')
  pagination.value.pageNum = 1
  loadData()
}

// 监听应出勤天数变化，保存到 localStorage
watch(tableData, (val) => {
  const month = searchForm.value.month || dayjs().format('YYYY-MM')
  const WORKDAYS_KEY = 'attendance_workdays_' + month
  const obj = {}
  val.forEach(u => { if (u.workDays) obj[u.realName] = u.workDays })
  localStorage.setItem(WORKDAYS_KEY, JSON.stringify(obj))
}, { deep: true })

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.attendance-monthly-page {
  .box-card {
    border-top: 3px solid #00a65a;
  }
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-wrap: wrap;
    gap: 10px;
  }
  .search-form { margin-bottom: 0; }
  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
  .text-danger { color: #f56c6c; font-weight: bold; }
}
</style>
