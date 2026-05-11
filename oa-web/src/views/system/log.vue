<template>
  <div class="log-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户日志</span>
          <div class="header-actions">
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              size="small"
              style="margin-right: 10px"
              @change="handleSearch"
            />
            <el-select v-model="filterAction" placeholder="操作类型" size="small" clearable style="width: 120px; margin-right: 10px;" @change="handleSearch">
              <el-option label="登录" value="login" />
              <el-option label="登出" value="logout" />
              <el-option label="新增" value="create" />
              <el-option label="修改" value="update" />
              <el-option label="删除" value="delete" />
            </el-select>
            <el-input
              v-model="searchKeyword"
              placeholder="搜索操作人..."
              size="small"
              style="width: 150px"
              clearable
              @keyup.enter="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
          </div>
        </div>
      </template>

      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="userName" label="操作人" width="120" />
        <el-table-column prop="action" label="操作类型" width="100">
          <template #default="{ row }">
            <el-tag size="small" :type="getActionType(row.action)">{{ getActionText(row.action) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="ip" label="IP地址" width="140" />
        <el-table-column prop="module" label="操作模块" width="120" />
        <el-table-column prop="content" label="操作内容" min-width="200" />
        <el-table-column prop="createTime" label="操作时间" width="180" />
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getUserLogs } from '@/api/system'

const loading = ref(false)
const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dateRange = ref([])
const filterAction = ref('')
const searchKeyword = ref('')

async function loadData() {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
      action: filterAction.value,
      keyword: searchKeyword.value,
      startDate: dateRange.value?.[0],
      endDate: dateRange.value?.[1]
    }
    const res = await getUserLogs(params)
    tableData.value = res.data?.list || res.data || []
    total.value = res.data?.total || tableData.value.length
  } catch (error) {
    tableData.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  currentPage.value = 1
  loadData()
}

function getActionType(action) {
  const typeMap = {
    login: 'success',
    logout: 'info',
    create: 'primary',
    update: 'warning',
    delete: 'danger'
  }
  return typeMap[action] || 'info'
}

function getActionText(action) {
  const textMap = {
    login: '登录',
    logout: '登出',
    create: '新增',
    update: '修改',
    delete: '删除'
  }
  return textMap[action] || action
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.log-page {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .header-actions {
      display: flex;
      align-items: center;
    }
  }

  .pagination {
    display: flex;
    justify-content: flex-end;
    margin-top: 15px;
  }
}

.el-button--primary {
  color: #fff;
}
</style>
