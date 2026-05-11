<template>
  <div class="menu-manage">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>菜单管理</span>
          <el-button type="primary" class="btn-primary-green">
            <el-icon><Plus /></el-icon>
            添加菜单
          </el-button>
        </div>
      </template>

      <el-table
        :data="tableData"
        v-loading="loading"
        default-expand-all
        row-key="menuId"
        stripe
        border
        class="menu-table"
      >
        <el-table-column prop="menuName" label="菜单名称" width="200" />
        <el-table-column prop="icon" label="图标" width="100" align="center">
          <template #default="{ row }">
            <el-icon><component :is="row.icon" /></el-icon>
          </template>
        </el-table-column>
        <el-table-column prop="menuType" label="菜单类型" width="120" align="center">
          <template #default="{ row }">
            <el-tag size="small" :type="row.menuType === 'menu' ? 'success' : 'warning'">
              {{ row.menuType === 'menu' ? '菜单' : '按钮' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="path" label="路由路径" width="200" />
        <el-table-column prop="component" label="组件路径" />
        <el-table-column prop="sortOrder" label="排序" width="80" align="center" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="180">
          <template #default>
            <el-button type="primary" link>编辑</el-button>
            <el-button type="danger" link>删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const loading = ref(false)
const tableData = ref([
  {
    menuId: 1,
    menuName: '系统管理',
    icon: 'Setting',
    menuType: 'menu',
    path: '/system',
    component: '',
    sortOrder: 1,
    status: 1,
    children: [
      { menuId: 11, menuName: '用户管理', icon: 'User', menuType: 'menu', path: '/system/user', component: 'system/user/index', sortOrder: 1, status: 1 },
      { menuId: 12, menuName: '角色管理', icon: 'Role', menuType: 'menu', path: '/system/role', component: 'system/role/index', sortOrder: 2, status: 1 },
      { menuId: 13, menuName: '部门管理', icon: 'Office', menuType: 'menu', path: '/system/dept', component: 'system/dept/index', sortOrder: 3, status: 1 }
    ]
  }
])

onMounted(() => {
  // Load menu data
})
</script>

<style scoped lang="scss">
.menu-manage {
  .box-card {
    border-top: 3px solid #00a65a;
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .menu-table {
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
