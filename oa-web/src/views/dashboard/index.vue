<template>
  <div class="dashboard">
    <!-- 顶部功能卡片 -->
    <el-row :gutter="15">
      <el-col :span="6">
        <div class="shortcut-card green" @click="goTo('/attendance/checkin')">
          <div class="card-left">
            <div class="shortcut-icon"><Clock /></div>
            <div class="shortcut-info">
              <div class="time">{{ currentTime }}</div>
              <div class="label">{{ currentDate }} {{ currentWeekday }}</div>
            </div>
          </div>
          <div class="card-right">
            <el-button
              type="success"
              size="small"
              round
              :disabled="isCheckedIn"
              @click.stop="handleClockIn"
              :loading="clockInLoading"
            >
              {{ isCheckedIn ? '已签到' : '签到打卡' }}
            </el-button>
            <el-tag size="small" color="#ffffff" text-color="#00a65a" v-if="isCheckedIn" style="margin-top:4px">
              签到 {{ checkInTime }}
            </el-tag>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="shortcut-card blue" @click="goTo('/file/list')">
          <div class="shortcut-left">
            <div class="shortcut-icon"><Folder /></div>
            <div class="shortcut-text">
              <div class="num">{{ stats.totalFiles }}</div>
              <div class="label">文件总数</div>
            </div>
          </div>
          <div class="shortcut-arrow"><el-icon><ArrowRight /></el-icon></div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="shortcut-card purple" @click="goTo('/address/list')">
          <div class="shortcut-left">
            <div class="shortcut-icon"><User /></div>
            <div class="shortcut-text">
              <div class="num">{{ stats.addressCount }}</div>
              <div class="label">通讯录</div>
            </div>
          </div>
          <div class="shortcut-arrow"><el-icon><ArrowRight /></el-icon></div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="shortcut-card orange" @click="goTo('/chat')">
          <div class="shortcut-left">
            <div class="shortcut-icon"><ChatDotRound /></div>
            <div class="shortcut-text">
              <div class="num">{{ stats.chatCount }}</div>
              <div class="label">讨论区</div>
            </div>
          </div>
          <div class="shortcut-arrow"><el-icon><ArrowRight /></el-icon></div>
        </div>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="15" style="margin-top:15px">
      <el-col :span="16">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>本周系统使用量统计</span>
              <el-date-picker
                v-model="chartWeek"
                type="week"
                format="YYYY年第WW周"
                style="width:160px"
                size="small"
              />
            </div>
          </template>
          <div ref="lineChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>任务完成排行</span>
            </div>
          </template>
          <div ref="barChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 公告通知 + 行事历 -->
    <el-row :gutter="15" style="margin-top:15px">
      <el-col :span="12">
        <el-card class="data-card">
          <template #header>
            <div class="card-header">
              <span>公告通知</span>
              <el-button link type="primary" @click="goTo('/notice/list')">更多</el-button>
            </div>
          </template>
          <el-table :data="notices" stripe style="width:100%">
            <el-table-column prop="title" label="标题" min-width="160" show-overflow-tooltip />
            <el-table-column prop="publisher" label="发布人" width="80" />
            <el-table-column prop="publishTime" label="发布时间" width="100" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="data-card">
          <template #header>
            <div class="card-header">
              <span>行事历</span>
              <el-button link type="primary" @click="goTo('/schedule/list')">更多</el-button>
            </div>
          </template>
          <el-calendar v-model="calendarDate">
            <template #date-cell="{ data }">
              <div class="calendar-cell">
                <span class="calendar-day">{{ data.day.split('-')[2] }}</span>
                <span v-if="hasEvent(data.day)" class="event-dot"></span>
              </div>
            </template>
          </el-calendar>
        </el-card>
      </el-col>
    </el-row>

    <!-- 流程管理 + 工作计划 + 我的便签 -->
    <el-row :gutter="15" style="margin-top:15px">
      <el-col :span="8">
        <el-card class="data-card">
          <template #header>
            <div class="card-header">
              <span>流程管理</span>
              <el-button link type="primary" @click="goTo('/process/new')">新建流程</el-button>
            </div>
          </template>
          <el-table :data="processList" stripe style="width:100%">
            <el-table-column prop="type" label="类型" width="80" />
            <el-table-column prop="title" label="标题" min-width="130" show-overflow-tooltip />
            <el-table-column prop="status" label="状态" width="70">
              <template #default="{ row }">
                <el-tag :type="row.status === '已通过' ? 'success' : row.status === '已拒绝' ? 'danger' : 'warning'" size="small">
                  {{ row.status }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="data-card">
          <template #header>
            <div class="card-header">
              <span>工作计划</span>
              <el-button link type="primary" @click="goTo('/plan/list')">更多</el-button>
            </div>
          </template>
          <el-table :data="plans" stripe style="width:100%">
            <el-table-column prop="title" label="标题" min-width="130" show-overflow-tooltip />
            <el-table-column prop="startDate" label="开始" width="80" />
            <el-table-column prop="endDate" label="结束" width="80" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="data-card">
          <template #header>
            <div class="card-header">
              <span>我的帖子</span>
              <el-button link type="primary" @click="goTo('/chat/list')">更多</el-button>
            </div>
          </template>
          <el-table :data="myPosts" stripe style="width:100%">
            <el-table-column prop="title" label="标题" min-width="160" show-overflow-tooltip />
            <el-table-column prop="createTime" label="时间" width="100" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'
import * as echarts from 'echarts'
import { getTaskCompletionRank } from '@/api/task'
import { getNoticeList } from '@/api/notice'
import { getMyProcessList } from '@/api/process'
import { getPlans } from '@/api/plan'
import { getFileStats } from '@/api/file'
import { getWeeklyUsage } from '@/api/dashboard'
import { getDashboardData } from '@/api/dashboard'
import { clockIn } from '@/api/attendance/checkin'
import { getPosts } from '@/api/chat'

const router = useRouter()

// ====== 签到打卡 ======
const currentTime = ref('')
const currentDate = ref('')
const currentWeekday = ref('')
const isCheckedIn = ref(false)
const checkInTime = ref('')
const clockInLoading = ref(false)
let timer = null

function updateClock() {
  const now = dayjs()
  currentTime.value = now.format('HH:mm:ss')
  currentDate.value = now.format('MM月DD日')
  currentWeekday.value = now.format('dddd')
}

async function handleClockIn() {
  if (isCheckedIn.value) return
  clockInLoading.value = true
  try {
    const res = await clockIn()
    if (res.code === 200) {
      isCheckedIn.value = true
      checkInTime.value = dayjs().format('HH:mm')
      ElMessage.success('签到成功')
    }
  } catch (error) {
    ElMessage.error('签到失败')
  } finally {
    clockInLoading.value = false
  }
}

// ====== 统计数据 ======
const stats = ref({ totalFiles: 0, addressCount: 0, chatCount: 0 })

async function loadStats() {
  try {
    const [fileRes, dashboardRes] = await Promise.all([
      getFileStats().catch(() => ({ data: {} })),
      getDashboardData().catch(() => ({ data: {} }))
    ])
    const dashData = dashboardRes.data || {}
    stats.value = {
      totalFiles: fileRes.data?.totalFiles || 0,
      addressCount: dashData.addressCount || 0,
      chatCount: dashData.chatCount || 0
    }
  } catch (e) { /* ignore */ }
}

// ====== resize 处理器 ======
function handleResize() {
  lineChart?.resize()
  barChart?.resize()
}

// ====== 图表 ======
const lineChartRef = ref(null)
const barChartRef = ref(null)
const chartWeek = ref(new Date())
let lineChart = null
let barChart = null

function initLineChart(loginData, actionData) {
  if (!lineChartRef.value) return
  lineChart = echarts.init(lineChartRef.value)
  lineChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'], axisLabel: { color: '#999' } },
    yAxis: { type: 'value', axisLabel: { color: '#999' } },
    series: [
      { name: '登录次数', type: 'line', data: loginData || [0, 0, 0, 0, 0, 0, 0], smooth: true, lineStyle: { color: '#00a65a', width: 2 }, areaStyle: { color: 'rgba(0,166,90,0.1)' }, symbol: 'circle' },
      { name: '操作次数', type: 'line', data: actionData || [0, 0, 0, 0, 0, 0, 0], smooth: true, lineStyle: { color: '#409eff', width: 2 }, areaStyle: { color: 'rgba(64,158,255,0.1)' }, symbol: 'circle' }
    ]
  })
}

async function loadLineChartData() {
  try {
    const res = await getWeeklyUsage()
    if (res.code === 200) {
      const data = res.data
      lineChart?.dispose()
      lineChart = null
      nextTick(() => initLineChart(data.loginCount, data.actionCount))
    }
  } catch (e) { /* ignore */ }
}

function initBarChart(data) {
  if (!barChartRef.value) return
  const names = data.map(d => d.name)
  const values = data.map(d => d.completedCount)
  barChart = echarts.init(barChartRef.value)
  barChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: names.length ? names : ['暂无数据'], axisLabel: { color: '#999' } },
    yAxis: { type: 'value', axisLabel: { color: '#999' } },
    series: [{ type: 'bar', data: values.length ? values : [0], itemStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: '#00a65a' }, { offset: 1, color: '#67c23a' }]), borderRadius: [4, 4, 0, 0] } }]
  })
}

// ====== 加载公告通知 ======
const notices = ref([])
async function loadNotices() {
  try {
    const res = await getNoticeList({ pageNum: 1, pageSize: 5 })
    if (res.code === 200) {
      notices.value = (res.data?.records || []).map(n => ({ title: n.title, publisher: n.publisherName || '-', publishTime: n.publishTime ? dayjs(n.publishTime).format('MM-DD') : '-' }))
    }
  } catch (e) { /* ignore */ }
}

// ====== 行事历 ======
const calendarDate = ref(new Date())
const events = ref([])
function hasEvent(date) { return events.value.includes(date) }

// ====== 流程管理 ======
const processList = ref([])
async function loadProcesses() {
  try {
    const res = await getMyProcessList()
    if (res.code === 200) {
      processList.value = (res.data || []).slice(0, 4).map(p => ({ type: p.processName || p.businessType || '-', title: p.title || '-', status: p.status === 'approved' ? '已通过' : p.status === 'rejected' ? '已拒绝' : '审批中' }))
    }
  } catch (e) { /* ignore */ }
}

// ====== 工作计划 ======
const plans = ref([])
async function loadPlans() {
  try {
    const res = await getPlans()
    if (res.code === 200) {
      plans.value = (res.data || []).slice(0, 4).map(p => ({ title: p.title || '-', startDate: p.startTime ? dayjs(p.startTime).format('MM-DD') : '-', endDate: p.endTime ? dayjs(p.endTime).format('MM-DD') : '-' }))
    }
  } catch (e) { /* ignore */ }
}

// ====== 我的帖子 ======
const myPosts = ref([])
async function loadMyPosts() {
  try {
    const res = await getPosts({ page: 1, pageSize: 50 })
    let list = res.data?.list || res.data || []
    // 排序：按时间倒序取最近4条
    list.sort((a, b) => new Date(b.createTime || 0) - new Date(a.createTime || 0))
    myPosts.value = list.slice(0, 4).map(p => ({
      title: p.title || '-',
      createTime: p.createTime ? dayjs(p.createTime).format('MM-DD HH:mm') : '-'
    }))
  } catch (e) { /* ignore */ }
}

// ====== 工具 ======
const goTo = (path) => router.push(path)

// ====== 任务排行加载 ======
async function loadCompletionRank() {
  try {
    const res = await getTaskCompletionRank()
    const data = res.data || []
    if (Array.isArray(data) && data.length > 0) {
      barChart?.dispose()
      barChart = null
      nextTick(() => initBarChart(data))
    }
  } catch (e) { /* ignore */ }
}

// ====== 生命周期 ======
onMounted(() => {
  updateClock()
  timer = setInterval(updateClock, 1000)
  nextTick(() => {
    initLineChart([], [])
    initBarChart([])
    window.addEventListener('resize', handleResize)
  })
  // 并行加载所有真实数据
  Promise.all([loadLineChartData(), loadCompletionRank(), loadStats(), loadNotices(), loadProcesses(), loadPlans(), loadMyPosts()])
})

onUnmounted(() => {
  clearInterval(timer)
  window.removeEventListener('resize', handleResize)
  lineChart?.dispose()
  barChart?.dispose()
})
</script>

<style scoped lang="scss">
.dashboard {
  // ====== 顶部功能卡片 ======
  .shortcut-card {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 20px;
    border-radius: 8px;
    color: #fff;
    cursor: pointer;
    transition: transform 0.3s, box-shadow 0.3s;
    min-height: 85px;

    &:hover {
      transform: translateY(-3px);
      box-shadow: 0 6px 20px rgba(0,0,0,0.15);
    }

    &.green { background: linear-gradient(135deg, #00a65a, #28b868); }
    &.blue { background: linear-gradient(135deg, #409eff, #66b1ff); }
    &.purple { background: linear-gradient(135deg, #9c27b0, #b54ed4); }
    &.orange { background: linear-gradient(135deg, #ff9800, #ffb74d); }

    .card-left {
      display: flex;
      align-items: center;
      gap: 15px;
    }

    .shortcut-icon {
      font-size: 36px;
      opacity: 0.9;
    }

    .shortcut-info {
      .time {
        font-size: 22px;
        font-weight: bold;
        font-family: 'Arial', monospace;
      }
      .label { font-size: 13px; opacity: 0.85; margin-top: 3px; }
    }

    .card-right {
      display: flex;
      flex-direction: column;
      align-items: flex-end;
    }

    .shortcut-left {
      display: flex;
      align-items: center;
      gap: 15px;
    }

    .shortcut-text {
      .num {
        font-size: 28px;
        font-weight: bold;
      }
      .label { font-size: 13px; opacity: 0.85; margin-top: 2px; }
    }

    .shortcut-arrow {
      font-size: 20px;
      opacity: 0.7;
    }
  }

  // ====== 卡片通用 ======
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 15px;
    font-weight: 600;
    color: #333;
  }

  .chart-card, .data-card {
    border-top: 3px solid #00a65a;
  }

  .chart-container {
    width: 100%;
    height: 280px;
  }

  // ====== 行事历 ======
  :deep(.el-calendar) {
    --el-calendar-border: #ebeef5;

    .el-calendar-table td { padding: 2px; }
    .el-calendar-table .el-calendar-day { height: 36px; padding: 2px; }
  }

  .calendar-cell {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 100%;

    .calendar-day {
      font-size: 13px;
      line-height: 1.4;
    }

    .event-dot {
      width: 6px;
      height: 6px;
      background: #00a65a;
      border-radius: 50%;
      margin-top: 2px;
    }
  }


}

.el-button--success {
  color: #fff;
}
</style>
