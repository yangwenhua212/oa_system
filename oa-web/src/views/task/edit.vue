<template>
  <div class="task-edit-page">
    <div class="page-header">
      <el-button text @click="goBack" class="back-btn">
        <el-icon><ArrowLeft /></el-icon>
        返回
      </el-button>
    </div>

    <el-card class="form-card">
      <template #header><span class="card-title">{{ isEdit ? '编辑任务' : '新建任务' }}</span></template>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="40">
          <el-col :xs="24" :md="12">
            <el-form-item label="类型" prop="taskType">
              <el-select v-model="form.taskType" placeholder="请选择类型" style="width:100%">
                <el-option label="日常" value="daily" />
                <el-option label="项目" value="project" />
                <el-option label="紧急" value="urgent" />
              </el-select>
            </el-form-item>
            <el-form-item label="开始日期" prop="startTime">
              <el-date-picker v-model="form.startTime" type="datetime" placeholder="选择开始日期" format="YYYY-MM-DD HH:mm" value-format="YYYY-MM-DD HH:mm:ss" style="width:100%" />
            </el-form-item>
            <el-form-item label="标题" prop="taskName">
              <el-input v-model="form.taskName" placeholder="请输入标题" />
            </el-form-item>
            <el-form-item label="通讯录描述">
              <el-input v-model="form.addressDesc" type="textarea" :rows="3" placeholder="请输入通讯录描述" />
            </el-form-item>
            <el-form-item label="置顶">
              <el-checkbox v-model="form.isTop">置顶</el-checkbox>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :md="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择状态" style="width:100%">
                <el-option label="待处理" value="todo" />
                <el-option label="进行中" value="in_progress" />
                <el-option label="已完成" value="completed" />
              </el-select>
            </el-form-item>
            <el-form-item label="结束日期" prop="endTime">
              <el-date-picker v-model="form.endTime" type="datetime" placeholder="选择结束日期" format="YYYY-MM-DD HH:mm" value-format="YYYY-MM-DD HH:mm:ss" style="width:100%" />
            </el-form-item>
            <el-form-item label="接收人">
              <el-input v-model="handlerName" placeholder="点击选择接收人" readonly @click="userPickerVisible = true" style="cursor:pointer" />
            </el-form-item>
            <el-form-item label="评价">
              <el-input v-model="form.evaluation" type="textarea" :rows="3" placeholder="请输入评价" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div class="form-footer">
        <el-button type="primary" class="btn-primary-green" @click="handleSave">
          <el-icon><Plus /></el-icon> 保存
        </el-button>
        <el-button @click="goBack">取消</el-button>
      </div>
    </el-card>

    <!-- 接收人选择对话框 -->
    <el-dialog v-model="userPickerVisible" title="选择接收人" width="650px" destroy-on-close @open="loadUsers">
      <div class="user-picker-search">
        <el-input v-model="userSearch" placeholder="查找..." clearable style="flex:1" @keyup.enter="searchUsers" />
        <el-button type="primary" class="btn-primary-green" @click="searchUsers">搜索</el-button>
      </div>
      <el-table :data="userTableData" v-loading="userLoading" @selection-change="onUserSelect" ref="userTableRef">
        <el-table-column type="selection" width="40" align="center" />
        <el-table-column prop="deptName" label="部门" width="120" />
        <el-table-column prop="realName" label="真实姓名" width="120" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="positionName" label="职位" width="120" />
        <el-table-column prop="phone" label="电话" width="130" />
      </el-table>
      <div class="user-picker-pagination">
        <span class="pagination-info">共{{ userTotal }}条 | 每页{{ userPageSize }}条 | 共{{ userTotalPages }}页</span>
        <el-pagination
          v-model:current-page="userPageNum"
          v-model:page-size="userPageSize"
          :total="userTotal"
          layout="prev, pager, next"
          background
          size="small"
          @current-change="loadUsers"
        />
      </div>
      <template #footer>
        <el-button @click="userPickerVisible = false">取消</el-button>
        <el-button type="primary" class="btn-primary-green" @click="confirmUser">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { createTask, updateTask, getTaskById } from '@/api/task'
import { getUserList } from '@/api/system/user'

const router = useRouter()
const route = useRoute()
const formRef = ref()
const handlerName = ref('')
const isEdit = ref(false)

const form = reactive({
  taskType: '',
  status: 'todo',
  startTime: null,
  endTime: null,
  taskName: '',
  handlerId: null,
  addressDesc: '',
  evaluation: '',
  isTop: false
})

const rules = {
  taskType: [{ required: true, message: '请选择类型', trigger: 'change' }],
  taskName: [{ required: true, message: '请输入标题', trigger: 'blur' }]
}

// 用户选择器
const userPickerVisible = ref(false)
const userLoading = ref(false)
const userTableData = ref([])
const userPageNum = ref(1)
const userPageSize = ref(10)
const userTotal = ref(0)
const userSearch = ref('')
const userTableRef = ref()
const selectedUser = ref(null)

const userTotalPages = computed(() => Math.ceil(userTotal.value / userPageSize.value))

function searchUsers() {
  userPageNum.value = 1
  loadUsers()
}

async function loadUsers() {
  userLoading.value = true
  try {
    const res = await getUserList({
      pageNum: userPageNum.value,
      pageSize: userPageSize.value,
      phone: userSearch.value || null
    })
    if (res.code === 200) {
      const data = res.data
      userTableData.value = (data?.records || data?.list || []).map(u => ({
        id: u.id,
        realName: u.realName,
        username: u.username,
        phone: u.phone,
        deptName: u.deptName || '-',
        positionName: u.positionName || '-'
      }))
      userTotal.value = data?.total || userTableData.value.length
    }
  } catch (e) {
    console.error(e)
  } finally {
    userLoading.value = false
  }
}

function onUserSelect(selection) {
  selectedUser.value = selection.length > 0 ? selection[0] : null
}

function confirmUser() {
  if (selectedUser.value) {
    form.handlerId = selectedUser.value.id
    handlerName.value = selectedUser.value.realName
  }
  userPickerVisible.value = false
}

async function handleSave() {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
    const data = {
      taskName: form.taskName,
      taskType: form.taskType,
      status: form.status,
      startTime: form.startTime,
      endTime: form.endTime,
      handlerId: form.handlerId
    }
    if (isEdit.value) {
      data.id = route.query.id
      await updateTask(data)
      ElMessage.success('更新成功')
    } else {
      await createTask(data)
      ElMessage.success('创建成功')
    }
    router.push('/task/my')
  } catch (error) {
    if (error?.message) ElMessage.error(error.message)
  }
}

function goBack() { router.push('/task/all') }

onMounted(async () => {
  try {
    const taskId = route.query.id
    if (taskId) {
      isEdit.value = true
      const res = await getTaskById(taskId)
      if (res.code === 200 && res.data) {
        const task = res.data
        form.taskType = task.taskType || ''
        form.status = task.status || 'todo'
        form.startTime = task.startTime || null
        form.endTime = task.endTime || null
        form.taskName = task.taskName || ''
        form.handlerId = task.handlerId || null
        handlerName.value = task.handlerName || ''
      }
    }
    const res = await getUserList({ pageNum: 1, pageSize: 999 })
  } catch (e) { /* ignore */ }
})
</script>

<style scoped lang="scss">
.task-edit-page {
  .page-header { margin-bottom: 16px; }
  .back-btn { padding: 0; font-size: 14px; color: #00a65a; &:hover { color: #008d4c; } }
  .form-card { border-top: 3px solid #00a65a; }
  .card-title { font-size: 16px; font-weight: 600; color: #333; }
  .form-footer { margin-top: 20px; padding-top: 20px; border-top: 1px solid #ebeef5; }

  .user-picker-search { display:flex; gap:8px; margin-bottom:12px; }
  .user-picker-pagination {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: 12px;
    .pagination-info { font-size: 13px; color: #666; }
  }
}
.btn-primary-green {
  background-color: #00a65a !important;
  border-color: #00a65a !important;
  color: #fff !important;
  &:hover { background-color: #008d4c !important; border-color: #008d4c !important; }
}
</style>
