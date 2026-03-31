<template>
  <div class="recommend-view">
    <h1>个性化推荐</h1>

    <div v-if="!userStore.isLoggedIn" class="login-tip">
      <p>登录后开启个性化推荐功能</p>
    </div>

    <div v-else>
      <div v-if="loading" class="loading">加载中...</div>
      <template v-else>
        <div v-if="hubData.hotZone?.length" class="section">
          <h2>🔥 A区 · 热点推荐</h2>
          <div class="manga-grid">
            <div v-for="item in hubData.hotZone" :key="item.id" class="manga-card" @click="goDetail(item.id)">
              <img :src="item.coverUrl || '/placeholder.png'" class="manga-cover" />
              <div class="manga-info">
                <h3 class="manga-title">{{ item.title }}</h3>
                <p class="manga-author">{{ item.author }}</p>
              </div>
            </div>
          </div>
        </div>

        <div v-if="hubData.categoryZone?.length" class="section">
          <h2>📚 B区 · 同类推荐</h2>
          <div class="manga-grid">
            <div v-for="item in hubData.categoryZone" :key="item.id" class="manga-card" @click="goDetail(item.id)">
              <img :src="item.coverUrl || '/placeholder.png'" class="manga-cover" />
              <div class="manga-info">
                <h3 class="manga-title">{{ item.title }}</h3>
                <p class="manga-author">{{ item.author }}</p>
              </div>
            </div>
          </div>
        </div>

        <div v-if="hubData.cfZone?.length" class="section">
          <h2>⭐ C区 · 猜你喜欢 (Item-CF)</h2>
          <div class="manga-grid">
            <div v-for="item in hubData.cfZone" :key="item.id" class="manga-card" @click="goDetail(item.id)">
              <img :src="item.coverUrl || '/placeholder.png'" class="manga-cover" />
              <div class="manga-info">
                <h3 class="manga-title">{{ item.title }}</h3>
                <p class="manga-author">{{ item.author }}</p>
              </div>
            </div>
          </div>
        </div>

        <div v-if="hubData.scoreZone?.length" class="section">
          <h2>🏆 D区 · 高分佳作</h2>
          <div class="manga-grid">
            <div v-for="item in hubData.scoreZone" :key="item.id" class="manga-card" @click="goDetail(item.id)">
              <img :src="item.coverUrl || '/placeholder.png'" class="manga-cover" />
              <div class="manga-info">
                <h3 class="manga-title">{{ item.title }}</h3>
                <p class="manga-author">{{ item.author }}</p>
              </div>
            </div>
          </div>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import request from '@/utils/request'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const hubData = ref({})

async function fetchRecommendHub() {
  loading.value = true
  try {
    const res = await request.get('/recommend/hub')
    hubData.value = res.data || {}
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

function goDetail(id) {
  router.push({ name: 'MangaDetail', params: { id } })
}

onMounted(() => {
  if (userStore.isLoggedIn) {
    fetchRecommendHub()
  }
})
</script>

<style scoped>
.recommend-view {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

h1 {
  font-size: 28px;
  margin-bottom: 24px;
}

.login-tip {
  text-align: center;
  padding: 80px;
  background: white;
  border-radius: 12px;
  color: #999;
}

.loading {
  text-align: center;
  padding: 60px;
  color: #999;
}

.section {
  margin-bottom: 40px;
}

.section h2 {
  font-size: 20px;
  margin-bottom: 16px;
  color: #333;
}

.manga-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.manga-card {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.3s;
}

.manga-card:hover {
  transform: translateY(-4px);
}

.manga-cover {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.manga-info {
  padding: 10px;
}

.manga-title {
  font-size: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.manga-author {
  font-size: 12px;
  color: #666;
  margin-top: 4px;
}
</style>
