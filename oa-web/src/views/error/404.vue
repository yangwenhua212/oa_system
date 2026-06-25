<template>
  <div class="page-404">
    <div class="container">
      <h1>404</h1>
      <p>抱歉，您访问的页面不存在</p>
      <el-button type="primary" @click="$router.push('/')">返回首页</el-button>
    </div>
  </div>
</template>

<script setup>
</script>

<style scoped>
.page-404 {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 60vh;
}
.container {
  text-align: center;
}
h1 {
  font-size: 120px;
  color: #00a65a;
  margin: 0;
  line-height: 1;
}
p {
  font-size: 18px;
  color: #666;
  margin: 20px 0 30px;
}
</style>
