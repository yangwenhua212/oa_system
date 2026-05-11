<template>
  <div class="user-edit-page">
    <!-- 顶部导航由 layout 提供，此处只需面包屑与返回 -->
    <div class="page-header">
      <div class="breadcrumb-area">
        <el-button text @click="goBack" class="back-btn">
          <el-icon><ArrowLeft /></el-icon>
          返回
        </el-button>
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item :to="{ path: '/user/list' }">用户管理</el-breadcrumb-item>
          <el-breadcrumb-item>编辑用户</el-breadcrumb-item>
        </el-breadcrumb>
      </div>
    </div>

    <!-- 表单卡片 -->
    <div class="form-container">
      <el-card class="form-card">
        <template #header>
          <span class="card-title">编辑用户信息</span>
        </template>

        <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" size="default">
          <el-row :gutter="40">
            <!-- 左列 -->
            <el-col :xs="24" :md="12">
              <el-form-item label="用户名" prop="username">
                <el-input v-model="form.username" disabled class="readonly-input" />
              </el-form-item>
              <el-form-item label="真实姓名" prop="realName">
                <el-input v-model="form.realName" placeholder="请输入真实姓名" />
              </el-form-item>
              <el-form-item label="地址" prop="address">
                <el-input v-model="form.address" placeholder="请输入地址" />
              </el-form-item>
              <el-form-item label="毕业院校" prop="school">
                <el-input v-model="form.school" placeholder="请输入毕业院校" />
              </el-form-item>
              <el-form-item label="银行账号" prop="bankAccount">
                <el-input v-model="form.bankAccount" placeholder="请输入银行账号" />
              </el-form-item>
              <el-form-item label="性别" prop="sex">
                <el-select v-model="form.sex" placeholder="请选择性别">
                  <el-option label="男" :value="1" />
                  <el-option label="女" :value="2" />
                  <el-option label="未知" :value="0" />
                </el-select>
              </el-form-item>
              <el-form-item label="角色" prop="role">
                <el-select v-model="form.role" placeholder="请选择角色">
                  <el-option v-for="r in roleOptions" :key="r.id" :label="r.roleName" :value="r.id" />
                </el-select>
              </el-form-item>
              <el-form-item label="皮肤" prop="skin">
                <el-select v-model="form.skin" placeholder="请选择皮肤">
                  <el-option label="经典蓝" value="blue" />
                  <el-option label="极简白" value="white" />
                  <el-option label="暗夜黑" value="dark" />
                </el-select>
              </el-form-item>
            </el-col>

            <!-- 右列 -->
            <el-col :xs="24" :md="12">
              <el-form-item label="电话" prop="phone">
                <el-input v-model="form.phone" placeholder="请输入电话" />
              </el-form-item>
              <el-form-item label="Email" prop="email">
                <el-input v-model="form.email" placeholder="请输入邮箱" />
              </el-form-item>
              <el-form-item label="学历" prop="education">
                <el-input v-model="form.education" placeholder="请输入学历" />
              </el-form-item>
              <el-form-item label="身份证号" prop="idCard">
                <el-input v-model="form.idCard" placeholder="请输入身份证号" />
              </el-form-item>
              <el-form-item label="部门" prop="deptId">
                <el-select v-model="form.deptId" placeholder="请选择部门">
                  <el-option v-for="d in deptOptions" :key="d.id" :label="d.deptName" :value="d.id" />
                </el-select>
              </el-form-item>
              <el-form-item label="职位" prop="positionId">
                <el-select v-model="form.positionId" placeholder="请选择职位">
                  <el-option v-for="p in positionOptions" :key="p.id" :label="p.positionName" :value="p.id" />
                </el-select>
              </el-form-item>
              <el-form-item label="工资" prop="salary">
                <el-input v-model="form.salary" placeholder="请输入工资" />
              </el-form-item>
            </el-col>
          </el-row>

          <!-- 重置密码 -->
          <el-divider />
          <el-form-item label="重置密码">
            <el-checkbox v-model="resetPwdChecked">勾选后可修改用户密码</el-checkbox>
          </el-form-item>
          <el-form-item v-if="resetPwdChecked" label="新密码" prop="newPassword">
            <el-input v-model="form.newPassword" type="password" show-password placeholder="请输入新密码" style="max-width:300px" />
          </el-form-item>

          <!-- 底部按钮 -->
          <el-divider />
          <div class="form-actions">
            <el-button type="primary" @click="handleSave" class="btn-primary-green" :loading="saving">
              保存
            </el-button>
            <el-button @click="goBack">取消</el-button>
          </div>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import { getUserById, updateUser } from '@/api/system/user'
import { getAllRoles } from '@/api/system/role'
import { getDeptTree } from '@/api/system/dept'
import { getPositions } from '@/api/system'

const route = useRoute()
const router = useRouter()
const formRef = ref()
const saving = ref(false)
const resetPwdChecked = ref(false)

const roleOptions = ref([])
const deptOptions = ref([])
const positionOptions = ref([])

const form = reactive({
  id: null,
  username: '',
  realName: '',
  address: '',
  school: '',
  bankAccount: '',
  sex: 1,
  role: null,
  skin: 'blue',
  phone: '',
  email: '',
  education: '',
  idCard: '',
  deptId: null,
  positionId: null,
  salary: '',
  newPassword: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }]
}

function goBack() {
  router.push('/user/list')
}

async function loadUser() {
  const id = route.params.id
  if (!id) return
  try {
    const res = await getUserById(id)
    if (res.code === 200 && res.data) {
      const u = res.data
      form.id = u.id
      form.username = u.username || ''
      form.realName = u.realName || ''
      form.phone = u.phone || ''
      form.email = u.email || ''
      form.address = u.address || ''
      form.school = u.school || ''
      form.bankAccount = u.bankAccount || ''
      form.education = u.education || ''
      form.idCard = u.idCard || ''
      form.salary = u.salary || ''
      form.skin = u.skin || 'blue'
      form.sex = u.sex ?? 1
      form.deptId = u.deptId || null
      form.positionId = u.positionId || null
      form.role = (u.roleIds && u.roleIds.length > 0) ? u.roleIds[0] : null
    }
  } catch (error) {
    console.error(error)
  }
}

function flattenDeptTree(tree) {
  const result = []
  function walk(nodes) {
    nodes.forEach(n => {
      result.push({ id: n.id, deptName: n.deptName })
      if (n.children && n.children.length > 0) walk(n.children)
    })
  }
  walk(tree)
  return result
}

async function loadOptions() {
  try {
    const [roleRes, deptRes, posRes] = await Promise.all([
      getAllRoles(),
      getDeptTree(),
      getPositions({ page: 1, pageSize: 999 })
    ])
    if (roleRes.code === 200) roleOptions.value = roleRes.data || []
    if (deptRes.code === 200) deptOptions.value = flattenDeptTree(deptRes.data || [])
    if (posRes.code === 200) positionOptions.value = posRes.data?.records || posRes.data || []
  } catch (error) {
    console.error(error)
  }
}

async function handleSave() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  saving.value = true
  try {
    const payload = {
      id: form.id,
      realName: form.realName,
      phone: form.phone,
      email: form.email,
      sex: form.sex,
      deptId: form.deptId,
      positionId: form.positionId,
      address: form.address,
      school: form.school,
      bankAccount: form.bankAccount,
      education: form.education,
      idCard: form.idCard,
      salary: form.salary,
      skin: form.skin,
      roleIds: form.role ? [form.role] : []
    }
    await updateUser(payload)
    ElMessage.success('保存成功')
    goBack()
  } catch (error) {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  loadUser()
  loadOptions()
})
</script>

<style scoped lang="scss">
.user-edit-page {
  min-height: 100%;

  .page-header {
    margin-bottom: 16px;

    .breadcrumb-area {
      display: flex;
      align-items: center;
      gap: 12px;

      .back-btn {
        padding: 0;
        font-size: 14px;
        color: #00a65a;

        &:hover {
          color: #008d4c;
        }
      }
    }
  }

  .form-card {
    border-top: 3px solid #00a65a;

    :deep(.el-card__header) {
      border-bottom: 1px solid #ebeef5;
      padding: 15px 20px;
    }

    .card-title {
      font-size: 16px;
      font-weight: 600;
      color: #333;
    }

    .readonly-input {
      :deep(.el-input__wrapper) {
        background-color: #f5f7fa;
      }
    }
  }

  .form-actions {
    display: flex;
    gap: 12px;
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
