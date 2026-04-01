<template>
  <div class="home-view">
    <div class="banner">
      <h1>欢迎来到漫画推荐系统</h1>
      <p>基于 Item-CF 协同过滤算法，为您推荐最喜欢的漫画</p>
    </div>

    <div v-if="loading" class="loading">加载中...</div>
    <template v-else>
      <div class="section">
        <h2>🔥 A区 · 大家都在看</h2>
        <div v-if="!hubData.hotZone || hubData.hotZone.length === 0" class="empty">暂无数据</div>
        <div v-else class="manga-grid">
          <MangaCard v-for="item in hubData.hotZone" :key="item.id" :manga="item" />
        </div>
      </div>

      <div class="section">
        <h2>📚 B区 · 同类作品延伸</h2>
        <div v-if="!userStore.isLoggedIn" class="login-tip">
          <p>登录后解锁专属同类推荐</p>
        </div>
        <div v-else-if="!hubData.categoryZone || hubData.categoryZone.length === 0" class="empty">暂无数据</div>
        <div v-else class="manga-grid">
          <MangaCard v-for="item in hubData.categoryZone" :key="item.id" :manga="item" />
        </div>
      </div>

      <div class="section">
        <h2>✨ C区 · 猜你喜欢</h2>
        <div v-if="!userStore.isLoggedIn" class="login-tip">
          <p>登录后解锁 Item-CF 个性化推荐</p>
        </div>
        <div v-else class="manga-grid">
          <MangaCard 
            v-for="item in (hubData.cfZone && hubData.cfZone.length > 0 ? hubData.cfZone : hubData.hotZone)" 
            :key="item.id" 
            :manga="{
              ...item,
              tip: hubData.cfZone && hubData.cfZone.length > 0 ? '💡 因为您高分评价了相似作品，所以推荐' : '🔥 热门推荐'
            }" 
          />
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { recommendApi } from '@/api/recommend'
import { ElMessage } from 'element-plus'
import MangaCard from '@/components/MangaCard.vue'

const userStore = useUserStore()
const loading = ref(false)
const hubData = ref({})

async function fetchRecommendHub() {
  loading.value = true
  try {
    const res = await recommendApi.getRecommendHub({
      userId: userStore.isLoggedIn && userStore.currentUser ? userStore.currentUser.id : undefined
    })
    hubData.value = res.data || {}
  } catch (e) {
    console.error('获取推荐数据失败:', e)
    ElMessage.error('获取推荐数据失败，请稍后重试')
    hubData.value = {
      hotZone: [],
      categoryZone: [],
      cfZone: []
    }
  } finally {
    loading.value = false
  }
}



onMounted(() => {
  fetchRecommendHub()
})
</script>

<style scoped>
.home-view {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 60px 40px;
  border-radius: 12px;
  text-align: center;
  margin-bottom: 40px;
}

.banner h1 {
  font-size: 36px;
  margin-bottom: 16px;
}

.banner p {
  font-size: 18px;
  opacity: 0.9;
}

.section {
  margin-bottom: 48px;
}

.section h2 {
  font-size: 24px;
  margin-bottom: 20px;
  color: #333;
}

.loading, .empty, .login-tip {
  text-align: center;
  padding: 40px;
  color: #999;
  background: white;
  border-radius: 12px;
}

.login-tip {
  font-size: 16px;
}

.manga-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

@media (max-width: 768px) {
  .manga-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .banner h1 {
    font-size: 28px;
  }
  
  .banner p {
    font-size: 16px;
  }
  
  .section h2 {
    font-size: 20px;
  }
}

.manga-card {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
}

.manga-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.15);
}

.manga-cover {
  width: 100%;
  height: 240px;
  object-fit: cover;
}

.manga-info {
  padding: 12px;
}

.manga-title {
  font-size: 16px;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.manga-author {
  font-size: 13px;
  color: #666;
  margin-bottom: 4px;
}

.manga-tip {
  font-size: 12px;
  color: #e6a23c;
  margin-top: 4px;
  font-weight: 500;
}
</style>
