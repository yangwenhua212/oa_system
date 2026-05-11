<template>
  <div class="overtime-page">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>加班申请</span>
          <el-button type="primary" class="btn-primary-green" @click="handleCreate">
            <el-icon><Plus /></el-icon>
            新建申请
          </el-button>
        </div>
      </template>

      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="加班类型">
          <el-select v-model="searchForm.overtimeType" placeholder="请选择" clearable style="width: 150px">
            <el-option label="工作日加班" value="weekday" />
            <el-option label="周末加班" value="weekend" />
            <el-option label="节假日加班" value="holiday" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 150px">
            <el-option label="审批中" value="pending" />
            <el-option label="已通过" value="approved" />
            <el-option label="已拒绝" value="rejected" />
            <el-option label="已取消" value="cancelled" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 加班列表 -->
      <el-table :data="tableData" stripe border v-loading="loading">
        <el-table-column prop="overtimeType" label="加班类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getOvertimeTypeTag(row.overtimeType)">
              {{ getOvertimeTypeText(row.overtimeType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="160">
          <template #default="{ row }">
            {{ formatDateTime(row.startTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="endTime" label="结束时间" width="160">
          <template #default="{ row }">
            {{ formatDateTime(row.endTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="duration" label="加班时长" width="100">
          <template #default="{ row }">
            {{ row.duration }}小时
          </template>
        </el-table-column>
        <el-table-column prop="reason" label="加班原因" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="申请时间" width="160">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleView(row)">查看</el-button>
            <el-button 
              link 
              type="danger" 
              size="small" 
              @click="handleCancel(row)"
              v-if="row.status === 'pending'"
            >
              取消
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 新建/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
        <el-form-item label="加班类型" prop="overtimeType">
          <el-select v-model="form.overtimeType" placeholder="请选择加班类型" style="width: 100%">
            <el-option label="工作日加班" value="weekday" />
            <el-option label="周末加班" value="weekend" />
            <el-option label="节假日加班" value="holiday" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker
            v-model="form.startTime"
            type="datetime"
            placeholder="选择开始时间"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker
            v-model="form.endTime"
            type="datetime"
            placeholder="选择结束时间"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="加班时长">
          <span class="duration-display">{{ calculatedDuration }} 小时</span>
        </el-form-item>
        <el-form-item label="加班原因" prop="reason">
          <el-input
            v-model="form.reason"
            type="textarea"
            :rows="4"
            placeholder="请输入加班原因"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">提交</el-button>
      </template>
    </el-dialog>

    <!-- 查看详情对话框 -->
    <el-dialog v-model="detailVisible" title="加班详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="申请人">{{ detailData.realName }}</el-descriptions-item>
        <el-descriptions-item label="部门">{{ detailData.deptName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="加班类型">
          <el-tag :type="getOvertimeTypeTag(detailData.overtimeType)">
            {{ getOvertimeTypeText(detailData.overtimeType) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusTag(detailData.status)">
            {{ getStatusText(detailData.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="开始时间">{{ formatDateTime(detailData.startTime) }}</el-descriptions-item>
        <el-descriptions-item label="结束时间">{{ formatDateTime(detailData.endTime) }}</el-descriptions-item>
        <el-descriptions-item label="加班时长">{{ detailData.duration }}小时</el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ formatDateTime(detailData.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="加班原因" :span="2">{{ detailData.reason || '-' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMyOvertimeList, submitOvertime, cancelOvertime, getOvertimeById } from '@/api/attendance/overtime'
import dayjs from 'dayjs'

// 表格数据
const router = useRouter()
const tableData = ref([])
const loading = ref(false)
const pagination = ref({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 搜索表单
const searchForm = ref({
  overtimeType: '',
  status: ''
})

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('新建加班申请')
const formRef = ref(null)
const submitting = ref(false)
const form = ref({
  overtimeType: 'weekday',
  startTime: '',
  endTime: '',
  reason: ''
})

// 详情对话框
const detailVisible = ref(false)
const detailData = ref({})

// 表单校验
const formRules = {
  overtimeType: [{ required: true, message: '请选择加班类型', trigger: 'change' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }],
  reason: [{ required: true, message: '请输入加班原因', trigger: 'blur' }]
}

// 计算加班时长
const calculatedDuration = computed(() => {
  if (!form.value.startTime || !form.value.endTime) return 0
  const start = dayjs(form.value.startTime)
  const end = dayjs(form.value.endTime)
  if (end.isBefore(start)) return 0
  const hours = end.diff(start, 'minute') / 60
  return hours.toFixed(2)
})

// 获取加班类型文本
const getOvertimeTypeText = (type) => {
  const map = {
    weekday: '工作日加班',
    weekend: '周末加班',
    holiday: '节假日加班'
  }
  return map[type] || type
}

// 获取加班类型标签颜色
const getOvertimeTypeTag = (type) => {
  const map = {
    weekday: '',
    weekend: 'warning',
    holiday: 'danger'
  }
  return map[type] || ''
}

// 获取状态文本
const getStatusText = (status) => {
  const map = {
    draft: '草稿',
    pending: '审批中',
    approved: '已通过',
    rejected: '已拒绝',
    cancelled: '已取消'
  }
  return map[status] || status
}

// 获取状态标签颜色
const getStatusTag = (status) => {
  const map = {
    draft: 'info',
    pending: 'warning',
    approved: 'success',
    rejected: 'danger',
    cancelled: 'info'
  }
  return map[status] || ''
}

// 格式化日期时间
const formatDateTime = (datetime) => {
  if (!datetime) return '-'
  return dayjs(datetime).format('YYYY-MM-DD HH:mm')
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const res = await getMyOvertimeList()
    tableData.value = res.data || []
    pagination.value.total = tableData.value.length
  } catch (error) {
    console.error('加载加班列表失败', error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.value.pageNum = 1
  loadData()
}

// 重置
const handleReset = () => {
  searchForm.value = {
    overtimeType: '',
    status: ''
  }
  loadData()
}

// 新建
const handleCreate = () => {
  router.push('/attendance/overtime-form')
}

// 查看
const handleView = async (row) => {
  try {
    const res = await getOvertimeById(row.id)
    detailData.value = res.data || {}
    detailVisible.value = true
  } catch (error) {
    console.error('获取加班详情失败', error)
  }
}

// 取消
const handleCancel = (row) => {
  ElMessageBox.confirm('确定要取消该加班申请吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await cancelOvertime(row.id)
      ElMessage.success('取消成功')
      loadData()
    } catch (error) {
      console.error('取消失败', error)
    }
  }).catch(() => {})
}

// 提交
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        await submitOvertime(form.value)
        ElMessage.success('提交成功')
        dialogVisible.value = false
        loadData()
      } catch (error) {
        console.error('提交失败', error)
      } finally {
        submitting.value = false
      }
    }
  })
}

// 对话框关闭
const handleDialogClose = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

// 分页
const handleSizeChange = (size) => {
  pagination.value.pageSize = size
  loadData()
}

const handleCurrentChange = (page) => {
  pagination.value.pageNum = page
  loadData()
}

// 初始化
onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.overtime-page {
  .box-card {
    border-top: 3px solid #00a65a;
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .search-form {
    margin-bottom: 20px;
  }

  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }

  .duration-display {
    color: #00a65a;
    font-weight: bold;
    font-size: 16px;
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
