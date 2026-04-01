<template>
  <div class="home-view">
    <!-- 1. Hero Banner -->
    <section class="hero-banner">
      <div class="hero-content">
        <h1 class="hero-title">发现你的下一部心仪漫画</h1>
        <p class="hero-subtitle">
          基于 Item-CF 协同过滤算法，从数千部作品中为你精准推荐
        </p>
        <div class="hero-actions">
          <el-button type="primary" size="large" round @click="$router.push('/category')">
            开始探索
          </el-button>
          <el-button size="large" round class="ghost-btn" @click="$router.push('/recommend')">
            个性化推荐
          </el-button>
        </div>
      </div>
      <div class="hero-decoration">
        <div class="deco-circle deco-circle-1"></div>
        <div class="deco-circle deco-circle-2"></div>
        <div class="deco-circle deco-circle-3"></div>
      </div>
    </section>

    <!-- 2. 功能导航区 -->
    <section class="feature-nav">
      <div class="section-container">
        <h2 class="section-title">探索漫画世界</h2>
        <div class="feature-grid">
          <router-link to="/category" class="feature-card">
            <div class="feature-icon">
              <el-icon :size="36"><Grid /></el-icon>
            </div>
            <h3>分类浏览</h3>
            <p>按类型筛选，发现你喜欢的漫画风格</p>
            <span class="feature-arrow">&rarr;</span>
          </router-link>
          <router-link to="/leaderboard" class="feature-card">
            <div class="feature-icon">
              <el-icon :size="36"><Trophy /></el-icon>
            </div>
            <h3>排行榜</h3>
            <p>查看最受欢迎和高分佳作榜单</p>
            <span class="feature-arrow">&rarr;</span>
          </router-link>
          <router-link to="/recommend" class="feature-card">
            <div class="feature-icon">
              <el-icon :size="36"><MagicStick /></el-icon>
            </div>
            <h3>个性推荐</h3>
            <p>AI 算法为你量身定制推荐列表</p>
            <span class="feature-arrow">&rarr;</span>
          </router-link>
          <router-link to="/forum" class="feature-card">
            <div class="feature-icon">
              <el-icon :size="36"><ChatDotRound /></el-icon>
            </div>
            <h3>交流论坛</h3>
            <p>与漫迷分享心得，讨论热门话题</p>
            <span class="feature-arrow">&rarr;</span>
          </router-link>
        </div>
      </div>
    </section>

    <!-- 3. 热门漫画预览 -->
    <section class="hot-manga">
      <div class="section-container">
        <div class="section-header">
          <h2 class="section-title">热门漫画</h2>
          <router-link to="/category" class="view-more">查看更多 &rarr;</router-link>
        </div>
        <div v-if="hotLoading">
          <PageSkeleton type="card-grid" :count="6" />
        </div>
        <div v-else>
          <div v-if="hotList.length" class="manga-grid">
            <MangaCard v-for="item in hotList" :key="item.id" :manga="item" />
          </div>
          <EmptyState v-else title="暂无数据" />
        </div>
      </div>
    </section>

    <!-- 4. 排行榜速览 -->
    <section class="leaderboard-preview">
      <div class="section-container">
        <div class="section-header">
          <h2 class="section-title">排行榜</h2>
          <router-link to="/leaderboard" class="view-more">查看完整榜单 &rarr;</router-link>
        </div>
        <div v-loading="rankLoading" class="rank-columns">
          <div class="rank-column">
            <h3 class="rank-column-title">人气榜</h3>
            <div class="rank-list">
              <div v-for="(item, index) in hotRank" :key="item.id"
                   class="rank-item" @click="goDetail(item.id)">
                <span class="rank-num" :class="'rank-' + (index + 1)">{{ index + 1 }}</span>
                <img :src="item.coverUrl || '/placeholder.png'" class="rank-cover" loading="lazy" />
                <div class="rank-info">
                  <span class="rank-title">{{ item.title }}</span>
                </div>
                <span class="rank-value">{{ item.clickCount }} 点击</span>
              </div>
              <div v-if="!hotRank.length && !rankLoading" class="rank-empty">暂无数据</div>
            </div>
          </div>
          <div class="rank-column">
            <h3 class="rank-column-title">高分榜</h3>
            <div class="rank-list">
              <div v-for="(item, index) in scoreRank" :key="item.id"
                   class="rank-item" @click="goDetail(item.id)">
                <span class="rank-num" :class="'rank-' + (index + 1)">{{ index + 1 }}</span>
                <img :src="item.coverUrl || '/placeholder.png'" class="rank-cover" loading="lazy" />
                <div class="rank-info">
                  <span class="rank-title">{{ item.title }}</span>
                </div>
                <span class="rank-value">{{ item.avgScore?.toFixed(1) }} 分</span>
              </div>
              <div v-if="!scoreRank.length && !rankLoading" class="rank-empty">暂无数据</div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 5. 社区动态 -->
    <section class="forum-preview">
      <div class="section-container">
        <div class="section-header">
          <h2 class="section-title">社区动态</h2>
          <router-link to="/forum" class="view-more">进入论坛 &rarr;</router-link>
        </div>
        <div v-loading="forumLoading">
          <div v-if="forumList.length" class="forum-list">
            <div v-for="item in forumList" :key="item.id"
                 class="forum-item" @click="$router.push({ name: 'ForumDetail', params: { id: item.id } })">
              <h4>{{ item.title }}</h4>
              <div class="forum-meta">
                <span>{{ item.username }}</span>
                <span>{{ formatRelativeTime(item.createTime) }}</span>
                <span>{{ item.viewCount || 0 }} 浏览</span>
              </div>
            </div>
          </div>
          <EmptyState v-else title="暂无帖子" />
        </div>
      </div>
    </section>

    <!-- 6. 登录引导（未登录时显示） -->
      <LoginGuide v-if="!userStore.isLoggedIn" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Grid, Trophy, MagicStick, ChatDotRound } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import request from '@/utils/request'
import MangaCard from '@/components/MangaCard.vue'
import PageSkeleton from '@/components/PageSkeleton.vue'
import EmptyState from '@/components/EmptyState.vue'
import LoginGuide from '@/components/LoginGuide.vue'
import { formatRelativeTime } from '@/utils/helpers'

const router = useRouter()
const userStore = useUserStore()

// 热门漫画数据
const hotLoading = ref(false)
const hotList = ref([])

// 排行榜数据
const rankLoading = ref(false)
const hotRank = ref([])
const scoreRank = ref([])

// 论坛数据
const forumLoading = ref(false)
const forumList = ref([])

async function fetchHotManga() {
  hotLoading.value = true
  try {
    const res = await request.get('/manga/page', {
      params: { current: 1, size: 6 }
    })
    hotList.value = res.data.records || []
  } catch (e) {
  } finally {
    hotLoading.value = false
  }
}

async function fetchLeaderboard() {
  rankLoading.value = true
  try {
    const [hotRes, scoreRes] = await Promise.all([
      request.get('/manga/leaderboard', { params: { type: 'hot' } }),
      request.get('/manga/leaderboard', { params: { type: 'score' } })
    ])
    hotRank.value = (hotRes.data || []).slice(0, 5)
    scoreRank.value = (scoreRes.data || []).slice(0, 5)
  } catch (e) {
  } finally {
    rankLoading.value = false
  }
}

async function fetchForumPosts() {
  forumLoading.value = true
  try {
    const res = await request.get('/forum/list', {
      params: { current: 1, size: 5 }
    })
    forumList.value = res.data.records || []
  } catch (e) {
  } finally {
    forumLoading.value = false
  }
}

function goDetail(id) {
  router.push({ name: 'MangaDetail', params: { id } })
}

onMounted(() => {
  fetchHotManga()
  fetchLeaderboard()
  fetchForumPosts()
})
</script>

<style scoped>
/* ========== Hero Banner ========== */
.hero-banner {
  position: relative;
  background: linear-gradient(135deg, var(--color-primary) 0%, #667eea 50%, #764ba2 100%);
  color: white;
  padding: 80px 20px;
  text-align: center;
  overflow: hidden;
  min-height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.hero-content {
  position: relative;
  z-index: 2;
}

.hero-title {
  font-size: var(--font-size-hero);
  font-weight: var(--font-weight-bold);
  margin-bottom: var(--spacing-md);
  letter-spacing: 1px;
  text-shadow: 0 2px 12px rgba(0, 0, 0, 0.15);
}

.hero-subtitle {
  font-size: var(--font-size-lg);
  opacity: 0.92;
  margin-bottom: 36px;
  max-width: 560px;
  margin-left: auto;
  margin-right: auto;
  line-height: 1.6;
}

.hero-actions {
  display: flex;
  gap: var(--spacing-md);
  justify-content: center;
  flex-wrap: wrap;
}

.hero-actions .el-button {
  padding: 12px 32px;
  font-size: var(--font-size-md);
}

.ghost-btn {
  background: rgba(255, 255, 255, 0.15) !important;
  border: 2px solid rgba(255, 255, 255, 0.6) !important;
  color: white !important;
  backdrop-filter: blur(4px);
}

.ghost-btn:hover {
  background: rgba(255, 255, 255, 0.25) !important;
  border-color: white !important;
  color: white !important;
}

/* 装饰圆圈 */
.hero-decoration {
  position: absolute;
  inset: 0;
  pointer-events: none;
  overflow: hidden;
}

.deco-circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.06);
}

.deco-circle-1 {
  width: 320px;
  height: 320px;
  top: -100px;
  right: -80px;
  animation: float 8s ease-in-out infinite;
}

.deco-circle-2 {
  width: 220px;
  height: 220px;
  bottom: -60px;
  left: -50px;
  animation: float 10s ease-in-out infinite reverse;
}

.deco-circle-3 {
  width: 140px;
  height: 140px;
  top: 40%;
  left: 8%;
  animation: float 12s ease-in-out infinite 2s;
}

@keyframes float {
  0%, 100% { transform: translateY(0) scale(1); }
  50% { transform: translateY(-20px) scale(1.05); }
}

/* ========== 通用 Section ========== */
.section-container {
  max-width: var(--max-width);
  margin: 0 auto;
  padding: 0 var(--spacing-xl);
}

.section-title {
  font-size: var(--font-size-title);
  font-weight: var(--font-weight-bold);
  color: var(--color-text-primary);
  margin-bottom: var(--spacing-xl);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-xl);
}

.section-header .section-title {
  margin-bottom: 0;
}

.view-more {
  color: var(--color-primary);
  font-size: var(--font-size-sm);
  text-decoration: none;
  transition: opacity var(--transition-fast);
}

.view-more:hover {
  opacity: 0.8;
  text-decoration: underline;
}

/* ========== 功能导航区 ========== */
.feature-nav {
  padding: 64px 0;
}

.feature-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--spacing-xl);
}

.feature-card {
  background: var(--color-bg-white);
  border-radius: var(--radius-lg);
  padding: 36px 24px 28px;
  text-align: center;
  text-decoration: none;
  color: var(--color-text-primary);
  transition: transform var(--transition-normal), box-shadow var(--transition-normal);
  box-shadow: var(--shadow-sm);
  position: relative;
  overflow: hidden;
  animation: fadeInUp 0.5s ease forwards;
  opacity: 0;
}

.feature-card:nth-child(1) { animation-delay: 0.1s; }
.feature-card:nth-child(2) { animation-delay: 0.2s; }
.feature-card:nth-child(3) { animation-delay: 0.3s; }
.feature-card:nth-child(4) { animation-delay: 0.4s; }

@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.feature-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: var(--color-primary);
  transform: scaleX(0);
  transition: transform var(--transition-normal);
}

.feature-card:hover {
  transform: translateY(-6px);
  box-shadow: var(--shadow-lg);
}

.feature-card:hover::before {
  transform: scaleX(1);
}

.feature-icon {
  color: var(--color-primary);
  margin-bottom: var(--spacing-md);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 64px;
  height: 64px;
  border-radius: 16px;
  background: var(--color-primary-light);
  transition: background var(--transition-normal);
}

.feature-card:hover .feature-icon {
  background: var(--color-primary);
  color: white;
}

.feature-card h3 {
  font-size: var(--font-size-md);
  font-weight: var(--font-weight-semibold);
  margin-bottom: 8px;
}

.feature-card p {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  line-height: 1.5;
}

.feature-arrow {
  position: absolute;
  top: 20px;
  right: 20px;
  color: var(--color-border);
  font-size: var(--font-size-md);
  transition: color var(--transition-normal), transform var(--transition-normal);
}

.feature-card:hover .feature-arrow {
  color: var(--color-primary);
  transform: translateX(4px);
}

/* ========== 热门漫画 ========== */
.hot-manga {
  padding: 64px 0;
  background: var(--color-bg-white);
}

.manga-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--spacing-xl);
}

/* ========== 排行榜速览 ========== */
.leaderboard-preview {
  padding: 64px 0;
}

.rank-columns {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--spacing-xl);
}

.rank-column {
  background: var(--color-bg-white);
  border-radius: var(--radius-lg);
  padding: var(--spacing-xl);
  box-shadow: var(--shadow-sm);
}

.rank-column-title {
  font-size: var(--font-size-md);
  font-weight: var(--font-weight-semibold);
  margin-bottom: var(--spacing-md);
  padding-bottom: var(--spacing-md);
  border-bottom: 2px solid var(--color-primary);
  color: var(--color-text-primary);
}

.rank-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.rank-item {
  display: flex;
  align-items: center;
  padding: 10px 8px;
  cursor: pointer;
  transition: background var(--transition-fast);
  border-radius: var(--radius-md);
}

.rank-item:hover {
  background: var(--color-bg-secondary);
}

.rank-num {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: var(--font-weight-bold);
  color: var(--color-text-secondary);
  background: var(--color-bg-secondary);
  margin-right: var(--spacing-md);
  flex-shrink: 0;
}

.rank-1 {
  background: linear-gradient(135deg, #ffd700, #ffb347);
  color: white;
}

.rank-2 {
  background: linear-gradient(135deg, #c0c0c0, #a8a8a8);
  color: white;
}

.rank-3 {
  background: linear-gradient(135deg, #cd7f32, #b87333);
  color: white;
}

.rank-cover {
  width: 40px;
  height: 54px;
  object-fit: cover;
  border-radius: var(--radius-sm);
  margin-right: var(--spacing-md);
  flex-shrink: 0;
  background: var(--color-bg-secondary);
}

.rank-info {
  flex: 1;
  overflow: hidden;
}

.rank-title {
  font-size: var(--font-size-sm);
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: var(--color-text-primary);
}

.rank-value {
  font-size: 13px;
  color: var(--color-danger);
  font-weight: var(--font-weight-semibold);
  flex-shrink: 0;
  margin-left: 8px;
  white-space: nowrap;
}

.rank-empty {
  text-align: center;
  padding: var(--spacing-xl);
  color: var(--color-text-secondary);
  font-size: var(--font-size-sm);
}

/* ========== 社区动态 ========== */
.forum-preview {
  padding: 64px 0;
  background: var(--color-bg-white);
}

.forum-list {
  background: var(--color-bg-white);
  border-radius: var(--radius-lg);
  overflow: hidden;
  box-shadow: var(--shadow-sm);
}

.forum-item {
  padding: var(--spacing-md) var(--spacing-xl);
  cursor: pointer;
  border-bottom: 1px solid var(--color-border);
  transition: background var(--transition-fast);
}

.forum-item:last-child {
  border-bottom: none;
}

.forum-item:hover {
  background: var(--color-bg-secondary);
}

.forum-item h4 {
  font-size: var(--font-size-md);
  font-weight: var(--font-weight-medium);
  margin-bottom: 8px;
  color: var(--color-text-primary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.forum-meta {
  display: flex;
  gap: var(--spacing-md);
  font-size: 13px;
  color: var(--color-text-secondary);
}

/* ========== 响应式 ========== */
@media (max-width: 1024px) {
  .feature-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .manga-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .hero-banner {
    padding: 60px 20px;
    min-height: 300px;
  }

  .hero-title {
    font-size: var(--font-size-title);
  }

  .hero-subtitle {
    font-size: var(--font-size-base);
    margin-bottom: 28px;
  }

  .hero-actions .el-button {
    padding: 10px 24px;
    font-size: var(--font-size-base);
  }

  .rank-columns {
    grid-template-columns: 1fr;
  }

  .section-title {
    font-size: var(--font-size-xl);
  }
}

@media (max-width: 640px) {
  .feature-grid {
    grid-template-columns: 1fr;
  }

  .manga-grid {
    grid-template-columns: 1fr;
  }

  .hero-title {
    font-size: var(--font-size-xl);
  }

  .hero-subtitle {
    font-size: var(--font-size-sm);
  }

  .hero-actions {
    flex-direction: column;
    align-items: center;
  }

  .hero-actions .el-button {
    width: 200px;
  }

  .forum-meta {
    flex-direction: column;
    gap: 4px;
  }
}
</style>
