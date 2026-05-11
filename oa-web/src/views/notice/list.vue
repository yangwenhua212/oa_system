<template>
  <div class="notice-page">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>公告列表</span>
          <el-button type="primary" class="btn-primary-green" @click="handlePublish">
            <el-icon><Plus /></el-icon>
            发布公告
          </el-button>
        </div>
      </template>

      <!-- 搜索筛选 -->
      <div class="search-bar">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索公告标题"
          style="width: 200px"
          clearable
          @clear="loadData"
          @keyup.enter="loadData"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>

        <el-select
          v-model="filterType"
          placeholder="公告类型"
          style="width: 150px; margin-left: 10px"
          clearable
          @change="loadData"
        >
          <el-option label="全部类型" value="" />
          <el-option label="系统通知" value="system" />
          <el-option label="活动公告" value="activity" />
          <el-option label="会议通知" value="meeting" />
          <el-option label="重要提醒" value="important" />
        </el-select>

        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          style="margin-left: 10px; width: 250px"
          value-format="YYYY-MM-DD"
          @change="loadData"
        />

        <el-checkbox
          v-model="showTopOnly"
          style="margin-left: 15px"
          @change="loadData"
        >
          仅显示置顶
        </el-checkbox>
      </div>

      <!-- 公告列表 -->
      <el-table
        :data="tableData"
        stripe
        border
        v-loading="loading"
        class="notice-table"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="title" label="标题" min-width="200">
          <template #default="{ row }">
            <div class="title-cell">
              <el-tag v-if="row.isTop" type="danger" size="small">置顶</el-tag>
              <el-link type="primary" @click="viewDetail(row)">
                {{ row.title }}
              </el-link>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="noticeType" label="类型" width="120">
          <template #default="{ row }">
            <el-tag size="small" :type="getTypeColor(row.noticeType)">
              {{ getTypeName(row.noticeType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="发布人" width="100">
          <template #default="{ row }">
            {{ row.publisherName || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="发布时间" width="180">
          <template #default="{ row }">
            {{ row.publishTime ? dayjs(row.publishTime).format('YYYY-MM-DD HH:mm') : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="浏览量" width="100" align="center" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 || row.status === 'published' ? 'success' : 'info'" size="small">
              {{ row.status === 1 || row.status === 'published' ? '已发布' : '草稿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="viewDetail(row)">查看</el-button>
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
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
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getNoticeList, deleteNotice } from '@/api/notice'
import dayjs from 'dayjs'

const router = useRouter()

const loading = ref(false)
const searchKeyword = ref('')
const filterType = ref('')
const dateRange = ref([])
const showTopOnly = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const tableData = ref([])

const getTypeColor = (type) => {
  const colors = { system: 'primary', activity: 'success', meeting: 'warning', important: 'danger' }
  return colors[type] || 'info'
}

const getTypeName = (type) => {
  const names = { system: '系统通知', activity: '活动公告', meeting: '会议通知', important: '重要提醒' }
  return names[type] || '公告'
}

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      params: {
        title: searchKeyword.value || null,
        noticeType: filterType.value || null
      }
    }
    const res = await getNoticeList(params)
    if (res.code === 200 && res.data) {
      let records = res.data.records || []
      // 日期筛选（前端过滤）
      if (dateRange.value && dateRange.value.length === 2) {
        records = records.filter(item => {
          const d = dayjs(item.publishTime).format('YYYY-MM-DD')
          return d >= dateRange.value[0] && d <= dateRange.value[1]
        })
      }
      // 置顶筛选（前端过滤）
      if (showTopOnly.value) {
        records = records.filter(item => item.isTop)
      }
      tableData.value = records
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('加载公告列表失败', error)
  } finally {
    loading.value = false
  }
}

const handlePublish = () => {
  router.push('/notice/publish')
}

const viewDetail = (row) => {
  router.push(`/notice/detail?id=${row.id}`)
}

const handleEdit = (row) => {
  router.push(`/notice/publish?id=${row.id}`)
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除公告「${row.title}」吗？删除后无法恢复。`,
      '删除确认',
      { type: 'warning', confirmButtonText: '确定删除', cancelButtonText: '取消' }
    )
    await deleteNotice(row.id)
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
.notice-page {
  .box-card {
    border-top: 3px solid #00a65a;
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .search-bar {
    margin-bottom: 20px;
    display: flex;
    align-items: center;
  }

  .notice-table {
    .title-cell {
      display: flex;
      align-items: center;
      gap: 8px;
    }
  }

  .pagination-wrapper {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
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
</style>
