<template>
  <div class="checkin-page">
    <el-row :gutter="20">
      <!-- 打卡卡片 -->
      <el-col :span="8">
        <el-card class="box-card clock-card">
          <template #header>
            <div class="card-header">
              <span>签到打卡</span>
            </div>
          </template>
          <div class="clock-content">
            <!-- 时钟显示 -->
            <div class="clock-display">
              <div class="time">{{ currentTime }}</div>
              <div class="date">{{ currentDate }}</div>
              <div class="weekday">{{ currentWeekday }}</div>
            </div>

            <!-- 打卡状态 -->
            <div class="clock-status">
              <div class="status-item">
                <span class="label">上班打卡</span>
                <span class="value" :class="clockInStatus.class">
                  {{ clockInStatus.text }}
                </span>
              </div>
              <div class="status-item">
                <span class="label">下班打卡</span>
                <span class="value" :class="clockOutStatus.class">
                  {{ clockOutStatus.text }}
                </span>
              </div>
            </div>

            <!-- 打卡按钮 -->
            <div class="clock-buttons">
              <el-button
                type="primary"
                size="large"
                class="btn-clock-in"
                :disabled="!canClockIn"
                @click="handleClockIn"
                :loading="clockInLoading"
              >
                <el-icon class="btn-icon"><Clock /></el-icon>
                上班打卡
              </el-button>
              <el-button
                type="success"
                size="large"
                class="btn-clock-out"
                :disabled="!canClockOut"
                @click="handleClockOut"
                :loading="clockOutLoading"
              >
                <el-icon class="btn-icon"><CircleCheck /></el-icon>
                下班打卡
              </el-button>
            </div>

            <!-- 工作时长 -->
            <div class="work-duration" v-if="todayData.clockInTime && todayData.clockOutTime">
              <span class="label">今日工作时長</span>
              <span class="value">{{ todayData.workDuration }} 小時</span>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 考勤记录 -->
      <el-col :span="16">
        <el-card class="box-card">
          <template #header>
            <div class="card-header">
              <span>考勤记录</span>
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

          <el-table :data="tableData" stripe border v-loading="loading">
            <el-table-column prop="attendanceDate" label="日期" width="120">
              <template #default="{ row }">
                {{ formatDate(row.attendanceDate) }}
              </template>
            </el-table-column>
            <el-table-column prop="clockInTime" label="上班打卡" width="160">
              <template #default="{ row }">
                <span :class="getStatusClass(row.clockInStatus)">
                  {{ row.clockInTime ? formatTime(row.clockInTime) : '-' }}
                  <el-tag v-if="row.clockInStatus" :type="getClockInTagType(row.clockInStatus)" size="small">
                    {{ getStatusText(row.clockInStatus) }}
                  </el-tag>
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="clockOutTime" label="下班打卡" width="160">
              <template #default="{ row }">
                <span :class="getStatusClass(row.clockOutStatus)">
                  {{ row.clockOutTime ? formatTime(row.clockOutTime) : '-' }}
                  <el-tag v-if="row.clockOutStatus" :type="getClockOutTagType(row.clockOutStatus)" size="small">
                    {{ getStatusText(row.clockOutStatus) }}
                  </el-tag>
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="workDuration" label="工作时长" width="100">
              <template #default="{ row }">
                {{ row.workDuration ? row.workDuration + '小时' : '-' }}
              </template>
            </el-table-column>
            <el-table-column prop="overtimeDuration" label="加班时长" width="100">
              <template #default="{ row }">
                {{ row.overtimeDuration ? row.overtimeDuration + '小时' : '-' }}
              </template>
            </el-table-column>
            <el-table-column prop="remark" label="备注" show-overflow-tooltip />
          </el-table>

          <!-- 分页 -->
          <div class="pagination">
            <el-pagination
              v-model:current-page="pagination.pageNum"
              v-model:page-size="pagination.pageSize"
              :page-sizes="[10, 20, 50]"
              :total="pagination.total"
              layout="total, prev, pager, next"
              @current-change="loadData"
            />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="statistics-row">
      <el-col :span="6">
        <el-card class="stat-card stat-normal">
          <div class="stat-content">
            <div class="stat-value">{{ statistics.normal }}</div>
            <div class="stat-label">正常</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card stat-late">
          <div class="stat-content">
            <div class="stat-value">{{ statistics.late }}</div>
            <div class="stat-label">迟到</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card stat-early">
          <div class="stat-content">
            <div class="stat-value">{{ statistics.early }}</div>
            <div class="stat-label">早退</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card stat-total">
          <div class="stat-content">
            <div class="stat-value">{{ statistics.total }}</div>
            <div class="stat-label">总天数</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Clock, CircleCheck } from '@element-plus/icons-vue'
import { getTodayAttendance, clockIn, clockOut, getAttendanceList, getAttendanceStatistics } from '@/api/attendance/checkin'
import dayjs from 'dayjs'

// 当前时间
const currentTime = ref('')
const currentDate = ref('')
const currentWeekday = ref('')
let timeInterval = null

// 今日考勤数据
const todayData = ref({})

// 打卡状态
const clockInStatus = computed(() => {
  if (todayData.value.clockInTime) {
    const status = todayData.value.clockInStatus
    if (status === 'late') return { text: '迟到', class: 'status-late' }
    return { text: formatTime(todayData.value.clockInTime), class: 'status-normal' }
  }
  return { text: '未打卡', class: 'status-pending' }
})

const clockOutStatus = computed(() => {
  if (todayData.value.clockOutTime) {
    const status = todayData.value.clockOutStatus
    if (status === 'early') return { text: '早退', class: 'status-early' }
    return { text: formatTime(todayData.value.clockOutTime), class: 'status-normal' }
  }
  return { text: '未打卡', class: 'status-pending' }
})

// 打卡按钮状态
const canClockIn = computed(() => !todayData.value.clockInTime)
const canClockOut = computed(() => todayData.value.clockInTime && !todayData.value.clockOutTime)

// 加载状态
const clockInLoading = ref(false)
const clockOutLoading = ref(false)

// 表格数据
const tableData = ref([])
const loading = ref(false)
const pagination = ref({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 搜索表单
const searchForm = ref({
  dateRange: []
})

// 统计数据
const statistics = ref({
  total: 0,
  normal: 0,
  late: 0,
  early: 0
})

// 更新时钟
const updateClock = () => {
  const now = dayjs()
  currentTime.value = now.format('HH:mm:ss')
  currentDate.value = now.format('YYYY年MM月DD日')
  currentWeekday.value = now.format('dddd')
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return '-'
  return dayjs(date).format('YYYY-MM-DD')
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return '-'
  return dayjs(time).format('HH:mm')
}

// 获取状态文本
const getStatusText = (status) => {
  const map = {
    normal: '正常',
    late: '迟到',
    early: '早退',
    absent: '缺勤'
  }
  return map[status] || ''
}

// 获取状态样式
const getStatusClass = (status) => {
  const map = {
    normal: 'text-normal',
    late: 'text-late',
    early: 'text-early',
    absent: 'text-absent'
  }
  return map[status] || ''
}

// 获取上班打卡标签类型
const getClockInTagType = (status) => {
  const map = {
    normal: 'success',
    late: 'danger',
    absent: 'warning'
  }
  return map[status] || 'info'
}

// 获取下班打卡标签类型
const getClockOutTagType = (status) => {
  const map = {
    normal: 'success',
    early: 'warning',
    absent: 'danger'
  }
  return map[status] || 'info'
}

// 加载今日考勤
const loadTodayAttendance = async () => {
  try {
    const res = await getTodayAttendance()
    todayData.value = res.data || {}
  } catch (error) {
    console.error('获取今日考勤失败', error)
    todayData.value = {}
  }
}

// 加载考勤列表
const loadData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.value.pageNum,
      pageSize: pagination.value.pageSize,
      startDate: searchForm.value.dateRange?.[0] || '',
      endDate: searchForm.value.dateRange?.[1] || ''
    }
    const res = await getAttendanceList(params)
    tableData.value = res.data?.records || []
    pagination.value.total = res.data?.total || 0
  } catch (error) {
    console.error('加载考勤记录失败', error)
  } finally {
    loading.value = false
  }
}

// 加载统计数据
const loadStatistics = async () => {
  try {
    const today = dayjs()
    const startDate = today.startOf('month').format('YYYY-MM-DD')
    const endDate = today.endOf('month').format('YYYY-MM-DD')
    const res = await getAttendanceStatistics({ startDate, endDate })
    statistics.value = {
      total: res.data?.total || 0,
      normal: res.data?.normal || 0,
      late: res.data?.late || 0,
      early: res.data?.early || 0
    }
  } catch (error) {
    console.error('加载统计数据失败', error)
  }
}

// 上班打卡
const handleClockIn = async () => {
  clockInLoading.value = true
  try {
    const res = await clockIn()
    todayData.value = res.data || {}
    ElMessage.success('上班打卡成功')
    loadData()
    loadStatistics()
  } catch (error) {
    ElMessage.error(error.message || '打卡失败')
  } finally {
    clockInLoading.value = false
  }
}

// 下班打卡
const handleClockOut = async () => {
  clockOutLoading.value = true
  try {
    const res = await clockOut()
    todayData.value = res.data || {}
    ElMessage.success('下班打卡成功')
    loadData()
    loadStatistics()
  } catch (error) {
    ElMessage.error(error.message || '打卡失败')
  } finally {
    clockOutLoading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.value.pageNum = 1
  loadData()
}

// 重置
const handleReset = () => {
  searchForm.value.dateRange = []
  pagination.value.pageNum = 1
  loadData()
}

// 初始化
onMounted(() => {
  updateClock()
  timeInterval = setInterval(updateClock, 1000)
  loadTodayAttendance()
  loadData()
  loadStatistics()
})

onUnmounted(() => {
  if (timeInterval) {
    clearInterval(timeInterval)
  }
})
</script>

<style scoped lang="scss">
.checkin-page {
  .box-card {
    border-top: 3px solid #00a65a;
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .search-form {
    margin-bottom: 0;
  }
}

// 打卡卡片
.clock-card {
  .clock-content {
    text-align: center;
  }

  .clock-display {
    padding: 20px 0;
    border-bottom: 1px solid #eee;
    margin-bottom: 20px;

    .time {
      font-size: 48px;
      font-weight: bold;
      color: #333;
      font-family: 'Arial', sans-serif;
    }

    .date {
      font-size: 16px;
      color: #666;
      margin-top: 8px;
    }

    .weekday {
      font-size: 14px;
      color: #999;
      margin-top: 4px;
    }
  }

  .clock-status {
    display: flex;
    justify-content: space-around;
    margin-bottom: 30px;
    padding: 15px 0;
    background: #f8f9fa;
    border-radius: 8px;

    .status-item {
      text-align: center;

      .label {
        display: block;
        font-size: 14px;
        color: #999;
        margin-bottom: 8px;
      }

      .value {
        font-size: 16px;
        font-weight: 500;
      }

      .status-normal {
        color: #67c23a;
      }

      .status-late {
        color: #f56c6c;
      }

      .status-early {
        color: #e6a23c;
      }

      .status-pending {
        color: #909399;
      }
    }
  }

  .clock-buttons {
    display: flex;
    flex-direction: column;
    gap: 15px;

    .el-button {
      width: 100%;
      height: 50px;
      font-size: 16px;
      border-radius: 25px;

      .btn-icon {
        margin-right: 8px;
      }
    }

    .btn-clock-in {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border: none;

      &:hover:not(:disabled) {
        background: linear-gradient(135deg, #5a6fd6 0%, #6a4190 100%);
      }

      &:disabled {
        background: #ccc;
        border-color: #ccc;
      }
    }

    .btn-clock-out {
      background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
      border: none;

      &:hover:not(:disabled) {
        background: linear-gradient(135deg, #0f8a80 0%, #32d970 100%);
      }

      &:disabled {
        background: #ccc;
        border-color: #ccc;
      }
    }
  }

  .work-duration {
    margin-top: 20px;
    padding: 15px;
    background: #f0f9ff;
    border-radius: 8px;
    display: flex;
    justify-content: space-between;
    align-items: center;

    .label {
      color: #666;
    }

    .value {
      font-size: 20px;
      font-weight: bold;
      color: #00a65a;
    }
  }
}

// 分页
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

// 统计卡片
.statistics-row {
  margin-top: 20px;
}

.stat-card {
  .stat-content {
    text-align: center;
    padding: 10px 0;

    .stat-value {
      font-size: 32px;
      font-weight: bold;
      margin-bottom: 5px;
    }

    .stat-label {
      font-size: 14px;
      color: #666;
    }
  }
}

.stat-normal {
  border-top: 3px solid #67c23a;

  .stat-value {
    color: #67c23a;
  }
}

.stat-late {
  border-top: 3px solid #f56c6c;

  .stat-value {
    color: #f56c6c;
  }
}

.stat-early {
  border-top: 3px solid #e6a23c;

  .stat-value {
    color: #e6a23c;
  }
}

.stat-total {
  border-top: 3px solid #409eff;

  .stat-value {
    color: #409eff;
  }
}

// 状态文字颜色
.text-normal {
  color: #67c23a;
}

.text-late {
  color: #f56c6c;
}

.text-early {
  color: #e6a23c;
}

.text-absent {
  color: #909399;
}
</style>
