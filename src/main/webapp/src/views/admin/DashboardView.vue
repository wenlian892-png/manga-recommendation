<template>
  <div class="dashboard-view">
    <h1>数据大盘</h1>
    <div v-if="loading" class="loading">加载中...</div>
    <div v-else class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon">👥</div>
        <div class="stat-info">
          <p class="stat-value">{{ stats.totalUsers || 0 }}</p>
          <p class="stat-label">总用户数</p>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">📚</div>
        <div class="stat-info">
          <p class="stat-value">{{ stats.totalManga || 0 }}</p>
          <p class="stat-label">漫画资源</p>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">⭐</div>
        <div class="stat-info">
          <p class="stat-value">{{ stats.totalScores || 0 }}</p>
          <p class="stat-label">评分记录</p>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">📖</div>
        <div class="stat-info">
          <p class="stat-value">{{ stats.todayReadCount || 0 }}</p>
          <p class="stat-label">今日阅读</p>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">❤️</div>
        <div class="stat-info">
          <p class="stat-value">{{ stats.totalCollections || 0 }}</p>
          <p class="stat-label">收藏总数</p>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">💬</div>
        <div class="stat-info">
          <p class="stat-value">{{ stats.totalPosts || 0 }}</p>
          <p class="stat-label">论坛帖子</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'

const loading = ref(false)
const stats = ref({})

async function fetchStats() {
  loading.value = true
  try {
    const res = await request.get('/admin/dashboard/stats')
    stats.value = res.data || {}
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchStats()
})
</script>

<style scoped>
.dashboard-view {
  padding: 0 10px;
}

h1 {
  font-size: 24px;
  margin-bottom: 24px;
}

.loading {
  text-align: center;
  padding: 60px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  font-size: 36px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #333;
}

.stat-label {
  font-size: 14px;
  color: #999;
  margin-top: 4px;
}
</style>
