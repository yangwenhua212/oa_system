<template>
  <div class="attendance-weekly-page">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>考勤周报表</span>
          <el-form :inline="true" :model="searchForm" class="search-form">
            <el-form-item label="周次">
              <el-date-picker
                v-model="searchForm.weekRange"
                type="week"
                placeholder="选择周"
                format="YYYY年第WW周"
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

      <!-- 周次标题 -->
      <div class="week-title" v-if="weekInfoText">
        <el-alert :title="weekInfoText" type="info" :closable="false" show-icon />
      </div>

      <!-- 异常统计 -->
      <div class="anomaly-summary" v-if="anomalyStats.length > 0">
        <el-card shadow="never" class="anomaly-card">
          <template #header>
            <span class="anomaly-title">本周异常统计</span>
          </template>
          <el-row :gutter="16">
            <el-col :span="6" v-for="stat in anomalyStats" :key="stat.label">
              <div class="anomaly-stat-item" :class="stat.type">
                <span class="stat-label">{{ stat.label }}</span>
                <span class="stat-count">{{ stat.count }}</span>
                <span class="stat-unit">人次</span>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </div>

      <!-- 周报表：人员 x 天 交叉表 -->
      <el-table
        :data="tableData"
        v-loading="loading"
        stripe
        border
        :span-method="deptSpanMethod"
        :header-cell-style="headerCellStyle"
        class="weekly-table"
      >
        <el-table-column type="index" label="序号" width="55" fixed="left" />
        <el-table-column prop="deptName" label="部门" width="110" fixed="left">
          <template #default="{ row }">
            <el-tag type="info" effect="plain" size="small">{{ row.deptName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="realName" label="姓名" width="90" fixed="left">
          <template #default="{ row }">
            <span class="name-cell">{{ row.realName }}</span>
          </template>
        </el-table-column>

        <!-- 周一 ~ 周日 -->
        <el-table-column
          v-for="(day, idx) in weekColumns"
          :key="idx"
          :label="day.label"
          :width="day.width"
          align="center"
        >
          <template #default="{ row }">
            <div class="day-cell" :class="getDayCellClass(row, day.key)">
              <!-- 判断是否是周末 -->
              <template v-if="day.isWeekend">
                <span class="weekend-text">休息</span>
              </template>
              <template v-else>
                <template v-if="row[day.key]">
                  <div class="clock-time">
                    <el-tooltip content="签到时间" placement="top">
                      <span class="time-label">签</span>
                    </el-tooltip>
                    <span :class="getClockInClass(row[day.key].clockInStatus)">
                      {{ formatTime(row[day.key].clockInTime) }}
                    </span>
                  </div>
                  <div class="clock-time">
                    <el-tooltip content="签退时间" placement="top">
                      <span class="time-label">退</span>
                    </el-tooltip>
                    <span :class="getClockOutClass(row[day.key].clockOutStatus)">
                      {{ formatTime(row[day.key].clockOutTime) }}
                    </span>
                  </div>
                  <div class="work-duration" v-if="row[day.key].workDuration">
                    <el-icon><Timer /></el-icon>
                    {{ row[day.key].workDuration }}h
                  </div>
                  <div class="day-status-tags">
                    <el-tag
                      v-if="row[day.key].clockInStatus === 'late'"
                      type="danger"
                      size="small"
                      effect="dark"
                    >迟到</el-tag>
                    <el-tag
                      v-if="row[day.key].clockOutStatus === 'early'"
                      type="warning"
                      size="small"
                      effect="dark"
                    >早退</el-tag>
                    <el-tag
                      v-if="row[day.key].clockInStatus === 'absent'"
                      type="info"
                      size="small"
                      effect="dark"
                    >缺勤</el-tag>
                  </div>
                </template>
                <template v-else>
                  <span class="no-record">-</span>
                </template>
              </template>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getAttendanceList } from '@/api/attendance/checkin'
import { Timer } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import isoWeek from 'dayjs/plugin/isoWeek'
import 'dayjs/locale/zh-cn'

dayjs.extend(isoWeek)
dayjs.locale('zh-cn')

const searchForm = ref({
  weekRange: new Date()
})

const loading = ref(false)
const tableData = ref([])
const weekDays = ref([])

// 周次信息文字
const weekInfoText = computed(() => {
  if (weekDays.value.length === 0) return ''
  const start = weekDays.value[0]
  const end = weekDays.value[weekDays.value.length - 1]
  return `统计周期：${start.fullDate}（${start.weekday}） ~ ${end.fullDate}（${end.weekday}）`
})

// 周列定义
const weekColumns = computed(() => {
  return weekDays.value.map(d => ({
    label: `${d.weekday}\n${d.date}`,
    key: d.key,
    width: 140,
    isWeekend: d.isWeekend
  }))
})

// 异常统计
const anomalyStats = computed(() => {
  let lateCount = 0
  let earlyCount = 0
  let absentCount = 0
  for (const row of tableData.value) {
    for (const day of weekDays.value) {
      const record = row[day.key]
      if (record) {
        if (record.clockInStatus === 'late') lateCount++
        if (record.clockOutStatus === 'early') earlyCount++
        if (record.clockInStatus === 'absent') absentCount++
      }
    }
  }
  const stats = []
  if (lateCount > 0) stats.push({ label: '迟到', count: lateCount, type: 'late' })
  if (earlyCount > 0) stats.push({ label: '早退', count: earlyCount, type: 'early' })
  if (absentCount > 0) stats.push({ label: '缺勤', count: absentCount, type: 'absent' })
  return stats
})

function formatDate(d) {
  return d ? dayjs(d).format('YYYY-MM-DD') : '-'
}

function formatTime(t) {
  return t ? dayjs(t).format('HH:mm') : '-'
}

function getClockInClass(status) {
  if (status === 'late') return 'clock-warn'
  if (status === 'normal') return 'clock-ok'
  return 'clock-miss'
}

function getClockOutClass(status) {
  if (status === 'normal') return 'clock-ok'
  if (status === 'early') return 'clock-warn'
  return 'clock-miss'
}

function getDayCellClass(row, key) {
  const record = row[key]
  if (!record) return ''
  if (record.clockInStatus === 'late' || record.clockOutStatus === 'early') return 'cell-anomaly'
  if (record.clockInStatus === 'absent') return 'cell-absent'
  return 'cell-normal'
}

// 计算周一到周日
function calcWeekDays(date) {
  const d = dayjs(date)
  const startOfWeek = d.startOf('isoWeek') // 周一开始
  const days = []
  const weekdayMap = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
  for (let i = 0; i < 7; i++) {
    const day = startOfWeek.add(i, 'day')
    days.push({
      label: `${weekdayMap[i]}\n${day.format('MM/DD')}`,
      key: `day${i}`,
      date: day.format('D'),
      fullDate: day.format('YYYY-MM-DD'),
      weekday: weekdayMap[i],
      isWeekend: i >= 5,
      isoDate: day.format('YYYY-MM-DD')
    })
  }
  return days
}

async function loadData() {
  loading.value = true
  try {
    // 计算当前选择的周的起止日期
    const selectedWeek = searchForm.value.weekRange || new Date()
    weekDays.value = calcWeekDays(selectedWeek)
    const startDate = weekDays.value[0].isoDate
    const endDate = weekDays.value[6].isoDate

    // 分页设置：获取该周所有记录（不分页）
    const params = {
      pageNum: 1,
      pageSize: 9999,
      startDate,
      endDate
    }
    const res = await getAttendanceList(params)
    if (res.code === 200 && res.data) {
      const records = res.data.records || []

      // 按用户分组
      const userMap = {}
      for (const r of records) {
        const key = r.userId || r.realName
        if (!userMap[key]) {
          userMap[key] = {
            realName: r.realName,
            deptName: r.deptName || '-',
            deptId: r.deptId,
            records: {}
          }
        }
        // 将记录关联到对应的日期
        if (r.attendanceDate) {
          const dateStr = dayjs(r.attendanceDate).format('YYYY-MM-DD')
          userMap[key].records[dateStr] = r
        }
      }

      // 构建交叉表数据：每个用户一行，day0~day6 为列
      const result = []
      for (const [userId, user] of Object.entries(userMap)) {
        const row = {
          realName: user.realName,
          deptName: user.deptName,
          deptId: user.deptId,
          _userId: userId
        }
        for (const day of weekDays.value) {
          row[day.key] = user.records[day.isoDate] || null
        }
        result.push(row)
      }

      // 按部门排序
      result.sort((a, b) => {
        if (a.deptName !== b.deptName) return a.deptName.localeCompare(b.deptName, 'zh')
        return a.realName.localeCompare(b.realName, 'zh')
      })

      tableData.value = result
    } else {
      tableData.value = []
    }
  } catch (error) {
    console.error('加载考勤周报失败', error)
  } finally {
    loading.value = false
  }
}

// 部门合并
function deptSpanMethod({ row, column, rowIndex, columnIndex }) {
  if (columnIndex === 1) {
    // 部门列：相同部门合并
    const dept = row.deptName
    let rowspan = 1
    if (rowIndex > 0 && dept === tableData.value[rowIndex - 1]?.deptName) {
      return { rowspan: 0, colspan: 0 }
    }
    for (let i = rowIndex + 1; i < tableData.value.length; i++) {
      if (tableData.value[i].deptName === dept) {
        rowspan++
      } else {
        break
      }
    }
    return { rowspan, colspan: 1 }
  }
}

function headerCellStyle({ columnIndex }) {
  if (columnIndex >= 3) {
    return {
      backgroundColor: '#f5f7fa',
      fontWeight: 'bold',
      whiteSpace: 'pre-line',
      lineHeight: '1.4'
    }
  }
}

function handleSearch() {
  loadData()
}

function handleReset() {
  searchForm.value.weekRange = new Date()
  loadData()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.attendance-weekly-page {
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

  .week-title {
    margin-bottom: 16px;
  }

  // 异常统计
  .anomaly-summary {
    margin-bottom: 16px;
    .anomaly-card {
      border: 1px solid #ebeef5;
      :deep(.el-card__header) {
        padding: 10px 16px;
        background: #fafafa;
      }
      .anomaly-title {
        font-size: 14px;
        font-weight: 600;
        color: #303133;
      }
    }
    .anomaly-stat-item {
      text-align: center;
      padding: 12px 0;
      border-radius: 8px;
      background: #f8f9fa;
      transition: all 0.2s;
      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 2px 12px rgba(0,0,0,0.08);
      }
      &.late {
        border-left: 3px solid #f56c6c;
        .stat-count { color: #f56c6c; }
      }
      &.early {
        border-left: 3px solid #e6a23c;
        .stat-count { color: #e6a23c; }
      }
      &.absent {
        border-left: 3px solid #909399;
        .stat-count { color: #909399; }
      }
      .stat-label {
        display: block;
        font-size: 13px;
        color: #666;
        margin-bottom: 4px;
      }
      .stat-count {
        font-size: 28px;
        font-weight: bold;
      }
      .stat-unit {
        font-size: 12px;
        color: #999;
        margin-left: 2px;
      }
    }
  }

  // 周报表表格
  .weekly-table {
    :deep(th) {
      background: #f5f7fa;
    }
    :deep(td) {
      padding: 6px 0;
    }
  }

  .name-cell {
    font-weight: 500;
    color: #303133;
  }

  .day-cell {
    padding: 4px;
    min-height: 60px;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 3px;

    .clock-time {
      font-size: 12px;
      color: #606266;
      display: flex;
      align-items: center;
      gap: 4px;
      width: 100%;
      justify-content: center;

      .time-label {
        display: inline-flex;
        align-items: center;
        justify-content: center;
        width: 16px;
        height: 16px;
        font-size: 10px;
        border-radius: 3px;
        background: #eee;
        color: #666;
        flex-shrink: 0;
      }
    }

    .work-duration {
      font-size: 11px;
      color: #409eff;
      display: flex;
      align-items: center;
      gap: 2px;
      .el-icon {
        font-size: 12px;
      }
    }

    .clock-ok { color: #67c23a; font-weight: 500; }
    .clock-warn { color: #f56c6c; font-weight: 500; }
    .clock-miss { color: #909399; }

    .day-status-tags {
      display: flex;
      gap: 2px;
      flex-wrap: wrap;
      justify-content: center;
      :deep(.el-tag) {
        font-size: 10px;
        padding: 0 4px;
        height: 18px;
        line-height: 18px;
      }
    }

    .weekend-text {
      color: #c0c4cc;
      font-size: 12px;
    }

    .no-record {
      color: #dcdfe6;
      font-size: 14px;
    }
  }

  .cell-anomaly {
    background: #fef0f0;
  }
  .cell-absent {
    background: #f4f4f5;
  }
  .cell-normal {
    background: #f0f9eb;
  }

  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}
</style>
