<template>
  <div class="role-manage">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>角色列表</span>
          <el-button type="primary" @click="handleAdd" class="btn-primary-green">
            <el-icon><Plus /></el-icon>
            添加角色
          </el-button>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" stripe border class="role-table">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="roleName" label="名称" min-width="150" />
        <el-table-column prop="menuCount" label="权限值" width="100" align="center" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="200" show-overflow-tooltip />
        <el-table-column label="操作" fixed="right" width="240">
          <template #default="{ row }">
            <el-button type="primary" link @click="handlePermission(row)">设定</el-button>
            <el-button type="primary" link @click="handleEdit(row)">修改</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加/编辑角色对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="form.roleCode" placeholder="请输入角色编码" :disabled="!!form.id" />
        </el-form-item>
        <el-form-item label="角色描述" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入角色描述" />
        </el-form-item>
        <el-form-item label="数据范围" prop="dataScope">
          <el-select v-model="form.dataScope" placeholder="请选择数据范围" style="width: 100%">
            <el-option :value="1" label="全部数据" />
            <el-option :value="2" label="本部门数据" />
            <el-option :value="3" label="本部门及以下" />
            <el-option :value="4" label="仅本人数据" />
          </el-select>
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

    <!-- 权限设定对话框 -->
    <el-dialog v-model="permDialogVisible" title="权限设定" width="450px" destroy-on-close>
      <div class="perm-header">
        <span>角色名称：<strong>{{ currentRole?.roleName }}</strong></span>
      </div>
      <el-divider />
      <div class="perm-tree-container" v-loading="permLoading">
        <el-tree
          v-if="permLoaded"
          ref="treeRef"
          :data="menuTree"
          :props="treeProps"
          show-checkbox
          node-key="id"
          default-expand-all
          :default-checked-keys="checkedMenuIds"
          class="perm-tree"
        />
      </div>
      <template #footer>
        <el-button @click="permDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSavePermission" class="btn-primary-green">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRoleList, getRoleById, addRole, updateRole, deleteRole } from '@/api/system/role'
import { getMenuTree } from '@/api/system/menu'

const loading = ref(false)
const tableData = ref([])

// 表单
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref()
const form = reactive({
  id: null,
  roleName: '',
  roleCode: '',
  remark: '',
  dataScope: 1,
  status: 1
})

const rules = {
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  roleCode: [{ required: true, message: '请输入角色编码', trigger: 'blur' }]
}

// 权限设定
const permDialogVisible = ref(false)
const permLoading = ref(false)
const permLoaded = ref(false)
const currentRole = ref(null)
const menuTree = ref([])
const checkedMenuIds = ref([])
const treeRef = ref()
const treeProps = {
  children: 'children',
  label: 'menuName'
}

// 加载角色列表
async function loadRoles() {
  loading.value = true
  try {
    const res = await getRoleList({ pageNum: 1, pageSize: 100 })
    if (res.code === 200) {
      tableData.value = res.data?.records || (Array.isArray(res.data) ? res.data : [])
    } else {
      tableData.value = []
    }
  } catch (error) {
    console.error('加载角色列表失败:', error)
    tableData.value = []
  } finally {
    loading.value = false
  }
}

// 添加
function handleAdd() {
  dialogTitle.value = '添加角色'
  form.id = null
  form.roleName = ''
  form.roleCode = ''
  form.remark = ''
  form.dataScope = 1
  form.status = 1
  dialogVisible.value = true
}

// 编辑
async function handleEdit(row) {
  dialogTitle.value = '修改角色'
  try {
    const res = await getRoleById(row.id)
    if (res.code === 200 && res.data) {
      Object.assign(form, res.data)
    } else {
      Object.assign(form, row)
    }
  } catch {
    Object.assign(form, row)
  }
  dialogVisible.value = true
}

// 提交
async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  try {
    if (form.id) {
      await updateRole(form)
      ElMessage.success('修改成功')
    } else {
      await addRole(form)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadRoles()
  } catch (error) {
    ElMessage.error(form.id ? '修改失败' : '添加失败')
  }
}

// 权限设定
async function handlePermission(row) {
  currentRole.value = row
  checkedMenuIds.value = []
  menuTree.value = []
  permLoaded.value = false
  permDialogVisible.value = true
  permLoading.value = true

  try {
    // 并行加载菜单树和角色已有权限
    const [treeRes, roleRes] = await Promise.all([
      getMenuTree(),
      getRoleById(row.id)
    ])

    // 先设置菜单树，用于计算父节点ID
    if (treeRes.code === 200) {
      menuTree.value = treeRes.data || []
    }

    // 再设置已选菜单ID（过滤掉父节点ID，避免父节点勾选导致所有子节点全选）
    if (roleRes.code === 200 && roleRes.data?.menuIds) {
      const parentIds = new Set()
      function collectParentIds(nodes) {
        for (const node of nodes) {
          if (node.children && node.children.length > 0) {
            parentIds.add(node.id)
            collectParentIds(node.children)
          }
        }
      }
      collectParentIds(menuTree.value)
      checkedMenuIds.value = roleRes.data.menuIds
        .map(Number)
        .filter(id => !parentIds.has(id))
    }

    // 标记加载完成，显示树（v-if="permLoaded" 确保节点渲染后再应用选中状态）
    permLoaded.value = true
  } catch (error) {
    console.error('加载权限数据失败:', error)
  } finally {
    permLoading.value = false
  }
}

// 保存权限
async function handleSavePermission() {
  const checkedKeys = treeRef.value?.getCheckedKeys() || []
  const halfCheckedKeys = treeRef.value?.getHalfCheckedKeys() || []
  const allKeys = [...new Set([...checkedKeys, ...halfCheckedKeys])]

  // 收集所有父节点ID（有children的菜单），保存时只存叶子节点
  const parentIds = new Set()
  function collectParentIds(nodes) {
    for (const node of nodes) {
      if (node.children && node.children.length > 0) {
        parentIds.add(node.id)
        collectParentIds(node.children)
      }
    }
  }
  collectParentIds(menuTree.value)

  // 过滤掉父节点ID，只保存叶子节点
  const leafKeys = allKeys.filter(id => !parentIds.has(id))

  try {
    await updateRole({
      id: currentRole.value.id,
      menuIds: leafKeys
    })
    ElMessage.success('权限设定成功')
    permDialogVisible.value = false
    loadRoles()
  } catch (error) {
    ElMessage.error('权限设定失败')
  }
}

// 删除
async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(
      `确定要删除角色"${row.roleName}"吗？`,
      '提示',
      { type: 'warning' }
    )
    await deleteRole(row.id)
    ElMessage.success('删除成功')
    loadRoles()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  loadRoles()
})
</script>

<style scoped lang="scss">
.role-manage {
  .box-card {
    border-top: 3px solid #00a65a;
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .role-table {
    font-size: 13px;

    :deep(.el-table__header th) {
      background-color: #f5f7fa;
      color: #333;
    }
  }

  .perm-header {
    font-size: 14px;
    margin-bottom: 5px;
  }

  .perm-tree-container {
    max-height: 400px;
    overflow-y: auto;
  }

  .perm-tree {
    font-size: 14px;
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
