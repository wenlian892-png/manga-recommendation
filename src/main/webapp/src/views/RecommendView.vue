<template>
  <div class="recommend-view">
    <div class="page-header">
      <h1>个性化推荐</h1>
      <p class="recommend-desc">基于 Item-CF 协同过滤算法，为你精选好漫画</p>
    </div>

    <div v-if="loading">
      <PageSkeleton type="card-grid" :count="6" />
    </div>
    <template v-else>
      <!-- A区：热点推荐（所有用户可见） -->
      <div v-if="hubData.hotZone?.length" class="section">
        <div class="section-header">
          <h2>🔥 热点推荐</h2>
          <el-button :icon="Refresh" circle size="small" @click="fetchRecommendHub" :loading="loading" />
        </div>
        <div class="manga-grid">
          <MangaCard v-for="item in hubData.hotZone" :key="item.id" :manga="item" />
        </div>
      </div>

      <!-- 登录引导（未登录时，在热点推荐之后显示） -->
      <div v-if="!userStore.isLoggedIn" class="login-guide-wrapper">
        <LoginGuide
          title="登录解锁更多推荐"
          description="登录后可获得同类推荐和 Item-CF 个性化推荐"
        />
      </div>

      <!-- B区：同类推荐（需登录） -->
      <div v-if="userStore.isLoggedIn && hubData.categoryZone?.length" class="section">
        <div class="section-header">
          <h2>📚 同类推荐</h2>
          <el-button :icon="Refresh" circle size="small" @click="fetchRecommendHub" :loading="loading" />
        </div>
        <div class="manga-grid">
          <MangaCard v-for="item in hubData.categoryZone" :key="item.id" :manga="item" />
        </div>
      </div>

      <!-- C区：猜你喜欢（需登录） -->
      <div v-if="userStore.isLoggedIn && hubData.cfZone?.length" class="section">
        <div class="section-header">
          <h2>⭐ 猜你喜欢 (Item-CF)</h2>
          <el-button :icon="Refresh" circle size="small" @click="fetchRecommendHub" :loading="loading" />
        </div>
        <div class="manga-grid">
          <MangaCard v-for="item in hubData.cfZone" :key="item.id" :manga="item">
            <template #extra>
              <p class="recommend-reason">💡 因为您高分评价了相似作品，所以推荐</p>
            </template>
          </MangaCard>
        </div>
      </div>

      <!-- D区：高分佳作（所有用户可见） -->
      <div v-if="hubData.scoreZone?.length" class="section">
        <div class="section-header">
          <h2>🏆 高分佳作</h2>
          <el-button :icon="Refresh" circle size="small" @click="fetchRecommendHub" :loading="loading" />
        </div>
        <div class="manga-grid">
          <MangaCard v-for="item in hubData.scoreZone" :key="item.id" :manga="item" />
        </div>
      </div>

      <EmptyState
        v-if="!hubData.hotZone?.length && !hubData.categoryZone?.length && !hubData.cfZone?.length && !hubData.scoreZone?.length"
        title="暂无推荐数据"
      />
    </template>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import request from '@/utils/request'
import MangaCard from '@/components/MangaCard.vue'
import LoginGuide from '@/components/LoginGuide.vue'
import PageSkeleton from '@/components/PageSkeleton.vue'
import EmptyState from '@/components/EmptyState.vue'
import { Refresh } from '@element-plus/icons-vue'

const userStore = useUserStore()
const loading = ref(false)
const hubData = ref({})

async function fetchRecommendHub() {
  loading.value = true
  try {
    const params = {}
    if (userStore.isLoggedIn && userStore.currentUser?.id) {
      params.userId = userStore.currentUser.id
    }
    const res = await request.get('/recommend/hub', { params })
    hubData.value = res.data || {}
  } catch (e) {
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchRecommendHub()
})
</script>

<style scoped>
.recommend-view {
  max-width: var(--max-width);
  margin: 0 auto;
  padding: var(--page-padding);
}

.page-header {
  margin-bottom: var(--spacing-xl);
}

.page-header h1 {
  font-size: var(--font-size-hero);
  font-weight: var(--font-weight-bold);
  color: var(--color-text-primary);
  margin-bottom: var(--spacing-sm);
}

.recommend-desc {
  font-size: var(--font-size-lg);
  color: var(--color-text-secondary);
}

.section {
  margin-bottom: var(--section-gap);
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--spacing-lg);
}

.section-header h2 {
  font-size: var(--font-size-xxl);
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
}

.manga-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--spacing-md);
}

.recommend-reason {
  font-size: var(--font-size-xs);
  color: var(--color-warning);
  margin-top: var(--spacing-xs);
  font-weight: var(--font-weight-medium);
}

.login-guide-wrapper {
  margin: var(--spacing-xl) 0;
}

/* 响应式 */
@media (max-width: 1024px) {
  .manga-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 640px) {
  .manga-grid {
    grid-template-columns: 1fr;
  }

  .page-header h1 {
    font-size: var(--font-size-xxl);
  }
}
</style>
