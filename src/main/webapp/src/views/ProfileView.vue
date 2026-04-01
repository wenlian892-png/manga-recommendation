<template>
  <div class="profile-view">
    <div v-if="pageLoading">
      <PageSkeleton type="detail" :count="1" />
    </div>
    <template v-else>
      <div class="profile-header">
        <div class="user-card">
          <el-avatar :size="64" :icon="UserFilled" />
          <div class="user-info">
            <h1>{{ userStore.currentUser?.username }}</h1>
            <p>注册时间：{{ formatDate(userStore.currentUser?.createTime) }}</p>
          </div>
        </div>
      </div>

      <div class="stats-cards">
        <div class="stat-card">
          <div class="stat-icon stat-icon--read">
            <el-icon :size="28"><DataLine /></el-icon>
          </div>
          <div class="stat-info">
            <p class="stat-value">{{ stats.todayRead }}</p>
            <p class="stat-label">今日阅读</p>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon stat-icon--total">
            <el-icon :size="28"><Reading /></el-icon>
          </div>
          <div class="stat-info">
            <p class="stat-value">{{ stats.totalRead }}</p>
            <p class="stat-label">总阅读</p>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon stat-icon--score">
            <el-icon :size="28"><Star /></el-icon>
          </div>
          <div class="stat-info">
            <p class="stat-value">{{ userScores.length }}</p>
            <p class="stat-label">评分作品</p>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon stat-icon--collect">
            <el-icon :size="28"><CollectionTag /></el-icon>
          </div>
          <div class="stat-info">
            <p class="stat-value">{{ userCollections.length }}</p>
            <p class="stat-label">收藏作品</p>
          </div>
        </div>
      </div>

      <el-tabs v-model="activeTab" class="profile-tabs">
        <el-tab-pane label="我的评分" name="scores">
          <div class="content-section">
            <div v-if="userScores.length === 0">
              <EmptyState title="暂无评分记录" />
            </div>
            <div v-else class="item-list">
              <div v-for="item in userScores" :key="item.scoreId" class="item-card" @click="goManga(item.mangaId)">
                <img :src="item.mangaCover || '/placeholder.png'" class="item-cover" :alt="item.mangaTitle" loading="lazy" />
                <div class="item-info">
                  <h3 class="item-title">{{ item.mangaTitle }}</h3>
                  <div class="item-meta">
                    <el-rate v-model="item.score" disabled size="small" />
                    <span class="item-date">{{ formatDate(item.createTime) }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </el-tab-pane>

        <el-tab-pane label="我的书架" name="collections">
          <div class="content-section">
            <div v-if="userCollections.length === 0">
              <EmptyState title="暂无收藏" />
            </div>
            <div v-else class="item-grid">
              <MangaCard v-for="item in userCollections" :key="item.mangaId" :manga="{ id: item.mangaId, title: item.mangaTitle, author: '', coverUrl: item.mangaCover }">
                <template #extra>
                  <el-tag size="small">{{ item.category }}</el-tag>
                </template>
              </MangaCard>
            </div>
          </div>
        </el-tab-pane>

        <el-tab-pane label="阅读历史" name="history">
          <div class="content-section">
            <div v-if="readHistory.length === 0">
              <EmptyState title="暂无阅读记录" />
            </div>
            <div v-else class="item-list">
              <div v-for="item in readHistory" :key="item.mangaId + '-' + item.readTime" class="item-card history-card" @click="goManga(item.mangaId)">
                <img :src="item.mangaCover || '/placeholder.png'" class="item-cover" :alt="item.mangaTitle" loading="lazy" />
                <div class="item-info">
                  <h3 class="item-title">{{ item.mangaTitle }}</h3>
                  <p class="item-chapter">阅读至: {{ item.chapterTitle || '第一章' }}</p>
                  <span class="item-date">{{ formatDate(item.readTime) }}</span>
                </div>
              </div>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </template>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { UserFilled, DataLine, Reading, Star, CollectionTag } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { useUserStore } from '@/stores/user'
import { formatDate } from '@/utils/helpers'
import MangaCard from '@/components/MangaCard.vue'
import PageSkeleton from '@/components/PageSkeleton.vue'
import EmptyState from '@/components/EmptyState.vue'

const router = useRouter()
const userStore = useUserStore()

const pageLoading = ref(true)
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
  }
}

async function fetchUserScores() {
  try {
    const res = await request.get('/user/scores')
    userScores.value = res.data || []
  } catch (e) {
  }
}

async function fetchUserCollections() {
  try {
    const res = await request.get('/user/collections')
    userCollections.value = res.data || []
  } catch (e) {
  }
}

async function fetchReadHistory() {
  try {
    const res = await request.get('/user/read-history')
    readHistory.value = res.data || []
  } catch (e) {
  }
}

function goManga(mangaId) {
  router.push(`/manga/${mangaId}`)
}

onMounted(async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/')
    return
  }
  try {
    await Promise.all([
      fetchStats(),
      fetchUserScores(),
      fetchUserCollections(),
      fetchReadHistory()
    ])
  } finally {
    pageLoading.value = false
  }
})
</script>

<style scoped>
.profile-view {
  max-width: var(--max-width);
  margin: 0 auto;
  padding: var(--page-padding);
}

.profile-header {
  margin-bottom: var(--spacing-lg);
}

.user-card {
  display: flex;
  align-items: center;
  gap: var(--spacing-lg);
  background: var(--color-bg-white);
  border-radius: var(--radius-xl);
  padding: var(--spacing-xl);
  box-shadow: var(--shadow-md);
}

.user-info h1 {
  font-size: var(--font-size-hero);
  font-weight: var(--font-weight-bold);
  color: var(--color-text-primary);
  margin: 0 0 var(--spacing-xs) 0;
}

.user-info p {
  font-size: var(--font-size-base);
  color: var(--color-text-secondary);
  margin: 0;
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-lg);
}

.stat-card {
  background: var(--color-bg-white);
  border-radius: var(--radius-lg);
  padding: var(--spacing-lg);
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  box-shadow: var(--shadow-sm);
  transition: box-shadow var(--transition-normal);
}

.stat-card:hover {
  box-shadow: var(--shadow-md);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-icon--read {
  background: var(--color-primary-light);
  color: var(--color-primary);
}

.stat-icon--total {
  background: rgba(103, 194, 58, 0.1);
  color: var(--color-success);
}

.stat-icon--score {
  background: rgba(230, 162, 60, 0.1);
  color: var(--color-warning);
}

.stat-icon--collect {
  background: rgba(245, 108, 108, 0.1);
  color: var(--color-danger);
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: var(--font-weight-bold);
  color: var(--color-text-primary);
  margin: 0;
  line-height: 1.2;
}

.stat-label {
  font-size: var(--font-size-base);
  color: var(--color-text-secondary);
  margin: var(--spacing-xs) 0 0 0;
}

.profile-tabs {
  background: var(--color-bg-white);
  border-radius: var(--radius-lg);
  padding: var(--spacing-lg);
  box-shadow: var(--shadow-sm);
}

.content-section {
  min-height: 300px;
}

.item-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.item-card {
  display: flex;
  gap: var(--spacing-md);
  padding: var(--spacing-md);
  background: var(--color-bg-secondary);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: background var(--transition-fast);
}

.item-card:hover {
  background: var(--color-border-light);
}

.item-cover {
  width: 60px;
  height: 80px;
  object-fit: cover;
  border-radius: var(--radius-sm);
}

.item-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
}

.item-title {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
  margin: 0;
}

.item-meta {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  margin-top: var(--spacing-xs);
}

.item-chapter {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  margin: 0;
}

.item-date {
  font-size: var(--font-size-xs);
  color: var(--color-text-placeholder);
}

.item-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: var(--spacing-md);
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

  .user-card {
    flex-direction: column;
    text-align: center;
  }
}
</style>
