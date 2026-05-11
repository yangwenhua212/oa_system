<template>
  <div class="mail-account-page">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>账号管理</span>
          <el-button type="primary" @click="handleAdd" class="btn-primary-green">
            <el-icon><Plus /></el-icon>
            新增账号
          </el-button>
        </div>
      </template>

      <!-- 搜索 -->
      <div class="search-bar">
        <el-form :inline="true">
          <el-form-item label="账号名">
            <el-input v-model="searchForm.accountName" placeholder="请输入账号名" clearable @keyup.enter="loadData" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="loadData" class="btn-primary-green">查询</el-button>
            <el-button @click="searchForm.accountName = ''; loadData()">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="accountType" label="类型" width="120">
          <template #default="{ row }">
            <el-tag v-if="row.accountType === 'system'" type="success" size="small">系统邮件</el-tag>
            <el-tag v-else type="info" size="small">{{ row.accountType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="accountName" label="账号名" min-width="150" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '有效' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" align="center">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEdit(row)">修改</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="loadData"
          @current-change="loadData"
          background
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '修改账号' : '新增账号'" width="500px" destroy-on-close>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
        <el-form-item label="类型" prop="accountType">
          <el-select v-model="form.accountType" style="width:100%">
            <el-option label="系统邮件" value="system" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" style="width:100%">
            <el-option label="有效" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="名称" prop="accountName">
          <el-input v-model="form.accountName" placeholder="请输入名称" />
        </el-form-item>
        <el-form-item label="发件昵称">
          <el-input v-model="form.senderNickname" placeholder="请输入发件昵称" />
        </el-form-item>
        <el-form-item label="邮件账号" prop="accountAddr">
          <el-input v-model="form.accountAddr" placeholder="请输入邮件账号" />
        </el-form-item>
        <el-form-item label="授权码">
          <el-input v-model="form.authCode" type="password" show-password placeholder="请输入授权码" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave" class="btn-primary-green">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAccountList, createAccount, updateAccount, deleteAccount } from '@/api/mailAccount'

const loading = ref(false)
const tableData = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()

const searchForm = reactive({ accountName: '' })

const form = reactive({
  id: null,
  accountType: 'system',
  status: 1,
  accountName: '',
  senderNickname: '',
  accountAddr: '',
  authCode: '',
  remark: ''
})

const rules = {
  accountType: [{ required: true, message: '请选择类型', trigger: 'change' }],
  accountName: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  accountAddr: [{ required: true, message: '请输入邮件账号', trigger: 'blur' }]
}

async function loadData() {
  loading.value = true
  try {
    const res = await getAccountList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      params: { accountName: searchForm.accountName || null }
    })
    if (res.code === 200) {
      tableData.value = res.data?.records || []
      total.value = res.data?.total || 0
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

function handleAdd() {
  isEdit.value = false
  Object.assign(form, { id: null, accountType: 'system', status: 1, accountName: '', senderNickname: '', accountAddr: '', authCode: '', remark: '' })
  dialogVisible.value = true
}

function handleEdit(row) {
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

async function handleSave() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  try {
    if (form.id) {
      await updateAccount(form)
      ElMessage.success('修改成功')
    } else {
      await createAccount(form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error(form.id ? '修改失败' : '新增失败')
  }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确定要删除账号"${row.accountName}"吗？`, '提示', { type: 'warning' })
    await deleteAccount(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('删除失败')
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.mail-account-page {
  .box-card {
    border-top: 3px solid #00a65a;
  }
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  .search-bar {
    margin-bottom: 15px;
    padding: 10px 15px;
    background: #f5f7fa;
    border-radius: 6px;
  }
  .pagination {
    display: flex;
    justify-content: flex-end;
    margin-top: 15px;
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
