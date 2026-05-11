<template>
  <div class="online-page">
    <el-card class="main-card">
      <template #header>
        <div class="card-header">
          <span>在线用户</span>
          <el-button type="primary" size="small" @click="loadOnlineUsers">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </template>

      <el-table :data="userList" stripe v-loading="loading" border>
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column prop="realName" label="姓名" min-width="120" />
        <el-table-column prop="deptName" label="部门" min-width="120" />
        <el-table-column label="状态" width="100" align="center">
          <template #default>
            <el-tag type="success" size="small">在线</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="登录时间" min-width="180">
          <template #default="{ row }">
            {{ row.lastLoginTime ? dayjs(row.lastLoginTime).format('YYYY-MM-DD HH:mm:ss') : '从未登录' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" align="center">
          <template #default="{ row }">
            <el-button type="danger" size="small" @click="handleForceLogout(row)">强制下线</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import { getOnlineUsers } from '@/api/system'

const loading = ref(false)
const userList = ref([])

async function loadOnlineUsers() {
  loading.value = true
  try {
    const res = await getOnlineUsers()
    if (res.code === 200) userList.value = res.data || []
  } catch (error) {
    console.error('加载在线用户失败:', error)
  } finally {
    loading.value = false
  }
}

function handleForceLogout() {
  ElMessageBox.alert('强制下线功能需后端 JWT 黑名单支持，暂未实现', '提示', { type: 'info' })
}

onMounted(() => {
  loadOnlineUsers()
})
</script>

<style scoped lang="scss">
.online-page {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}

.el-button--primary {
  color: #fff;
}
</style>
