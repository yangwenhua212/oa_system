<template>
  <div class="schedule-page">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>日程安排</span>
          <el-button type="primary" class="btn-primary-green" @click="addDialogVisible = true">
            <el-icon><Plus /></el-icon>添加日程
          </el-button>
        </div>
      </template>
      <el-calendar v-model="currentDate">
        <template #date-cell="{ data }">
          <div class="calendar-cell" @click="viewDaySchedules(data.day)">
            <span>{{ data.day.split('-').slice(2).join('') }}</span>
            <div v-if="getSchedules(data.day).length" class="schedule-dots">
              <span v-for="n in Math.min(getSchedules(data.day).length, 3)" :key="n" class="dot"></span>
            </div>
          </div>
        </template>
      </el-calendar>
    </el-card>

    <!-- 当日日程列表 -->
    <el-card class="box-card" style="margin-top:15px">
      <template #header>
        <div class="card-header">
          <span>{{ selectedDate }} 日程</span>
        </div>
      </template>
      <div v-if="daySchedules.length === 0" class="empty-tip">当日暂无日程</div>
      <div v-for="s in daySchedules" :key="s.id" class="schedule-item">
        <div class="schedule-time">{{ s.startTime ? dayjs(s.startTime).format('HH:mm') : '' }} - {{ s.endTime ? dayjs(s.endTime).format('HH:mm') : '' }}</div>
        <div class="schedule-title">{{ s.title }}</div>
        <el-tag v-if="s.type === 'meeting'" size="small">会议</el-tag>
        <el-tag v-else-if="s.type === 'task'" size="small" type="success">任务</el-tag>
        <el-tag v-else size="small" type="info">其他</el-tag>
      </div>
    </el-card>

    <!-- 添加日程对话框 -->
    <el-dialog v-model="addDialogVisible" title="添加日程" width="500px">
      <el-form :model="scheduleForm" label-width="100px">
        <el-form-item label="标题" required>
          <el-input v-model="scheduleForm.title" placeholder="请输入日程标题" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="scheduleForm.type" style="width:100%">
            <el-option label="会议" value="meeting" />
            <el-option label="任务" value="task" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker v-model="scheduleForm.startTime" type="datetime" placeholder="选择开始时间" style="width:100%" />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker v-model="scheduleForm.endTime" type="datetime" placeholder="选择结束时间" style="width:100%" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="scheduleForm.remark" type="textarea" rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAddSchedule">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'

const currentDate = ref(new Date())
const selectedDate = ref('')
const daySchedules = ref([])
const addDialogVisible = ref(false)

const scheduleForm = reactive({
  title: '',
  type: 'meeting',
  startTime: '',
  endTime: '',
  remark: ''
})

// 所有日程（模拟）
const allSchedules = ref([])

const getSchedules = (day) => {
  return allSchedules.value.filter(s => {
    const d = s.startTime ? dayjs(s.startTime).format('YYYY-MM-DD') : ''
    return d === day
  })
}

const viewDaySchedules = (day) => {
  selectedDate.value = day
  daySchedules.value = allSchedules.value.filter(s => {
    const d = s.startTime ? dayjs(s.startTime).format('YYYY-MM-DD') : ''
    return d === day
  })
}

const handleAddSchedule = () => {
  if (!scheduleForm.title.trim()) {
    ElMessage.warning('请输入日程标题')
    return
  }
  const newSchedule = {
    id: Date.now(),
    title: scheduleForm.title,
    type: scheduleForm.type,
    startTime: scheduleForm.startTime,
    endTime: scheduleForm.endTime,
    remark: scheduleForm.remark
  }
  allSchedules.value.push(newSchedule)
  daySchedules.value = getSchedules(selectedDate.value)
  addDialogVisible.value = false
  scheduleForm.title = ''
  scheduleForm.type = 'meeting'
  scheduleForm.startTime = ''
  scheduleForm.endTime = ''
  scheduleForm.remark = ''
  ElMessage.success('日程添加成功')
}

onMounted(() => {
  selectedDate.value = dayjs().format('YYYY-MM-DD')
  daySchedules.value = getSchedules(selectedDate.value)
})
</script>

<style scoped lang="scss">
.schedule-page {
  .box-card { border-top: 3px solid #00a65a; }
  .card-header {
    display: flex; justify-content: space-between; align-items: center;
  }
  .calendar-cell {
    height: 60px; cursor: pointer;
    .schedule-dots {
      display: flex; gap: 2px;
      .dot { width: 6px; height: 6px; background: #00a65a; border-radius: 50%; }
    }
  }
  .empty-tip { text-align: center; color: #999; padding: 20px; font-size: 14px; }
  .schedule-item {
    display: flex; align-items: center; gap: 12px;
    padding: 10px 0; border-bottom: 1px solid #f0f0f0;
    &:last-child { border-bottom: none; }
    .schedule-time { font-size: 12px; color: #999; width: 100px; flex-shrink: 0; }
    .schedule-title { flex: 1; font-size: 14px; color: #333; }
  }
}
.btn-primary-green { background-color: #00a65a !important; border-color: #00a65a !important; color: #fff !important; }
</style>
