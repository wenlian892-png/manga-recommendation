<template>
  <div class="profile-view">
    <div class="profile-header">
      <h1>个人中心</h1>
    </div>

    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-icon">📊</div>
        <div class="stat-info">
          <p class="stat-value">{{ stats.todayRead }}</p>
          <p class="stat-label">今日阅读</p>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">📚</div>
        <div class="stat-info">
          <p class="stat-value">{{ stats.totalRead }}</p>
          <p class="stat-label">总阅读</p>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">⭐</div>
        <div class="stat-info">
          <p class="stat-value">{{ userScores.length }}</p>
          <p class="stat-label">评分作品</p>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">📖</div>
        <div class="stat-info">
          <p class="stat-value">{{ userCollections.length }}</p>
          <p class="stat-label">收藏作品</p>
        </div>
      </div>
    </div>

    <el-tabs v-model="activeTab" class="profile-tabs">
      <el-tab-pane label="我的评分" name="scores">
        <div class="content-section">
          <div v-if="userScores.length === 0" class="empty-state">
            <p>暂无评分记录</p>
          </div>
          <div v-else class="item-list">
            <button v-for="item in userScores" :key="item.scoreId" class="item-card" @click="goManga(item.mangaId)" type="button">
              <img :src="item.mangaCover || '/placeholder.png'" :alt="item.mangaTitle || '漫画封面'" class="item-cover" loading="lazy" width="60" height="80" />
              <div class="item-info">
                <h3 class="item-title">{{ item.mangaTitle }}</h3>
                <div class="item-meta">
                  <el-rate v-model="item.score" disabled size="small" />
                  <span class="item-date">{{ formatDate(item.createTime) }}</span>
                </div>
              </div>
            </button>
          </div>
        </div>
      </el-tab-pane>

      <el-tab-pane label="我的书架" name="collections">
        <div class="content-section">
          <div v-if="userCollections.length === 0" class="empty-state">
            <p>暂无收藏</p>
          </div>
          <div v-else class="item-grid">
            <button v-for="item in userCollections" :key="item.mangaId" class="manga-card" @click="goManga(item.mangaId)" type="button">
              <img :src="item.mangaCover || '/placeholder.png'" :alt="item.mangaTitle || '漫画封面'" class="manga-cover" loading="lazy" width="200" height="200" />
              <div class="manga-info">
                <h3 class="manga-title">{{ item.mangaTitle }}</h3>
                <el-tag size="small">{{ item.category }}</el-tag>
              </div>
            </button>
          </div>
        </div>
      </el-tab-pane>

      <el-tab-pane label="阅读历史" name="history">
        <div class="content-section">
          <div v-if="readHistory.length === 0" class="empty-state">
            <p>暂无阅读记录</p>
          </div>
          <div v-else class="item-list">
            <button v-for="item in readHistory" :key="item.mangaId + '-' + item.readTime" class="item-card history-card" @click="goManga(item.mangaId)" type="button">
              <img :src="item.mangaCover || '/placeholder.png'" :alt="item.mangaTitle || '漫画封面'" class="item-cover" loading="lazy" width="60" height="80" />
              <div class="item-info">
                <h3 class="item-title">{{ item.mangaTitle }}</h3>
                <p class="item-chapter">阅读至: {{ item.chapterTitle || '第一章' }}</p>
                <span class="item-date">{{ formatDate(item.readTime) }}</span>
              </div>
            </button>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const activeTab = ref('scores')
const stats = ref({ todayRead: 0, totalRead: 0 })
const userScores = ref([])
const userCollections = ref([])
const readHistory = ref([])

async function fetchStats() {
  try {
    const res = await request.get('/manga/read-stats')
    stats.value = res.data || { todayRead: 0, totalRead: 0 }
  } catch (e) {
    console.error(e)
  }
}

async function fetchUserScores() {
  try {
    const res = await request.get('/user/scores')
    userScores.value = res.data || []
  } catch (e) {
    console.error(e)
  }
}

async function fetchUserCollections() {
  try {
    const res = await request.get('/user/collections')
    userCollections.value = res.data || []
  } catch (e) {
    console.error(e)
  }
}

async function fetchReadHistory() {
  try {
    const res = await request.get('/user/read-history')
    readHistory.value = res.data || []
  } catch (e) {
    console.error(e)
  }
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN')
}

function goManga(mangaId) {
  router.push(`/manga/${mangaId}`)
}

onMounted(() => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/')
    return
  }
  fetchStats()
  fetchUserScores()
  fetchUserCollections()
  fetchReadHistory()
})
</script>

<style scoped>
.profile-view {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.profile-header {
  margin-bottom: 24px;
}

.profile-header h1 {
  font-size: 28px;
  color: #333;
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.stat-icon {
  font-size: 32px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #333;
  margin: 0;
}

.stat-label {
  font-size: 14px;
  color: #999;
  margin: 4px 0 0 0;
}

.profile-tabs {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.content-section {
  min-height: 300px;
}

.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 200px;
  color: #999;
  font-size: 16px;
}

.item-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.item-card {
  display: flex;
  gap: 16px;
  padding: 12px;
  background: #f9f9f9;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.2s;
  border: none;
  text-align: left;
  width: 100%;
}

.item-card:hover {
  background: #f0f0f0;
}

.item-cover {
  width: 60px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
}

.item-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.item-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.item-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 4px;
}

.item-chapter {
  font-size: 13px;
  color: #666;
  margin: 0;
}

.item-date {
  font-size: 12px;
  color: #999;
}

.item-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 16px;
}

.manga-card {
  background: #f9f9f9;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  border: none;
  text-align: left;
  width: 100%;
  padding: 0;
}

.manga-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.manga-cover {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.manga-info {
  padding: 12px;
}

.manga-title {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin: 0 0 8px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.history-card .item-info {
  justify-content: center;
}

@media (max-width: 768px) {
  .stats-cards {
    grid-template-columns: repeat(2, 1fr);
  }

  .item-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>