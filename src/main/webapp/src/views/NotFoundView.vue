<template>
  <div class="not-found">
    <div class="content">
      <h1 class="code">404</h1>
      <h2 class="title">页面不存在</h2>
      <p class="desc">抱歉，您访问的页面可能已被删除或不存在</p>
      <el-button type="primary" @click="goHome">返回首页</el-button>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

function goHome() {
  router.push({ name: 'Home' })
}
</script>

<style scoped>
.not-found {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: calc(100vh - 200px);
}

.content {
  text-align: center;
}

.code {
  font-size: 120px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 16px;
}

.title {
  font-size: 28px;
  color: #333;
  margin-bottom: 12px;
}

.desc {
  font-size: 16px;
  color: #999;
  margin-bottom: 32px;
}
</style>
