<template>
  <div class="leaderboard-view">
    <h1>排行榜</h1>
    <div class="tabs">
      <el-radio-group v-model="activeTab">
        <el-radio-button label="hot">全站人气榜</el-radio-button>
        <el-radio-button label="score">高分神作榜</el-radio-button>
      </el-radio-group>
    </div>

    <div v-if="loading" class="loading">加载中...</div>
    <div v-else-if="list.length === 0" class="empty">暂无数据</div>
    <div v-else class="rank-list">
      <div v-for="(item, index) in list" :key="item.id" class="rank-item" :class="{ 'top-item': index < 3 }" @click="goDetail(item.id)">
        <div class="rank-num" :class="{ 'top-1': index === 0, 'top-2': index === 1, 'top-3': index === 2 }">
          <span v-if="index < 3" class="medal">{{ ['🥇', '🥈', '🥉'][index] }}</span>
          <span v-else>{{ index + 1 }}</span>
        </div>
        <img :src="item.coverUrl || '/placeholder.png'" class="rank-cover" />
        <div class="rank-info">
          <h3>{{ item.title }}</h3>
          <p>{{ item.author }}</p>
        </div>
        <div class="rank-score">
          <span v-if="activeTab === 'score'">{{ item.avgScore?.toFixed(1) || '暂无' }}</span>
          <span v-else>{{ item.clickCount }} 点击</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/utils/request'

const router = useRouter()
const activeTab = ref('hot')
const loading = ref(false)
const list = ref([])

async function fetchList() {
  loading.value = true
  try {
    const res = await request.get('/manga/leaderboard', { params: { type: activeTab.value } })
    list.value = res.data || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

function goDetail(id) {
  router.push({ name: 'MangaDetail', params: { id } })
}

watch(activeTab, () => {
  fetchList()
})

onMounted(() => {
  fetchList()
})
</script>

<style scoped>
.leaderboard-view {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

h1 {
  font-size: 28px;
  margin-bottom: 24px;
}

.tabs {
  margin-bottom: 24px;
}

.loading, .empty {
  text-align: center;
  padding: 60px;
  color: #999;
}

.rank-list {
  background: white;
  border-radius: 12px;
  overflow: hidden;
}

.rank-item {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  cursor: pointer;
  transition: background 0.3s;
}

.rank-item:hover {
  background: #f5f5f5;
}

.rank-num {
  width: 50px;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: bold;
  color: #999;
  background: #f5f5f5;
  border-radius: 50%;
  margin-right: 16px;
  flex-shrink: 0;
}

.rank-num.top-1 {
  background: linear-gradient(135deg, #ffd700, #ffb347);
  color: white;
  font-size: 20px;
  box-shadow: 0 4px 12px rgba(255, 183, 71, 0.5);
}

.rank-num.top-2 {
  background: linear-gradient(135deg, #c0c0c0, #a0a0a0);
  color: white;
  font-size: 20px;
  box-shadow: 0 4px 12px rgba(160, 160, 160, 0.5);
}

.rank-num.top-3 {
  background: linear-gradient(135deg, #cd7f32, #b87333);
  color: white;
  font-size: 20px;
  box-shadow: 0 4px 12px rgba(184, 115, 51, 0.5);
}

.medal {
  font-size: 24px;
}

.top-item {
  background: linear-gradient(90deg, #fff9e6 0%, transparent 100%);
}

.rank-item.top-item {
  border-left: 3px solid #ffd700;
}

.rank-cover {
  width: 60px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
  margin-right: 16px;
}

.rank-info {
  flex: 1;
}

.rank-info h3 {
  font-size: 16px;
  margin-bottom: 4px;
}

.rank-info p {
  font-size: 13px;
  color: #666;
}

.rank-score {
  font-size: 16px;
  color: #f56c6c;
  font-weight: bold;
}
</style>
