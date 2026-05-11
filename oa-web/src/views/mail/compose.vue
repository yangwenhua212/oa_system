<template>
  <div class="compose-page">
    <el-card class="box-card">
      <template #header>
        <span>写邮件</span>
      </template>
      <el-form label-width="80px">
        <el-form-item label="收件人">
          <el-input v-model="form.to" placeholder="请输入收件人" />
        </el-form-item>
        <el-form-item label="主题">
          <el-input v-model="form.subject" placeholder="请输入邮件主题" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="form.content" type="textarea" :rows="10" placeholder="请输入邮件内容" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="btn-primary-green">发送</el-button>
          <el-button>存为草稿</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive } from 'vue'

const form = reactive({
  to: '',
  subject: '',
  content: ''
})
</script>

<style scoped lang="scss">
.compose-page {
  .box-card {
    border-top: 3px solid #00a65a;
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
