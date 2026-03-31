<template>
  <div class="manga-detail">
    <div v-if="loading" class="loading">
      <el-icon class="is-loading"><Loading /></el-icon>
      加载中...
    </div>
    <div v-else-if="!manga" class="empty">漫画不存在</div>
    <template v-else>
      <div class="detail-hero">
        <div class="hero-cover">
          <img :src="manga.coverUrl || '/placeholder.png'" class="cover-img" />
        </div>
        <div class="hero-info">
          <h1 class="manga-title">{{ manga.title }}</h1>
          <div class="info-tags">
            <el-tag v-if="manga.category" size="small">{{ manga.category }}</el-tag>
            <el-tag :type="manga.status === 0 ? 'success' : 'warning'" size="small">
              {{ manga.status === 0 ? '连载中' : '已完结' }}
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
            <el-button size="large" :type="isCollected ? 'info' : 'default'" @click="handleCollect">
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
        <div v-else class="chapter-item" v-for="chapter in chapters" :key="chapter.id" @click="selectChapter(chapter)">
          <span class="chapter-title">{{ chapter.title }}</span>
          <el-button type="primary" size="small">阅读</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Loading, Reading, Collection, Star } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { useUserStore } from '@/stores/user'

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
const selectedChapter = ref(null)

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
    console.error(e)
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
  selectedChapter.value = chapter
  try {
    await request.post(`/manga/${route.params.id}/chapters/${chapter.id}/read`)
    ElMessage.success(`正在阅读: ${chapter.title}`)
  } catch (e) {
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
  }
}

onMounted(() => {
  fetchDetail()
})
</script>

<style scoped>
.manga-detail {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.loading, .empty {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 100px;
  color: #999;
  font-size: 16px;
}

.detail-hero {
  display: flex;
  gap: 40px;
  background: white;
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  margin-bottom: 24px;
}

.hero-cover {
  flex-shrink: 0;
}

.cover-img {
  width: 280px;
  height: 380px;
  object-fit: cover;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.hero-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.manga-title {
  font-size: 32px;
  font-weight: bold;
  color: #333;
  margin-bottom: 12px;
}

.info-tags {
  display: flex;
  gap: 8px;
  margin-bottom: 20px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-bottom: 20px;
  background: #f9f9f9;
  border-radius: 10px;
  padding: 16px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-label {
  font-size: 12px;
  color: #999;
}

.info-value {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.score-highlight {
  color: #f56c6c;
}

.manga-desc {
  font-size: 14px;
  color: #666;
  line-height: 1.8;
  margin-bottom: 24px;
  flex: 1;
}

.action-buttons {
  display: flex;
  gap: 12px;
}

.action-buttons .el-button {
  padding: 20px 32px;
  font-size: 16px;
}

.detail-section {
  background: white;
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 24px;
}

.rating-section .section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  color: #333;
  margin-bottom: 20px;
}

.rating-card {
  text-align: center;
  padding: 20px;
}

.rating-hint {
  font-size: 14px;
  color: #999;
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
  font-size: 16px;
  color: #333;
  margin-bottom: 16px;
}

.confirm-stars {
  display: flex;
  justify-content: center;
  margin-bottom: 16px;
}

.confirm-tip {
  font-size: 13px;
  color: #999;
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
  border-bottom: 1px solid #eee;
  cursor: pointer;
  transition: background 0.2s;
}

.chapter-item:hover {
  background: #f5f5f5;
}

.chapter-item:last-child {
  border-bottom: none;
}

.chapter-title {
  font-size: 14px;
  color: #333;
}

.empty-chapters {
  text-align: center;
  padding: 40px;
  color: #999;
}
</style>
