<template>
  <div class="dept-manage">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>部门管理</span>
          <el-button type="primary" @click="handleAdd(null)" class="btn-primary-green">
            <el-icon><Plus /></el-icon>
            添加部门
          </el-button>
        </div>
      </template>

      <el-table
        :data="tableData"
        v-loading="loading"
        default-expand-all
        row-key="id"
        stripe
        border
        class="dept-table"
      >
        <el-table-column prop="deptName" label="部门名称" width="200" />
        <el-table-column prop="deptCode" label="部门编码" width="150" />
        <el-table-column prop="sort" label="排序" width="100" align="center" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="300" align="center">
          <template #default="{ row }">
            <div style="display:flex;gap:4px;justify-content:center;flex-wrap:nowrap">
              <el-button type="primary" link size="small" @click="handleAdd(row)">添加下级</el-button>
              <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
              <el-button type="warning" link size="small" @click="handleTransfer">调动</el-button>
              <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 人事调动对话框 -->
    <el-dialog v-model="transferVisible" title="人事调动" width="500px" destroy-on-close>
      <el-form :model="transferForm" label-width="100px">
        <el-form-item label="选择员工">
          <el-select v-model="transferForm.userId" filterable placeholder="请选择员工" style="width:100%" @change="onUserChange">
            <el-option v-for="u in allUsers" :key="u.id" :label="`${u.realName} (${u.username})`" :value="u.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="原部门">
          <el-input :model-value="transferForm.sourceDept" disabled class="readonly-input" />
        </el-form-item>
        <el-form-item label="目标部门" prop="targetDeptId">
          <el-tree-select
            v-model="transferForm.targetDeptId"
            :data="deptOptions"
            :props="{ label: 'deptName', value: 'id', children: 'children' }"
            check-strictly
            clearable
            placeholder="请选择目标部门"
          />
        </el-form-item>
        <el-form-item label="调动原因">
          <el-input v-model="transferForm.reason" type="textarea" :rows="3" placeholder="请输入调动原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="transferVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitTransfer" class="btn-primary-green" :loading="transferLoading">确认调动</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="上级部门">
          <el-tree-select
            v-model="form.parentId"
            :data="deptOptions"
            :props="{ label: 'deptName', value: 'id', children: 'children' }"
            check-strictly
            clearable
            placeholder="请选择上级部门"
          />
        </el-form-item>
        <el-form-item label="部门名称" prop="deptName">
          <el-input v-model="form.deptName" />
        </el-form-item>
        <el-form-item label="部门编码" prop="deptCode">
          <el-input v-model="form.deptCode" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" />
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
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getDeptTree, createDept, updateDept, deleteDept } from '@/api/system/dept'
import { getUserList, updateUser } from '@/api/system/user'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref()

// 人事调动
const transferVisible = ref(false)
const transferLoading = ref(false)
const allUsers = ref([])
const deptNameMap = ref({})
const transferForm = reactive({
  userId: null,
  sourceDept: '',
  sourceDeptId: null,
  targetDeptId: null,
  reason: ''
})

const form = reactive({
  id: null,
  parentId: 0,
  deptName: '',
  deptCode: '',
  sort: 0,
  status: 1
})

const rules = {
  deptName: [{ required: true, message: '请输入部门名称', trigger: 'blur' }],
  deptCode: [{ required: true, message: '请输入部门编码', trigger: 'blur' }]
}

const deptOptions = computed(() => {
  const addRoot = [{ id: 0, deptName: '顶级部门', children: [] }]
  return [...addRoot, ...tableData.value]
})

const loadData = async () => {
  loading.value = true
  try {
    const res = await getDeptTree()
    tableData.value = res.data
    buildDeptMap(tableData.value)
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleAdd = (parent) => {
  dialogTitle.value = '添加部门'
  form.id = null
  form.parentId = parent?.id || 0
  form.deptName = ''
  form.deptCode = ''
  form.sort = 1
  form.status = 1
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑部门'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  try {
    if (form.id) {
      await updateDept(form)
      ElMessage.success('更新成功')
    } else {
      await createDept(form)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (error) {
    console.error(error)
  }
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定要删除该部门吗？', '提示', { type: 'warning' })
  try {
    await deleteDept(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    console.error(error)
  }
}

// 人事调动
function buildDeptMap(tree) {
  tree.forEach(d => {
    deptNameMap.value[d.id] = d.deptName
    if (d.children && d.children.length) buildDeptMap(d.children)
  })
}

async function loadUsers() {
  try {
    const res = await getUserList({ pageNum: 1, pageSize: 999 })
    if (res.code === 200) {
      allUsers.value = res.data?.list || res.data?.records || []
      allUsers.value.forEach(u => { u.password = null })
    }
  } catch (error) {
    console.error(error)
  }
}

function handleTransfer() {
  transferForm.userId = null
  transferForm.sourceDept = ''
  transferForm.sourceDeptId = null
  transferForm.targetDeptId = null
  transferForm.reason = ''
  transferVisible.value = true
  loadUsers()
}

function onUserChange(userId) {
  const user = allUsers.value.find(u => u.id === userId)
  if (user) {
    transferForm.sourceDeptId = user.deptId
    transferForm.sourceDept = deptNameMap.value[user.deptId] || '无部门'
  }
}

async function handleSubmitTransfer() {
  if (!transferForm.userId) return ElMessage.warning('请选择员工')
  if (!transferForm.targetDeptId) return ElMessage.warning('请选择目标部门')
  if (transferForm.sourceDeptId === transferForm.targetDeptId) return ElMessage.warning('目标部门与原部门相同')

  transferLoading.value = true
  try {
    await updateUser({ id: transferForm.userId, deptId: transferForm.targetDeptId })
    ElMessage.success('调动成功')
    transferVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error('调动失败')
  } finally {
    transferLoading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.dept-manage {
  .box-card {
    border-top: 3px solid #00a65a;
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .dept-table {
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

.readonly-input {
  :deep(.el-input__wrapper) {
    background-color: #f5f7fa;
  }
}

.el-button--primary {
  color: #fff;
}
</style>
