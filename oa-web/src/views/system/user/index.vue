<template>
  <div class="user-manage">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <el-button type="primary" @click="handleAdd" class="btn-primary-green">
            <el-icon><Plus /></el-icon>
            添加用户
          </el-button>
        </div>
      </template>

      <!-- Search Form -->
      <el-form inline :model="searchForm" class="search-form">
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input v-model="searchForm.realName" placeholder="请输入真实姓名" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch" class="btn-primary-green">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>

      <!-- Table -->
      <el-table
        :data="tableData"
        v-loading="loading"
        stripe
        border
        class="user-table"
      >
        <el-table-column prop="userId" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="真实姓名" width="120" />
        <el-table-column prop="deptName" label="部门" width="120" />
        <el-table-column prop="roleNames" label="角色" min-width="140" />
        <el-table-column prop="email" label="邮箱" width="180" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lastLoginTime" label="最后登录" width="180" />
        <el-table-column label="操作" fixed="right" width="220">
          <template #default="{ row }">
            <el-button type="primary" link @click="router.push(`/user/edit/${row.id}`)">编辑</el-button>
            <el-button type="warning" link @click="handleResetPwd(row)">重置密码</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- Pagination -->
      <el-pagination
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top: 20px; justify-content: flex-end"
        @size-change="loadData"
        @current-change="loadData"
        background
      />
    </el-card>

    <!-- Add/Edit Dialog -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      destroy-on-close
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" :disabled="!!form.userId" />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="form.realName" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!form.userId">
          <el-input v-model="form.password" type="password" show-password placeholder="留空默认123456" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" class="btn-primary-green">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserList, createUser, updateUser, deleteUser, resetPassword } from '@/api/system/user'

const router = useRouter()
const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref()

const searchForm = reactive({
  username: '',
  realName: ''
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const form = reactive({
  userId: null,
  username: '',
  realName: '',
  password: '',
  email: '',
  phone: '',
  status: 1
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getUserList({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      ...searchForm
    })
    tableData.value = res.data.list
    pagination.total = res.data.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.pageNum = 1
  loadData()
}

const handleReset = () => {
  searchForm.username = ''
  searchForm.realName = ''
  handleSearch()
}

const handleAdd = () => {
  dialogTitle.value = '添加用户'
  Object.keys(form).forEach(key => {
    if (key !== 'status') form[key] = ''
    if (key === 'status') form[key] = 1
  })
  form.userId = null
  form.password = ''
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑用户'
  Object.assign(form, row)
  form.password = '' // 编辑时不显示密码
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  try {
    if (form.userId) {
      await updateUser(form)
      ElMessage.success('更新成功')
    } else {
      await createUser(form)
      ElMessage.success(form.password ? '创建成功' : '创建成功，默认密码：123456')
    }
    dialogVisible.value = false
    loadData()
  } catch (error) {
    console.error(error)
  }
}

const handleResetPwd = async (row) => {
  await ElMessageBox.confirm('确定要重置该用户密码为 123456 吗？', '提示', { type: 'warning' })
  try {
    await resetPassword(row.userId)
    ElMessage.success('密码重置成功')
  } catch (error) {
    console.error(error)
  }
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定要删除该用户吗？', '提示', { type: 'warning' })
  try {
    await deleteUser(row.userId)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.user-manage {
  .box-card {
    border-top: 3px solid #00a65a;
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .search-form {
    margin-bottom: 15px;
  }

  .user-table {
    font-size: 13px;

    :deep(.el-table__header th) {
      background-color: #f5f7fa;
      color: #333;
    }
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

.el-button--primary {
  color: #fff;
}
</style>
