<template>
  <div class="manga-detail">
    <div v-if="loading">
      <PageSkeleton type="detail" />
    </div>
    <template v-else>
      <div v-if="!manga"><EmptyState title="漫画不存在" /></div>
      <template v-else>
        <nav class="breadcrumb">
          <router-link to="/">首页</router-link>
          <span class="separator">/</span>
          <router-link to="/category">漫画</router-link>
          <span class="separator">/</span>
          <span class="current">{{ manga.title || '加载中...' }}</span>
        </nav>

        <div class="detail-hero">
          <div class="hero-cover">
            <img :src="manga.coverUrl || '/placeholder.png'" class="cover-img" :alt="manga.title" />
          </div>
          <div class="hero-info">
            <h1 class="manga-title">{{ manga.title }}</h1>
            <div class="info-tags">
              <el-tag v-if="manga.category" size="small">{{ manga.category }}</el-tag>
              <el-tag :type="manga.status === 1 ? 'success' : 'info'" size="small">
                {{ manga.status === 1 ? '连载中' : '已完结' }}
              </el-tag>
            </div>
            <div class="info-grid">
              <div class="info-item">
                <span class="info-label">作者</span>
                <span class="info-value">{{ manga.author || '未知' }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">点击量</span>
                <span class="info-value">{{ manga.clickCount || 0 }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">平均评分</span>
                <span class="info-value score-highlight" v-if="manga.avgScore">
                  ⭐ {{ manga.avgScore.toFixed(1) }}
                </span>
                <span class="info-value" v-else>暂无评分</span>
              </div>
            </div>
            <p class="manga-desc">{{ manga.description || '暂无简介' }}</p>
            <div class="action-buttons">
              <el-button type="primary" size="large" @click="handleRead">
                <el-icon><Reading /></el-icon>
                开始阅读
              </el-button>
              <el-button
                size="large"
                :type="isCollected ? 'info' : 'default'"
                class="collect-btn"
                :class="{ 'is-collected': isCollected }"
                @click="handleCollect"
              >
                <el-icon><Collection /></el-icon>
                {{ isCollected ? '已加入书架' : '加入书架' }}
              </el-button>
            </div>
          </div>
        </div>

        <div class="detail-section rating-section">
          <h2 class="section-title">
            <el-icon><Star /></el-icon>
            为漫画评分
          </h2>
          <div class="rating-card">
            <p class="rating-hint">您的评分将帮助我们优化推荐算法</p>
            <div class="star-rating">
              <el-rate
                v-model="scoreValue"
                :colors="['#99A9BF', '#F7BA2A', '#FF9900']"
                show-text
                :texts="['很差', '较差', '一般', '推荐', '强烈推荐']"
                size="large"
              />
            </div>
            <el-button type="primary" class="submit-score-btn" @click="submitScore">
              提交评分
            </el-button>
          </div>
        </div>
      </template>
    </template>

    <el-dialog v-model="showScoreDialog" title="确认评分" width="360px" center>
      <div class="score-confirm">
        <p class="confirm-text">您为《{{ manga?.title }}》评分为：</p>
        <div class="confirm-stars">
          <el-rate v-model="scoreValue" disabled size="large" />
        </div>
        <p class="confirm-tip">评分提交后无法修改，确认提交吗？</p>
      </div>
      <template #footer>
        <el-button @click="showScoreDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmScore">确认提交</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showChaptersDialog" :title="'《' + manga?.title + '》章节目录'" width="500px" center>
      <div class="chapters-list">
        <div v-if="chapters.length === 0" class="empty-chapters">暂无章节</div>
        <button v-else class="chapter-item" v-for="chapter in chapters" :key="chapter.id" @click="selectChapter(chapter)">
          <span class="chapter-title">{{ chapter.title }}</span>
          <el-button type="primary" size="small">阅读</el-button>
        </button>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Reading, Collection, Star } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { useUserStore } from '@/stores/user'
import PageSkeleton from '@/components/PageSkeleton.vue'
import EmptyState from '@/components/EmptyState.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const manga = ref(null)
const showScoreDialog = ref(false)
const scoreValue = ref(5)
const isCollected = ref(false)
const chapters = ref([])
const showChaptersDialog = ref(false)

async function fetchDetail() {
  loading.value = true
  try {
    const res = await request.get(`/manga/detail/${route.params.id}`)
    manga.value = res.data
    if (userStore.isLoggedIn) {
      checkCollect()
      checkUserScore()
    }
  } catch (e) {
    ElMessage.error('获取漫画详情失败')
  } finally {
    loading.value = false
  }
}

async function checkCollect() {
  try {
    const res = await request.get('/interaction/collect/status', {
      params: { mangaId: route.params.id }
    })
    isCollected.value = res.data?.collected || false
  } catch (e) {
    ElMessage.error('获取收藏状态失败')
  }
}

async function checkUserScore() {
  try {
    const res = await request.get('/interaction/score/current', {
      params: { mangaId: route.params.id }
    })
    if (res.data?.score) {
      scoreValue.value = res.data.score
    }
  } catch (e) {
    ElMessage.error('获取评分状态失败')
  }
}

async function handleRead() {
  try {
    const res = await request.get(`/manga/${route.params.id}/chapters`)
    chapters.value = res.data || []
    showChaptersDialog.value = true
  } catch (e) {
    ElMessage.error('获取章节失败')
  }
}

async function selectChapter(chapter) {
  try {
    await request.post(`/manga/${route.params.id}/chapters/${chapter.id}/read`)
    ElMessage.success(`正在阅读: ${chapter.title}`)
  } catch (e) {
    ElMessage.error('阅读章节失败')
  }
}

async function handleCollect() {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录后再操作')
    return
  }
  const action = isCollected.value ? 'remove' : 'add'
  isCollected.value = !isCollected.value
  try {
    if (action === 'add') {
      await request.post('/interaction/collect', { mangaId: route.params.id })
      ElMessage.success('已加入书架')
    } else {
      await request.delete('/interaction/collect', { params: { mangaId: route.params.id } })
      ElMessage.success('已从书架移除')
    }
  } catch (e) {
    isCollected.value = !isCollected.value
    ElMessage.error('操作失败，请稍后重试')
  }
}

function submitScore() {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录后再评分')
    return
  }
  showScoreDialog.value = true
}

async function confirmScore() {
  showScoreDialog.value = false
  try {
    await request.post('/interaction/score', {
      mangaId: route.params.id,
      score: scoreValue.value
    })
    ElMessage.success('评分成功！您的偏好已记录，底层算法矩阵将在下一周期更新。')
  } catch (e) {
    ElMessage.error('评分提交失败，请稍后重试')
  }
}

onMounted(() => {
  fetchDetail()
})
</script>

<style scoped>
.manga-detail {
  max-width: var(--max-width);
  margin: 0 auto;
  padding: var(--spacing-xl);
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  margin-bottom: var(--spacing-xl);
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
}

.breadcrumb a {
  color: var(--color-text-secondary);
  text-decoration: none;
  transition: color var(--transition-fast);
}

.breadcrumb a:hover {
  color: var(--color-primary);
}

.breadcrumb .separator {
  color: var(--color-border);
}

.breadcrumb .current {
  color: var(--color-text-primary);
  font-weight: var(--font-weight-medium);
}

.detail-hero {
  display: flex;
  gap: 40px;
  background: var(--color-bg-white);
  border-radius: var(--radius-lg);
  padding: 30px;
  box-shadow: var(--shadow-md);
  margin-bottom: var(--spacing-xl);
}

.hero-cover {
  flex-shrink: 0;
}

.cover-img {
  width: 280px;
  height: 380px;
  object-fit: cover;
  border-radius: var(--radius-lg);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.hero-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.manga-title {
  font-size: var(--font-size-hero);
  font-weight: var(--font-weight-bold);
  color: var(--color-text-primary);
  margin-bottom: var(--spacing-md);
}

.info-tags {
  display: flex;
  gap: 8px;
  margin-bottom: 20px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--spacing-md);
  margin-bottom: 20px;
  background: var(--color-bg-secondary);
  border-radius: var(--radius-md);
  padding: var(--spacing-md);
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-label {
  font-size: var(--font-size-xs);
  color: var(--color-text-secondary);
}

.info-value {
  font-size: var(--font-size-md);
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
}

.score-highlight {
  color: var(--color-danger);
}

.manga-desc {
  font-size: var(--font-size-sm);
  color: var(--color-text-regular);
  line-height: 1.8;
  margin-bottom: var(--spacing-xl);
  flex: 1;
}

.action-buttons {
  display: flex;
  gap: var(--spacing-md);
}

.action-buttons .el-button {
  padding: 20px 32px;
  font-size: var(--font-size-md);
}

.collect-btn.is-collected {
  animation: heartbeat 0.3s ease;
}

@keyframes heartbeat {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.2); }
}

.detail-section {
  background: var(--color-bg-white);
  border-radius: var(--radius-lg);
  padding: var(--spacing-xl);
  margin-bottom: var(--spacing-xl);
}

.rating-section .section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: var(--font-size-md);
  color: var(--color-text-primary);
  margin-bottom: 20px;
}

.rating-card {
  text-align: center;
  padding: 20px;
}

.rating-hint {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  margin-bottom: 20px;
}

.star-rating {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

.submit-score-btn {
  width: 200px;
}

.score-confirm {
  text-align: center;
}

.confirm-text {
  font-size: var(--font-size-md);
  color: var(--color-text-primary);
  margin-bottom: var(--spacing-md);
}

.confirm-stars {
  display: flex;
  justify-content: center;
  margin-bottom: var(--spacing-md);
}

.confirm-tip {
  font-size: 13px;
  color: var(--color-text-secondary);
}

.chapters-list {
  max-height: 400px;
  overflow-y: auto;
}

.chapter-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid var(--color-border-light);
  cursor: pointer;
  transition: background var(--transition-fast);
  background: none;
  border: none;
  width: 100%;
  text-align: left;
  font: inherit;
  color: inherit;
}

.chapter-item:hover {
  background: var(--color-bg-secondary);
}

.chapter-item:last-child {
  border-bottom: none;
}

.chapter-title {
  font-size: var(--font-size-sm);
  color: var(--color-text-primary);
}

.empty-chapters {
  text-align: center;
  padding: 40px;
  color: var(--color-text-secondary);
}

@media (max-width: 768px) {
  .detail-hero {
    flex-direction: column;
    gap: 20px;
    padding: 20px;
  }

  .cover-img {
    width: 200px;
    height: 280px;
    margin: 0 auto;
  }

  .manga-title {
    font-size: var(--font-size-title);
  }

  .info-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .action-buttons {
    flex-direction: column;
  }

  .action-buttons .el-button {
    width: 100%;
  }
}
</style>
