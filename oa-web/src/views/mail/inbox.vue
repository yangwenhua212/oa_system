<template>
  <div class="mail-page">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>收件箱</span>
          <el-button type="primary" class="btn-primary-green" @click="$router.push('/mail/compose')">
            <el-icon><Edit /></el-icon>
            写邮件
          </el-button>
        </div>
      </template>
      <el-table :data="tableData" stripe border>
        <el-table-column prop="sender" label="发件人" width="150" />
        <el-table-column prop="subject" label="主题" />
        <el-table-column prop="time" label="时间" width="180" />
        <el-table-column label="操作" width="100">
          <template #default>
            <el-button type="primary" link>查看</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const tableData = ref([
  { sender: '张三', subject: '项目进度汇报', time: '2026-04-29 10:30' },
  { sender: '李四', subject: '会议通知', time: '2026-04-29 09:15' }
])
</script>

<style scoped lang="scss">
.mail-page {
  .box-card {
    border-top: 3px solid #00a65a;
  }
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
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
