<template>
  <div class="plan-page">
    <el-card class="main-card">
      <template #header>
        <div class="card-header">
          <span>工作计划</span>
          <div class="header-actions">
            <el-select v-model="filterStatus" placeholder="按状态筛选" size="small" clearable style="width: 120px; margin-right: 10px;">
              <el-option label="全部" :value="null" />
              <el-option v-for="s in statuses" :key="s.id" :label="s.name" :value="s.id" />
            </el-select>
            <el-select v-model="filterType" placeholder="按类型筛选" size="small" clearable style="width: 120px; margin-right: 10px;">
              <el-option label="全部" :value="null" />
              <el-option v-for="t in types" :key="t.id" :label="t.name" :value="t.id" />
            </el-select>
            <el-button type="primary" size="small" @click="handleNewPlan">
              <el-icon><Plus /></el-icon>
              新建计划
            </el-button>
          </div>
        </div>
      </template>

      <!-- 计划统计 -->
      <div class="stats-container">
        <div class="stat-item" v-for="stat in stats" :key="stat.label">
          <div class="stat-value" :style="{ color: stat.color }">{{ stat.value }}</div>
          <div class="stat-label">{{ stat.label }}</div>
        </div>
      </div>

      <!-- 计划列表 -->
      <el-table :data="filteredPlans" stripe v-loading="loading">
        <el-table-column prop="title" label="计划标题" min-width="200" />
        <el-table-column prop="typeName" label="类型" width="100">
          <template #default="{ row }">
            <el-tag size="small" type="primary">{{ row.typeName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="statusName" label="状态" width="100">
          <template #default="{ row }">
            <el-tag size="small" :type="getStatusType(row.statusId)">{{ row.statusName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="120" />
        <el-table-column prop="endTime" label="结束时间" width="120" />
        <el-table-column prop="progress" label="进度" width="150">
          <template #default="{ row }">
            <el-progress :percentage="row.progress || 0" :color="getProgressColor(row.progress)" />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="120" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">查看</el-button>
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <!-- 新建/编辑计划对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑计划' : '新建计划'"
      width="600px"
    >
      <el-form :model="planForm" :rules="planRules" ref="planFormRef" label-width="100px">
        <el-form-item label="计划标题" prop="title">
          <el-input v-model="planForm.title" placeholder="请输入计划标题" />
        </el-form-item>
        <el-form-item label="计划类型" prop="typeId">
          <el-select v-model="planForm.typeId" placeholder="选择类型" style="width: 100%">
            <el-option v-for="t in types" :key="t.id" :label="t.name" :value="t.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker
            v-model="planForm.startTime"
            type="datetime"
            placeholder="选择开始时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker
            v-model="planForm.endTime"
            type="datetime"
            placeholder="选择结束时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="计划内容" prop="content">
          <el-input v-model="planForm.content" type="textarea" :rows="4" placeholder="请输入计划内容" />
        </el-form-item>
        <el-form-item label="进度" prop="progress">
          <el-slider v-model="planForm.progress" :step="10" show-stops />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>

    <!-- 查看计划对话框 -->
    <el-dialog v-model="viewDialogVisible" title="计划详情" width="600px">
      <el-descriptions :column="1" border v-if="currentPlan">
        <el-descriptions-item label="计划标题">{{ currentPlan.title }}</el-descriptions-item>
        <el-descriptions-item label="类型">
          <el-tag size="small" type="primary">{{ currentPlan.typeName }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag size="small" :type="getStatusType(currentPlan.statusId)">{{ currentPlan.statusName }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="开始时间">{{ currentPlan.startTime }}</el-descriptions-item>
        <el-descriptions-item label="结束时间">{{ currentPlan.endTime }}</el-descriptions-item>
        <el-descriptions-item label="计划内容">{{ currentPlan.content }}</el-descriptions-item>
        <el-descriptions-item label="进度">
          <el-progress :percentage="currentPlan.progress || 0" />
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="viewDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="handleEditFromView">编辑</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPlans, getPlanById, createPlan, updatePlan, deletePlan, getPlanTypes, getPlanStatuses } from '@/api/plan'

// 状态变量
const loading = ref(false)
const plans = ref([])
const types = ref([])
const statuses = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const filterStatus = ref(null)
const filterType = ref(null)
const isEdit = ref(false)
const currentPlan = ref(null)

// 对话框状态
const dialogVisible = ref(false)
const viewDialogVisible = ref(false)
const planFormRef = ref(null)

// 计划表单
const planForm = reactive({
  id: null,
  title: '',
  typeId: null,
  startTime: '',
  endTime: '',
  content: '',
  progress: 0
})

// 表单验证
const planRules = {
  title: [{ required: true, message: '请输入计划标题', trigger: 'blur' }],
  typeId: [{ required: true, message: '请选择计划类型', trigger: 'change' }]
}

// 统计数据
const stats = computed(() => {
  const total = plans.value.length
  const completed = plans.value.filter(p => p.statusId === 5).length
  const inProgress = plans.value.filter(p => p.statusId === 4).length
  const notStarted = plans.value.filter(p => p.statusId === 3).length
  
  return [
    { label: '总计划', value: total, color: '#409eff' },
    { label: '已完成', value: completed, color: '#67c23a' },
    { label: '进行中', value: inProgress, color: '#e6a23c' },
    { label: '未开始', value: notStarted, color: '#909399' }
  ]
})

// 过滤后的计划
const filteredPlans = computed(() => {
  let result = plans.value
  if (filterStatus.value) {
    result = result.filter(p => p.statusId === filterStatus.value)
  }
  if (filterType.value) {
    result = result.filter(p => p.typeId === filterType.value)
  }
  return result
})

// 获取状态类型
function getStatusType(statusId) {
  const typeMap = {
    3: 'info',    // 未开始
    4: 'warning', // 进行中
    5: 'success', // 已完成
    6: 'danger'   // 已取消
  }
  return typeMap[statusId] || 'info'
}

// 获取进度颜色
function getProgressColor(progress) {
  if (progress >= 100) return '#67c23a'
  if (progress >= 50) return '#e6a23c'
  return '#409eff'
}

// 加载数据
async function loadData() {
  loading.value = true
  try {
    const [plansRes, typesRes, statusesRes] = await Promise.all([
      getPlans({ page: currentPage.value, pageSize: pageSize.value }),
      getPlanTypes(),
      getPlanStatuses()
    ])
    plans.value = plansRes.data?.list || plansRes.data || []
    types.value = typesRes.data || []
    statuses.value = statusesRes.data || []
    total.value = plansRes.data?.total || plans.value.length
  } catch (error) {
    console.error('加载数据失败:', error)
    plans.value = []
    types.value = []
    statuses.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 新建计划
function handleNewPlan() {
  isEdit.value = false
  Object.assign(planForm, {
    id: null,
    title: '',
    typeId: types.value[0]?.id,
    startTime: '',
    endTime: '',
    content: '',
    progress: 0
  })
  dialogVisible.value = true
}

// 查看计划
function handleView(row) {
  currentPlan.value = row
  viewDialogVisible.value = true
}

// 编辑计划
function handleEdit(row) {
  isEdit.value = true
  Object.assign(planForm, row)
  viewDialogVisible.value = false
  dialogVisible.value = true
}

function handleEditFromView() {
  viewDialogVisible.value = false
  handleEdit(currentPlan.value)
}

// 保存计划
async function handleSave() {
  try {
    await planFormRef.value.validate()
    if (planForm.id) {
      await updatePlan(planForm.id, planForm)
    } else {
      await createPlan(planForm)
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadData()
  } catch (error) {
    if (error !== false) {
      ElMessage.error('保存失败')
    }
  }
}

// 删除计划
async function handleDelete(row) {
  try {
    await ElMessageBox.confirm('确定要删除该计划吗？', '提示', { type: 'warning' })
    await deletePlan(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 分页
function handlePageChange(page) {
  currentPage.value = page
  loadData()
}

function handleSizeChange(size) {
  pageSize.value = size
  loadData()
}

// 初始化
onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.plan-page {
  .main-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .header-actions {
        display: flex;
        align-items: center;
      }
    }
  }

  .stats-container {
    display: flex;
    gap: 20px;
    margin-bottom: 20px;
    padding: 20px;
    background: #f5f7fa;
    border-radius: 8px;

    .stat-item {
      text-align: center;
      min-width: 100px;

      .stat-value {
        font-size: 28px;
        font-weight: bold;
        margin-bottom: 5px;
      }

      .stat-label {
        font-size: 14px;
        color: #909399;
      }
    }
  }

  .pagination {
    display: flex;
    justify-content: flex-end;
    margin-top: 15px;
  }
}
</style>
